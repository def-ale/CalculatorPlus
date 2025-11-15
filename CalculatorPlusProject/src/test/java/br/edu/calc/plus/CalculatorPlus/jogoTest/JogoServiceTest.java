package br.edu.calc.plus.CalculatorPlus.jogoTest;

import br.edu.calc.plus.domain.EOperator;
import br.edu.calc.plus.domain.Jogo;
import br.edu.calc.plus.repo.JogoRepo;
import br.edu.calc.plus.service.JogoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JogoServiceTest {

    @Mock
    private JogoRepo jDao;

    @InjectMocks
    private JogoService jogoService;

    // Lista de jogos usada em vários testes
    private List<Jogo> jogosMock;

    private AutoCloseable mocks; // para fechar mocks após cada teste

    @BeforeEach
    void setup() {
        System.out.println("--- Setup inicializado para JogoServiceTest ---");
        // Inicializa os mocks
        mocks = MockitoAnnotations.openMocks(this);
        System.out.println("Mocks inicializados.");

        // Mock do DAO para retornar 0 acertos por padrão
        when(jDao.getAllAcertosUser(anyInt())).thenReturn(0L);
        System.out.println("Mock de jDao.getAllAcertosUser(anyInt()) configurado para retornar 0L.");

        // Lista de jogos mockados para testes
        jogosMock = List.of(
                new Jogo(null, 5, 3, EOperator.soma, EOperator.soma.calcular(5,3), 0, 1),
                new Jogo(null, 7, 2, EOperator.subtracao, EOperator.subtracao.calcular(7,2), 0, 1),
                new Jogo(null, 4, 5, EOperator.multiplicacao, EOperator.multiplicacao.calcular(4,5), 0, 1),
                new Jogo(null, 8, 2, EOperator.divisao, EOperator.divisao.calcular(8,2), 0, 1)
        );
        System.out.println("Lista de jogos mockados criada para testes.");
        System.out.println("--- Setup concluído ---");
    }

    @AfterEach
    void finalizar() throws Exception {
        System.out.println("--- Finalizando JogoServiceTest ---");
        // Fecha os mocks para liberar recursos e evitar efeitos colaterais
        mocks.close();
        System.out.println("Mocks fechados.");
        System.out.println("--- Finalização concluída ---");
    }

    @Test
    void deveGerarJogosDeterministicosComBonusValido() {
        System.out.println("\n--- Iniciando teste: deveGerarJogosDeterministicosComBonusValido ---");
        System.out.println("Objetivo: Verificar se os jogos gerados possuem bônus válidos e resultados corretos.");

        for (Jogo jogo : jogosMock) {
            System.out.println("Testando jogo: " + jogo.getValor1() + " " + jogo.getOperador() + " " + jogo.getValor2());

            // Calculando bônus
            double bonus = jogoService.bonusInicial(jogo.getOperador(), 1).doubleValue();
            jogo.setBonus(bonus);
            System.out.println("Bônus calculado: " + bonus);

            // Verificações básicas
            assertNotNull(jogo, "O jogo não deve ser nulo.");
            System.out.println("Assertiva: Jogo não é nulo.");

            assertNotNull(jogo.getOperador(), "O operador não deve ser nulo.");
            System.out.println("Assertiva: Operador não é nulo.");

            assertTrue(jogo.getBonus() >= 0.1, "Bônus deve ser >= 0.1.");
            System.out.println("Assertiva: Bônus (" + jogo.getBonus() + ") é maior ou igual a 0.1.");

            assertTrue(jogo.getBonus() <= 18, "Bônus deve ser <= 18.");
            System.out.println("Assertiva: Bônus (" + jogo.getBonus() + ") é menor ou igual a 18.");
            System.out.println("Assertiva: Bônus (" + jogo.getBonus() + ") está dentro do intervalo esperado [0.1, 18].");

            // Verifica se o resultado bate com o cálculo do operador
            double resultadoEsperado = jogo.getOperador().calcular((int)jogo.getValor1(), (int)jogo.getValor2());
            assertEquals(resultadoEsperado, jogo.getResultado(), 0.0001, "Resultado do jogo está incorreto.");
            System.out.println("Assertiva: Resultado do jogo - Esperado: " + resultadoEsperado + ", Obtido: " + jogo.getResultado());
            System.out.println("Assertiva: Resultado do jogo (" + jogo.getResultado() + ") corresponde ao esperado (" + resultadoEsperado + ").");
        }
        System.out.println("--- Teste 'deveGerarJogosDeterministicosComBonusValido' finalizado com sucesso. ---");
    }

    @Test
    void deveGerarBonusValidoParaTodosOperadores() {
        System.out.println("\n--- Iniciando teste: deveGerarBonusValidoParaTodosOperadores ---");
        System.out.println("Objetivo: Verificar se o bônus inicial é gerado corretamente para todos os operadores.");

        for (EOperator op : EOperator.values()) {
            // Evitar divisão por zero
            int valor1 = (op == EOperator.divisao) ? 10 : 5;
            int valor2 = (op == EOperator.divisao) ? 2 : 3;

            double bonus = jogoService.bonusInicial(op, 1).doubleValue();
            System.out.println("Operador: " + op + " | Bonus: " + bonus);

            assertTrue(bonus >= 0.1, "Bônus mínimo esperado >= 0.1 para o operador " + op + ".");
            System.out.println("Assertiva: Bônus (" + bonus + ") para o operador " + op + " é maior ou igual a 0.1.");

            assertTrue(bonus <= 18, "Bônus máximo esperado <= 18 para o operador " + op + ".");
            System.out.println("Assertiva: Bônus (" + bonus + ") para o operador " + op + " é menor ou igual a 18.");
            System.out.println("Assertiva: Bônus (" + bonus + ") para o operador " + op + " está dentro do intervalo esperado [0.1, 18].");
        }
        System.out.println("--- Teste 'deveGerarBonusValidoParaTodosOperadores' finalizado com sucesso. ---");
    }
}
