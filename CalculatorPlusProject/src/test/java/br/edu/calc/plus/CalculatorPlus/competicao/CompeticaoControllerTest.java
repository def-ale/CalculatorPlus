package br.edu.calc.plus.CalculatorPlus.competicao;

import br.edu.calc.plus.controller.CompeticaoController;
import br.edu.calc.plus.domain.Jogo;
import br.edu.calc.plus.domain.Partida;
import br.edu.calc.plus.service.JogoService;
import br.edu.calc.plus.service.PartidaService;
import br.edu.calc.plus.util.LogadoUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class CompeticaoControllerTest {

    @InjectMocks
    private CompeticaoController controller;

    @Mock
    private JogoService jogoService;

    @Mock
    private PartidaService partidaService;

    @Mock
    private LogadoUtil logado;

    @Mock
    private Authentication auth;

    @Mock
    private RedirectAttributes attr;

    @Mock
    private ModelMap model;

    @Mock
    private HttpSession session;

    private Partida partidaMock;
    private List<Jogo> jogosMock;

    @BeforeEach
    void setup() throws Exception {
        System.out.println("--- Setup inicializado para CompeticaoControllerTest ---");
        jogosMock = List.of(
                mock(Jogo.class), mock(Jogo.class), mock(Jogo.class),
                mock(Jogo.class), mock(Jogo.class), mock(Jogo.class),
                mock(Jogo.class), mock(Jogo.class), mock(Jogo.class),
                mock(Jogo.class)
        );
        System.out.println("Mock de 10 jogos criado.");

        partidaMock = mock(Partida.class);
        lenient().when(partidaMock.getId()).thenReturn(10);
        lenient().when(partidaMock.getJogoList()).thenReturn(jogosMock);
        System.out.println("Mock de partida criado com id = " + partidaMock.getId());
        System.out.println("--- Setup concluído ---\n");
    }

    @Test
    void deveRedirecionarSeJaCompetiuHoje() {
        System.out.println("--- Iniciando teste: deveRedirecionarSeJaCompetiuHoje ---");
        System.out.println("Objetivo: Verificar se o usuário é redirecionado se já competiu hoje.");

        when(logado.getIdUserLogado(auth)).thenReturn(1);
        when(partidaService.userJaCompetiuHoje(1)).thenReturn(true);

        String view = controller.novoJogo(model, auth, attr, session);
        System.out.println("View retornada: " + view);

        String esperado = "redirect:/competicao";
        assertEquals(esperado, view, "Esperado: " + esperado + ", Obtido: " + view);
        System.out.println("Assertiva: Redirecionamento para /competicao verificado.");
        verify(attr, never()).addFlashAttribute(eq("error"), anyString());
        System.out.println("Assertiva: Nenhuma mensagem de erro adicionada.");
        System.out.println("--- Teste 'deveRedirecionarSeJaCompetiuHoje' finalizado com sucesso. ---\n");
    }

    @Test
    void deveIniciarPartidaNova() throws Exception {
        System.out.println("--- Iniciando teste: deveIniciarPartidaNova ---");
        System.out.println("Objetivo: Verificar se uma nova partida é iniciada corretamente.");

        when(logado.getIdUserLogado(auth)).thenReturn(1);
        when(partidaService.userJaCompetiuHoje(1)).thenReturn(false);
        when(partidaService.iniciarPartida(1)).thenReturn(partidaMock);

        String view = controller.novoJogo(model, auth, attr, session);
        System.out.println("View retornada: " + view);

        String esperadoView = "jogar";
        assertEquals(esperadoView, view, "Esperado: " + esperadoView + ", Obtido: " + view);
        System.out.println("Assertiva: View 'jogar' retornada.");

        verify(model).addAttribute("idPartida", partidaMock.getId());
        System.out.println("Assertiva: Atributo 'idPartida' adicionado ao modelo.");
        verify(model).addAttribute(eq("jogo"), any(Jogo.class));
        System.out.println("Assertiva: Atributo 'jogo' adicionado ao modelo.");
        verify(model).addAttribute("pergunta", 1);
        System.out.println("Assertiva: Atributo 'pergunta' (valor 1) adicionado ao modelo.");
        verify(session).setAttribute(eq("tempoInicio"), any());
        System.out.println("Assertiva: Atributo 'tempoInicio' adicionado à sessão.");
        System.out.println("--- Teste 'deveIniciarPartidaNova' finalizado com sucesso. ---\n");
    }

    @Test
    void deveAvancarParaProximaPergunta() throws Exception {
        System.out.println("--- Iniciando teste: deveAvancarParaProximaPergunta ---");
        System.out.println("Objetivo: Verificar se o controller avança para a próxima pergunta após uma resposta.");

        when(logado.getIdUserLogado(auth)).thenReturn(1);
        when(partidaService.savePartida(anyInt(), anyInt(), anyInt(), anyInt(), anyDouble()))
                .thenReturn(partidaMock);

        String view = controller.nextJogo("5", "10", "20", "1", model, auth, attr, session);
        System.out.println("View retornada: " + view);

        String esperadoView = "jogar";
        assertEquals(esperadoView, view, "Esperado: " + esperadoView + ", Obtido: " + view);
        System.out.println("Assertiva: View 'jogar' retornada.");

        verify(model).addAttribute("idPartida", 10);
        System.out.println("Assertiva: Atributo 'idPartida' (valor 10) adicionado ao modelo.");
        verify(model).addAttribute(eq("jogo"), any(Jogo.class));
        System.out.println("Assertiva: Atributo 'jogo' adicionado ao modelo.");
        verify(model).addAttribute("pergunta", 2);
        System.out.println("Assertiva: Atributo 'pergunta' (valor 2) adicionado ao modelo.");
        System.out.println("--- Teste 'deveAvancarParaProximaPergunta' finalizado com sucesso. ---\n");
    }

    @Test
    void deveFinalizarPartidaNaDecimaPergunta() throws Exception {
        System.out.println("--- Iniciando teste: deveFinalizarPartidaNaDecimaPergunta ---");
        System.out.println("Objetivo: Verificar se a partida é finalizada corretamente na décima pergunta.");

        // arrange
        when(logado.getIdUserLogado(auth)).thenReturn(1);
        when(partidaService.savePartida(anyInt(), anyInt(), eq(10), anyInt(), anyDouble()))
                .thenReturn(partidaMock);
        when(session.getAttribute("tempoInicio")).thenReturn(LocalDateTime.now());

        // act
        String view = controller.nextJogo("3", "10", "20", "10", model, auth, attr, session);
        System.out.println("View retornada: " + view);

        // assert
        String esperadoView = "redirect:/competicao/10/detalhe";
        assertEquals(esperadoView, view, "Esperado: " + esperadoView + ", Obtido: " + view);
        System.out.println("Assertiva: Redirecionamento para detalhes da competição verificado.");
        verify(partidaService).FinalizaPartida(eq(10), eq(1), any());
        System.out.println("Assertiva: Método 'FinalizaPartida' do serviço chamado.");
        verify(attr).addFlashAttribute("success", "Fim da participação por hoje, Até amanhã");
        System.out.println("Assertiva: Mensagem de sucesso adicionada ao RedirectAttributes.");
        System.out.println("--- Teste 'deveFinalizarPartidaNaDecimaPergunta' finalizado com sucesso. ---\n");
    }

    @Test
    void deveTratarErroAoSalvarPartida() {
        System.out.println("--- Iniciando teste: deveTratarErroAoSalvarPartida ---");
        System.out.println("Objetivo: Verificar se erros ao salvar a partida são tratados e redirecionam corretamente.");

        when(logado.getIdUserLogado(auth)).thenReturn(1);

        String view = controller.nextJogo("abc", "x", "y", "z", model, auth, attr, session);
        System.out.println("View retornada: " + view);

        String esperadoView = "redirect:/competicao";
        assertEquals(esperadoView, view, "Esperado: " + esperadoView + ", Obtido: " + view);
        System.out.println("Assertiva: Redirecionamento para /competicao verificado.");
        verify(attr).addFlashAttribute(eq("error"), contains("Erro"));
        System.out.println("Assertiva: Mensagem de erro adicionada ao RedirectAttributes.");
        System.out.println("--- Teste 'deveTratarErroAoSalvarPartida' finalizado com sucesso. ---\n");
    }
}
