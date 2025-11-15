package br.edu.calc.plus.CalculatorPlus.jogoTest;

import br.edu.calc.plus.domain.EOperator;
import br.edu.calc.plus.domain.Jogo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JogoTest {

    private Jogo jogoAcerto;
    private Jogo jogoErro;

    private double valor1;
    private double valor2;

    @BeforeEach
    void setup() {
        System.out.println("--- Setup inicializado para JogoTest ---");
        // Valores padrões
        valor1 = 5;
        valor2 = 3;

        // jogo correto
        jogoAcerto = new Jogo(null, valor1, valor2, EOperator.soma, EOperator.soma.calcular((int)valor1,(int)valor2), EOperator.soma.calcular((int)valor1,(int)valor2), 1);
        System.out.println("Jogo de acerto criado: " + valor1 + " " + EOperator.soma + " " + valor2 + " = " + jogoAcerto.getResultado());

        // jogo incorreto
        jogoErro = new Jogo(null, valor1, valor2, EOperator.soma, 0, 0, 1);
        System.out.println("Jogo de erro criado: " + valor1 + " " + EOperator.soma + " " + valor2 + " = " + jogoErro.getResultado() + " (resposta esperada: " + EOperator.soma.calcular((int)valor1,(int)valor2) + ")");
        System.out.println("--- Setup concluído ---");
    }

    @Test
    void testeCalculoOperadores() {
        System.out.println("\n--- Iniciando teste: testeCalculoOperadores ---");
        System.out.println("Objetivo: Verificar se os operadores matemáticos realizam os cálculos corretamente.");

        System.out.println("Comparando 5 + 3 = 8...");
        double somaObtida = EOperator.soma.calcular(5,3);
        assertEquals(8, somaObtida, 0.0001);
        System.out.println("Assertiva: Soma - Esperado: 8, Obtido: " + somaObtida);

        System.out.println("Comparando 5 - 3 = 2...");
        double subtracaoObtida = EOperator.subtracao.calcular(5,3);
        assertEquals(2, subtracaoObtida, 0.0001);
        System.out.println("Assertiva: Subtração - Esperado: 2, Obtido: " + subtracaoObtida);

        System.out.println("Comparando 5 * 3 = 15...");
        double multiplicacaoObtida = EOperator.multiplicacao.calcular(5,3);
        assertEquals(15, multiplicacaoObtida, 0.0001);
        System.out.println("Assertiva: Multiplicação - Esperado: 15, Obtido: " + multiplicacaoObtida);

        System.out.println("Comparando 10 / 2 = 5...");
        double divisaoObtida = EOperator.divisao.calcular(10,2);
        assertEquals(5, divisaoObtida, 0.0001);
        System.out.println("Assertiva: Divisão - Esperado: 5, Obtido: " + divisaoObtida);
        
        System.out.println("--- Teste 'testeCalculoOperadores' finalizado com sucesso. ---");
    }

    @Test
    void testeEstaCerto() {
        System.out.println("\n--- Iniciando teste: testeEstaCerto ---");
        System.out.println("Objetivo: Verificar a lógica de acerto e erro do objeto Jogo.");

        System.out.println("Verificando comportamento de estaCerto() e isCorrect() para resposta correta.");
        boolean estaCerto1 = jogoAcerto.estaCerto();
        assertTrue(estaCerto1, "Esperado: true para estaCerto(), Obtido: " + estaCerto1);
        
        boolean isCorrect1 = jogoAcerto.isCorrect();
        assertTrue(isCorrect1, "Esperado: true para isCorrect(), Obtido: " + isCorrect1);
        System.out.println("Assertiva: Ambos os métodos retornaram true para resposta correta.");

        jogoAcerto.setResposta(7);
        System.out.println("Alterando resposta para incorreta e verificando novamente.");
        boolean estaCerto2 = jogoAcerto.estaCerto();
        assertFalse(estaCerto2, "Esperado: false para estaCerto(), Obtido: " + estaCerto2);

        boolean isCorrect2 = jogoAcerto.isCorrect();
        assertFalse(isCorrect2, "Esperado: false para isCorrect(), Obtido: " + isCorrect2);
        System.out.println("Assertiva: Ambos os métodos retornaram false para resposta incorreta.");
        
        System.out.println("--- Teste 'testeEstaCerto' finalizado com sucesso. ---");
    }

    @Test
    void divisaoPorZeroLancaException() {
        System.out.println("\n--- Iniciando teste: divisaoPorZeroLancaException ---");
        System.out.println("Objetivo: Verificar se a divisão por zero lança a exceção esperada.");

        Jogo j = new Jogo(null,5,0,EOperator.divisao,0,0,1);
        System.out.println("Criado Jogo com divisão por zero: " + j.getValor1() + " " + j.getOperador() + " " + j.getValor2());
        
        System.out.println("Verificando se EOperator.divisao.calcular(5,0) lança ArithmeticException...");
        assertThrows(ArithmeticException.class, () -> {
            EOperator.divisao.calcular((int) j.getValor1(), (int) j.getValor2());
        }, "A divisão por zero deveria lançar ArithmeticException.");
        System.out.println("Assertiva: ArithmeticException foi lançada conforme esperado para divisão por zero.");
        
        System.out.println("--- Teste 'divisaoPorZeroLancaException' finalizado com sucesso. ---");
    }
}
