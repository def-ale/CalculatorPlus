package br.edu.calc.plus.CalculatorPlus.partidaTest;

import br.edu.calc.plus.domain.Jogo;
import br.edu.calc.plus.domain.Partida;
import br.edu.calc.plus.domain.EOperator;
import br.edu.calc.plus.domain.dto.JogoListDTO;
import br.edu.calc.plus.repo.JogoRepo;
import br.edu.calc.plus.repo.PartidaRepo;
import br.edu.calc.plus.repo.UsuarioRepo;
import br.edu.calc.plus.service.JogoService;
import br.edu.calc.plus.service.PartidaService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PartidaServiceTest {

    @Mock
    private PartidaRepo partidaRepo;

    @Mock
    private JogoRepo jogoRepo;

    @Mock
    private UsuarioRepo usuarioRepo;

    @Mock
    private JogoService jogoService;

    @InjectMocks
    private PartidaService partidaService;

    private Partida partidaPadrao;
    private AutoCloseable mocks;

    @BeforeEach
    void configurar() {
        System.out.println("--- Setup inicializado para PartidaServiceTest ---");
        mocks = MockitoAnnotations.openMocks(this);
        System.out.println("Mocks inicializados.");

        // Criando uma partida padrão: 1 acerto, 0 erro
        partidaPadrao = criarPartida(1, 0);
        System.out.println("Partida padrão criada com 1 acerto e 0 erro.");
        System.out.println("--- Setup concluído ---\n");
    }

    @AfterEach
    void finalizar() throws Exception {
        System.out.println("--- Finalizando PartidaServiceTest ---");
        mocks.close();
        System.out.println("Mocks fechados.");
        System.out.println("--- Finalização concluída ---\n");
    }

    //helpers
    private Partida criarPartida(int acertos, int erros) {
        List<Jogo> jogos = new ArrayList<>();

        // corretos
        for (int i = 0; i < acertos; i++) {
            jogos.add(new Jogo(null, 5, 3, EOperator.soma, 8, 8, 2));
        }

        // incorretos
        for (int i = 0; i < erros; i++) {
            jogos.add(new Jogo(null, 5, 3, EOperator.soma, 8, 5, 2));
        }

        Partida partida = new Partida();
        partida.setId(1);
        partida.setData(LocalDateTime.now());
        partida.setBonificacao(0);
        partida.setTempo(0);
        partida.setJogoList(jogos);

        return partida;
    }

    private void aplicarRespostas(Partida partida, List<Double> respostas) throws Exception {
        for (int i = 0; i < respostas.size(); i++) {
            partidaService.savePartida(partida.getId(), 1, i + 1, i + 1, respostas.get(i));
        }
    }

    @Test
    void getMeusJogos_deveRetornarCamposCorretos() {
        System.out.println("--- Iniciando teste: getMeusJogos_deveRetornarCamposCorretos ---");
        System.out.println("Objetivo: Verificar se getMeusJogos retorna corretamente os campos da DTO.");

        when(partidaRepo.findByUsuarioId(1)).thenReturn(List.of(partidaPadrao));

        List<JogoListDTO> lista = partidaService.getMeusJogos(1);

        System.out.println("Assertiva: A lista de DTOs deve ter tamanho 1.");
        assertEquals(1, lista.size());

        JogoListDTO dto = lista.get(0);
        System.out.println("Assertiva: Os campos do DTO devem corresponder à partida padrão (1 acerto, 0 erros).");
        assertNotNull(dto.getDataJogo());
        assertEquals(0, dto.getBonificacao());
        assertEquals(1, dto.getAcertos());
        assertEquals(0, dto.getErros());
        assertEquals(0, dto.getTempo());
        System.out.println("--- Teste 'getMeusJogos_deveRetornarCamposCorretos' finalizado com sucesso. ---");
    }

    @Test
    void userJaCompetiuHoje_deveRetornarTrueOuFalse() {
        System.out.println("--- Iniciando teste: userJaCompetiuHoje_deveRetornarTrueOuFalse ---");
        System.out.println("Objetivo: Verificar se o método identifica corretamente se o usuário já competiu hoje.");

        LocalDateTime dtIni = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime dtFim = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(0);

        when(partidaRepo.getUsuarioCompetil(1, dtIni, dtFim)).thenReturn(1L);
        System.out.println("Assertiva: Deve retornar true quando o repositório indica que o usuário já competiu.");
        assertTrue(partidaService.userJaCompetiuHoje(1));

        when(partidaRepo.getUsuarioCompetil(1, dtIni, dtFim)).thenReturn(0L);
        System.out.println("Assertiva: Deve retornar false quando o repositório indica que o usuário não competiu.");
        assertFalse(partidaService.userJaCompetiuHoje(1));
        System.out.println("--- Teste 'userJaCompetiuHoje_deveRetornarTrueOuFalse' finalizado com sucesso. ---");
    }

    @Test
    void iniciarPartida_deveCriar10Jogos() throws Exception {
        System.out.println("--- Iniciando teste: iniciarPartida_deveCriar10Jogos ---");
        System.out.println("Objetivo: Verificar se iniciarPartida cria exatamente 10 jogos.");

        when(usuarioRepo.getById(1)).thenReturn(null);
        when(jogoService.criarJogosAleatorio(10, 1)).thenReturn(List.of(
                new Jogo(), new Jogo(), new Jogo(), new Jogo(), new Jogo(),
                new Jogo(), new Jogo(), new Jogo(), new Jogo(), new Jogo()
        ));

        Partida part = partidaService.iniciarPartida(1);

        System.out.println("Assertiva: A lista de jogos da nova partida não deve ser nula e deve conter 10 jogos.");
        assertNotNull(part.getJogoList());
        assertEquals(10, part.getJogoList().size());
        System.out.println("--- Teste 'iniciarPartida_deveCriar10Jogos' finalizado com sucesso. ---");
    }

    @Test
    void savePartida_respostaCorreta_aplicaBonus() throws Exception {
        System.out.println("--- Iniciando teste: savePartida_respostaCorreta_aplicaBonus ---");
        System.out.println("Objetivo: Resposta correta deve aplicar o bônus do jogo à partida.");

        when(partidaRepo.findByIdAndUsuarioId(1, 1)).thenReturn(partidaPadrao);

        Partida resultado = partidaService.savePartida(1, 1, 1, 1, 8);
        
        System.out.println("Assertiva: A bonificação da partida deve ser 2 (bônus do jogo de acerto).");
        assertEquals(2, resultado.getBonificacao());
        System.out.println("--- Teste 'savePartida_respostaCorreta_aplicaBonus' finalizado com sucesso. ---");
    }

    @Test
    void savePartida_respostaIncorreta_removeMetadeBonus() throws Exception {
        System.out.println("--- Iniciando teste: savePartida_respostaIncorreta_removeMetadeBonus ---");
        System.out.println("Objetivo: Resposta incorreta deve remover metade do bônus do jogo da partida.");

        when(partidaRepo.findByIdAndUsuarioId(1, 1)).thenReturn(partidaPadrao);

        Partida resultado = partidaService.savePartida(1, 1, 1, 1, 5);
        
        System.out.println("Assertiva: A bonificação da partida deve ser -1 (metade do bônus removido).");
        assertEquals(-1, resultado.getBonificacao());
        System.out.println("--- Teste 'savePartida_respostaIncorreta_removeMetadeBonus' finalizado com sucesso. ---");
    }

    @Test
    void finalizarPartida_todosAcertos_duplicaBonus() throws Exception {
        System.out.println("--- Iniciando teste: finalizarPartida_todosAcertos_duplicaBonus ---");
        System.out.println("Objetivo: Se todos os jogos forem acertados, a bonificação é duplicada.");

        Partida partida = criarPartida(3, 0);
        when(partidaRepo.findByIdAndUsuarioId(1, 1)).thenReturn(partida);

        aplicarRespostas(partida, List.of(8.0, 8.0, 8.0)); // 3 acertos
        Partida finalizada = partidaService.FinalizaPartida(1, 1, LocalDateTime.now().minusSeconds(10));

        System.out.println("Assertiva: Bônus final deve ser 12 (3 jogos * 2 bônus * 2 [duplicado]).");
        assertEquals(12, finalizada.getBonificacao());
        System.out.println("Assertiva: O tempo da partida deve ser maior que 0.");
        assertTrue(finalizada.getTempo() > 0);
        System.out.println("--- Teste 'finalizarPartida_todosAcertos_duplicaBonus' finalizado com sucesso. ---");
    }

    @Test
    void finalizarPartida_mistos_acertosEerros() throws Exception {
        System.out.println("--- Iniciando teste: finalizarPartida_mistos_acertosEerros ---");
        System.out.println("Objetivo: Testar partida com acertos e erros mistos.");

        Partida partida = criarPartida(2, 2);
        when(partidaRepo.findByIdAndUsuarioId(1, 1)).thenReturn(partida);

        aplicarRespostas(partida, List.of(8.0, 8.0, 5.0, 5.0)); // 2 acertos, 2 erros
        Partida finalizada = partidaService.FinalizaPartida(1, 1, LocalDateTime.now().minusSeconds(10));

        System.out.println("Assertiva: Bônus final deve ser 2 (2*2 - 2*1).");
        assertEquals(2, finalizada.getBonificacao());
        System.out.println("Assertiva: O tempo da partida deve ser maior que 0.");
        assertTrue(finalizada.getTempo() > 0);
        System.out.println("--- Teste 'finalizarPartida_mistos_acertosEerros' finalizado com sucesso. ---");
    }

    @Test
    void finalizarPartida_semAcertos_bonusMantido() throws Exception {
        System.out.println("--- Iniciando teste: finalizarPartida_semAcertos_bonusMantido ---");
        System.out.println("Objetivo: Se nenhum jogo for acertado, a bonificação final deve refletir somente perdas.");

        Partida partida = criarPartida(0, 3);
        when(partidaRepo.findByIdAndUsuarioId(1, 1)).thenReturn(partida);

        aplicarRespostas(partida, List.of(5.0, 5.0, 5.0)); // todos erros
        Partida finalizada = partidaService.FinalizaPartida(1, 1, LocalDateTime.now().minusSeconds(10));

        System.out.println("Assertiva: Bônus final deve ser -3 (3 jogos * -1 bônus).");
        assertEquals(-3, finalizada.getBonificacao());
        System.out.println("--- Teste 'finalizarPartida_semAcertos_bonusMantido' finalizado com sucesso. ---");
    }
}
