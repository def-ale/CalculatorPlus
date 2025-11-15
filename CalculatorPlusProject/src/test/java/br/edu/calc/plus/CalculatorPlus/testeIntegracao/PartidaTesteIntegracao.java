package br.edu.calc.plus.CalculatorPlus.testeIntegracao;

import br.edu.calc.plus.domain.EOperator;
import br.edu.calc.plus.domain.Jogo;
import br.edu.calc.plus.domain.Partida;
import br.edu.calc.plus.domain.Usuario;
import br.edu.calc.plus.domain.dto.JogoListDTO;
import br.edu.calc.plus.repo.JogoRepo;
import br.edu.calc.plus.repo.PartidaRepo;
import br.edu.calc.plus.repo.UsuarioRepo;
import br.edu.calc.plus.service.PartidaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PartidaTesteIntegracao {

    @Autowired
    private PartidaService partidaService;

    @Autowired
    private UsuarioRepo usuarioRepo;

    @Autowired
    private PartidaRepo partidaRepo;

    @Autowired
    private JogoRepo jogoRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Usuario usuarioDeTeste;

    @BeforeEach
    void setup() {
        jogoRepo.deleteAll();
        partidaRepo.deleteAll();
        usuarioRepo.deleteAll();
        
        Usuario novoUsuario = new Usuario(null, "Usuário Teste", "teste", "teste@email.com",
                passwordEncoder.encode("123456"), "Cidade Teste", LocalDate.now().minusYears(20));
        usuarioDeTeste = usuarioRepo.save(novoUsuario);
    }

    @Test
    void deveProcessarPartidaCompletaESalvarResultadoCorretamente() throws Exception {
        System.out.println("--- Iniciando teste: deveProcessarPartidaCompletaESalvarResultadoCorretamente ---");
        System.out.println("Objetivo: Verificar se uma partida completa é processada e salva corretamente no banco de dados.");

        // 1. Iniciar a partida
        Partida partidaIniciada = partidaService.iniciarPartida(usuarioDeTeste.getId());
        assertNotNull(partidaIniciada);
        assertEquals(10, partidaIniciada.getJogoList().size());
        System.out.println("Partida iniciada com sucesso. ID: " + partidaIniciada.getId() + ". Total de jogos: " + partidaIniciada.getJogoList().size());

        // 2. Simular respostas (8 acertos, 2 erros)
        for (int i = 0; i < 10; i++) {
            var jogo = partidaIniciada.getJogoList().get(i);
            double resposta = jogo.getResultado(); // Resposta correta

            if (i >= 8) { // Simula 2 erros no final
                resposta++;
            }
            
            // O id do jogo não é usado pelo service, que usa a posição. Passamos 0.
            partidaService.savePartida(partidaIniciada.getId(), usuarioDeTeste.getId(), (i + 1), 0, resposta);
        }
        System.out.println("Respostas simuladas: 8 corretas e 2 incorretas.");

        // 3. Finalizar a partida
        LocalDateTime tempoInicio = LocalDateTime.now().minusSeconds(30); // Simula que começou há 30s
        partidaService.FinalizaPartida(partidaIniciada.getId(), usuarioDeTeste.getId(), tempoInicio);
        System.out.println("Partida finalizada. Tempo de início simulado: " + tempoInicio);

        // 4. Buscar a partida do banco de dados para verificação
        Partida partidaFinalizada = partidaRepo.findById(partidaIniciada.getId()).orElse(null);
        System.out.println("Buscando partida finalizada do banco de dados para verificação.");

        // 5. Assertivas
        assertNotNull(partidaFinalizada, "A partida finalizada não deve ser nula.");
        System.out.println("Assertiva: Partida finalizada não é nula.");

        assertEquals(8, partidaFinalizada.getAcertos(), "O número de acertos deve ser 8.");
        System.out.println("Assertiva: Acertos - Esperado: 8, Obtido: " + partidaFinalizada.getAcertos());

        assertEquals(2, partidaFinalizada.getErros(), "O número de erros deve ser 2.");
        System.out.println("Assertiva: Erros - Esperado: 2, Obtido: " + partidaFinalizada.getErros());

        assertTrue(partidaFinalizada.getTempo() > 0, "O tempo de jogo deve ser registrado.");
        System.out.println("Assertiva: Tempo de jogo - Esperado: > 0, Obtido: " + partidaFinalizada.getTempo());

        assertNotEquals(0, partidaFinalizada.getBonificacao(), "A bonificação final não deve ser zero.");
        System.out.println("Assertiva: Bonificação - Esperado: != 0, Obtido: " + partidaFinalizada.getBonificacao());
        System.out.println("Verificações concluídas: Acertos=" + partidaFinalizada.getAcertos() + ", Erros=" + partidaFinalizada.getErros() + ", Bonificação=" + partidaFinalizada.getBonificacao() + ", Tempo=" + partidaFinalizada.getTempo() + "s.");
        System.out.println("--- Teste 'deveProcessarPartidaCompletaESalvarResultadoCorretamente' finalizado com sucesso. ---");
    }

    @Test
    void naoDevePermitirIniciarSegundaPartidaNoMesmoDia() throws Exception {
        System.out.println("\n--- Iniciando teste: naoDevePermitirIniciarSegundaPartidaNoMesmoDia ---");
        System.out.println("Objetivo: Verificar se o sistema impede o início de uma segunda partida no mesmo dia.");

        // 1. Simula uma primeira partida completa no dia
        System.out.println("Simulando a finalização de uma primeira partida para o usuário de teste.");
        Partida primeiraPartida = partidaService.iniciarPartida(usuarioDeTeste.getId());
        partidaService.FinalizaPartida(primeiraPartida.getId(), usuarioDeTeste.getId(), LocalDateTime.now());
        System.out.println("Primeira partida (ID: " + primeiraPartida.getId() + ") finalizada com sucesso.");

        // 2. Verifica se o serviço identifica que o usuário já competiu hoje
        System.out.println("Verificando se o serviço reconhece que o usuário já competiu hoje.");
        boolean jaCompetiu = partidaService.userJaCompetiuHoje(usuarioDeTeste.getId());

        // 3. Assertiva
        assertTrue(jaCompetiu, "O serviço deve acusar que o usuário já competiu hoje.");
        System.out.println("Assertiva: Já competiu hoje - Esperado: true, Obtido: " + jaCompetiu);
        System.out.println("--- Teste 'naoDevePermitirIniciarSegundaPartidaNoMesmoDia' finalizado com sucesso. ---");
    }

    @Test
    void deveRetornarHistoricoDePartidasCorretamente() {
        System.out.println("\n--- Iniciando teste: deveRetornarHistoricoDePartidasCorretamente ---");
        System.out.println("Objetivo: Verificar se o serviço busca e mapeia o histórico de partidas corretamente.");

        // 1. Criar e salvar manualmente uma partida com jogos no banco de dados
        System.out.println("Criando e salvando uma partida de teste manualmente no banco de dados.");
        Partida partidaManual = new Partida();
        partidaManual.setUsuario(usuarioDeTeste);
        partidaManual.setData(LocalDateTime.now().minusDays(1)); // Partida de ontem
        partidaManual.setBonificacao(125);
        partidaManual.setTempo(45);
        
        List<Jogo> jogos = new ArrayList<>();
        // Adiciona 7 jogos corretos
        for(int i=0; i<7; i++) {
            Jogo jogo = new Jogo(null, 10, 5, EOperator.soma, 15, 15, 10); // resposta == resultado
            jogo.setPartida(partidaManual);
            jogos.add(jogo);
        }
        // Adiciona 3 jogos incorretos
        for(int i=0; i<3; i++) {
            Jogo jogo = new Jogo(null, 10, 5, EOperator.soma, 15, 0, 10); // resposta != resultado
            jogo.setPartida(partidaManual);
            jogos.add(jogo);
        }
        partidaManual.setJogoList(jogos);

        partidaRepo.save(partidaManual);
        System.out.println("Partida manual salva com ID: " + partidaManual.getId() + " e 10 jogos associados.");

        // 2. Chamar o serviço para buscar o histórico
        System.out.println("Buscando o histórico de jogos para o usuário de teste via serviço.");
        List<JogoListDTO> historico = partidaService.getMeusJogos(usuarioDeTeste.getId());

        // 3. Assertivas
        assertNotNull(historico, "A lista do histórico não deve ser nula.");
        System.out.println("Assertiva: Histórico não é nulo.");

        assertEquals(1, historico.size(), "O histórico deve conter exatamente uma partida.");
        System.out.println("Assertiva: Tamanho do histórico - Esperado: 1, Obtido: " + historico.size());

        JogoListDTO dto = historico.get(0);
        assertEquals(7, dto.getAcertos(), "O número de acertos no DTO está incorreto.");
        System.out.println("Assertiva: Acertos no DTO - Esperado: 7, Obtido: " + dto.getAcertos());

        assertEquals(3, dto.getErros(), "O número de erros no DTO está incorreto.");
        System.out.println("Assertiva: Erros no DTO - Esperado: 3, Obtido: " + dto.getErros());

        assertEquals(125, dto.getBonificacao(), "A bonificação no DTO está incorreta.");
        System.out.println("Assertiva: Bonificação no DTO - Esperado: 125, Obtido: " + dto.getBonificacao());

        assertEquals(45, dto.getTempo(), "O tempo no DTO está incorreto.");
        System.out.println("Assertiva: Tempo no DTO - Esperado: 45, Obtido: " + dto.getTempo());
        System.out.println("Assertiva: Os dados da partida (acertos, erros, bônus, tempo) no DTO correspondem aos dados salvos.");
        System.out.println("--- Teste 'deveRetornarHistoricoDePartidasCorretamente' finalizado com sucesso. ---");
    }
}
        