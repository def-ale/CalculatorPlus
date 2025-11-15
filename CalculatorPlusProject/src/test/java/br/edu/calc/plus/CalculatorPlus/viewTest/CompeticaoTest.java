package br.edu.calc.plus.CalculatorPlus.viewTest;

import br.edu.calc.plus.domain.EOperator;
import br.edu.calc.plus.domain.Usuario;
import br.edu.calc.plus.domain.dto.UserDTO;
import br.edu.calc.plus.repo.UsuarioRepo;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Tag("ui")
public class CompeticaoTest {

    @LocalServerPort
    private int porta;

    @Autowired
    private UsuarioRepo usuarioRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private WebDriver navegador;
    private String urlBase;

    private final String usuarioValido = "teste";
    private final String senhaValida = "Test@123";
    private final int MAX_PERGUNTAS = 10;

    @BeforeEach
    public void setup() {
        System.out.println("=== Configurando ambiente de teste ===");

        usuarioRepo.deleteAll();
        System.out.println("Banco de dados limpo.");

        UserDTO usuarioDTO = new UserDTO();
        usuarioDTO.setNome("Usuário Teste");
        usuarioDTO.setLogin(usuarioValido);
        usuarioDTO.setEmail("teste@email.com");
        usuarioDTO.setSenha(passwordEncoder.encode(senhaValida));
        usuarioDTO.setCidade("Cidade Teste");
        usuarioDTO.setNascimento(LocalDate.of(2000, 1, 1));

        Usuario usuario = usuarioDTO.ConvertUsuario();
        usuarioRepo.save(usuario);
        System.out.println("Usuário de teste criado: " + usuarioValido);

        urlBase = "http://localhost:" + porta;

        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions opcoes = new FirefoxOptions();
        opcoes.setBinary("C:/Program Files/Mozilla Firefox/firefox.exe");
        opcoes.addArguments("--start-maximized");
        // opcoes.addArguments("--headless");
        navegador = new FirefoxDriver(opcoes);
        navegador.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        System.out.println("Selenium WebDriver configurado e navegador iniciado.");
    }

    private void logar() {
        System.out.println("\n=== Iniciando login ===");
        navegador.get(urlBase + "/login");
        try {
            navegador.findElement(By.id("username")).sendKeys(usuarioValido);
            navegador.findElement(By.id("password")).sendKeys(senhaValida);
            navegador.findElement(By.cssSelector(".btn-primary.btn-round")).click();

            WebDriverWait espera = new WebDriverWait(navegador, Duration.ofSeconds(10));
            espera.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@href='/logout']")));
            System.out.println("Login realizado com sucesso como '" + usuarioValido + "'.");
        } catch (Exception e) {
            fail("Falha ao realizar login: " + e.getMessage());
        }
    }

    private double resolverOperacao(String expressao) {
        String[] partes = expressao.split(" ");
        if (partes.length != 3) {
            throw new IllegalArgumentException("Formato de expressão inválido: " + expressao);
        }

        int valor1 = (int) Double.parseDouble(partes[0].trim());
        String operador = partes[1].trim();
        int valor2 = (int) Double.parseDouble(partes[2].trim());

        EOperator operadorEnum;
        switch (operador) {
            case "+":
                operadorEnum = EOperator.soma;
                break;
            case "-":
                operadorEnum = EOperator.subtracao;
                break;
            case "*":
                operadorEnum = EOperator.multiplicacao;
                break;
            case "/":
                operadorEnum = EOperator.divisao;
                break;
            default:
                throw new UnsupportedOperationException("Operador desconhecido: " + operador);
        }

        double resultado = operadorEnum.calcular(valor1, valor2);
        System.out.println("Calculando operação: " + valor1 + " " + operador + " " + valor2 + " = " + resultado);
        return resultado;
    }

    private String formatarResposta(double valor) {
        return String.valueOf(valor);
    }

    @Test
    public void testGanharCompeticao() {
        logar();

        System.out.println("\n=== Iniciando competição ===");
        WebDriverWait espera = new WebDriverWait(navegador, Duration.ofSeconds(15));
        navegador.get(urlBase + "/competicao");

        try {
            WebElement botaoNovaCompeticao = espera.until(
                    ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='/competicao/new']"))
            );
            botaoNovaCompeticao.click();
            espera.until(ExpectedConditions.visibilityOfElementLocated(By.name("cpResposta")));
            System.out.println("Nova competição iniciada.");

            for (int i = 1; i <= MAX_PERGUNTAS; i++) {
                WebElement elementoPergunta = espera.until(
                        ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[@class='text-center']")));
                String expressao = elementoPergunta.getText().trim();

                WebElement elementoNumeroPergunta = navegador.findElement(By.xpath("//h3[contains(text(), 'Pergunta')]"));
                System.out.println("\n" + elementoNumeroPergunta.getText() + " (" + i + "/" + MAX_PERGUNTAS + "): " + expressao);

                double respostaEsperada = resolverOperacao(expressao);
                WebElement campoResposta = navegador.findElement(By.name("cpResposta"));
                campoResposta.clear();
                campoResposta.sendKeys(formatarResposta(respostaEsperada));
                System.out.println("Resposta enviada: " + formatarResposta(respostaEsperada));

                WebElement botaoProxima = navegador.findElement(By.xpath("//button[@type='submit' and text()='Próxima']"));
                botaoProxima.click();

                if (i < MAX_PERGUNTAS) {
                    espera.until(ExpectedConditions.visibilityOfElementLocated(By.name("cpResposta")));
                }
            }

            espera.until(ExpectedConditions.urlContains("/detalhe"));
            System.out.println("\n=== Competição finalizada ===");

            assertTrue(navegador.getCurrentUrl().contains("/detalhe"));
        } catch (Exception e) {
            fail("Erro durante o teste de Competição: " + e.getMessage());
        }
    }

    @Test
    public void testVerificaConteudoDaPaginaDeDetalhes() {
        logar();

        System.out.println("\n=== Iniciando competição para teste de detalhes ===");
        WebDriverWait espera = new WebDriverWait(navegador, Duration.ofSeconds(15));
        navegador.get(urlBase + "/competicao/new");

        List<String> perguntas = new ArrayList<>();
        List<String> respostas = new ArrayList<>();
        List<Double> resultados = new ArrayList<>();

        try {
            espera.until(ExpectedConditions.visibilityOfElementLocated(By.name("cpResposta")));
            System.out.println("Nova competição iniciada.");

            for (int i = 1; i <= MAX_PERGUNTAS; i++) {
                WebElement elementoPergunta = espera.until(
                        ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[@class='text-center']")));
                String expressao = elementoPergunta.getText().trim();
                perguntas.add(expressao);

                double respostaCorreta = resolverOperacao(expressao);
                resultados.add(respostaCorreta);
                String respostaEnviada;

                if (i == 10) { // Errar a última de propósito
                    respostaEnviada = formatarResposta(respostaCorreta + 1);
                } else {
                    respostaEnviada = formatarResposta(respostaCorreta);
                }
                respostas.add(respostaEnviada);

                WebElement campoResposta = navegador.findElement(By.name("cpResposta"));
                campoResposta.clear();
                campoResposta.sendKeys(respostaEnviada);

                WebElement botaoProxima = navegador.findElement(By.xpath("//button[@type='submit' and text()='Próxima']"));
                botaoProxima.click();

                if (i < MAX_PERGUNTAS) {
                    espera.until(ExpectedConditions.visibilityOfElementLocated(By.name("cpResposta")));
                }
            }

            espera.until(ExpectedConditions.urlContains("/detalhe"));
            System.out.println("\n=== Competição finalizada, verificando detalhes ===");

            List<WebElement> linhasResultado = navegador.findElements(By.cssSelector("ul.team-members li"));
            assertEquals(10, linhasResultado.size(), "A lista de detalhes deveria ter 10 itens.");

            for (int i = 0; i < linhasResultado.size(); i++) {
                WebElement linha = linhasResultado.get(i);
                String textoPergunta = linha.findElement(By.xpath(".//h3")).getText();
                String textoResposta = linha.findElement(By.xpath(".//h2")).getText();
                WebElement imagemStatus = linha.findElement(By.xpath(".//img"));
                String srcImagemStatus = imagemStatus.getAttribute("src");

                String perguntaEsperada = perguntas.get(i) + " = " + resultados.get(i);
                String respostaEsperada = respostas.get(i);

                System.out.println("Verificando linha " + (i + 1) + ": Questão='" + textoPergunta + "', Resposta='" + textoResposta + "'");

                assertEquals(perguntaEsperada, textoPergunta, "A questão na linha " + (i + 1) + " está incorreta.");
                assertEquals(respostaEsperada, textoResposta, "A resposta na linha " + (i + 1) + " está incorreta.");

                if (i < 9) { // 9 acertos
                    assertTrue(srcImagemStatus.contains("ok1.png"), "A linha " + (i + 1) + " deveria mostrar status de acerto.");
                } else { // 1 erro
                    assertTrue(srcImagemStatus.contains("nok1.png"), "A linha " + (i + 1) + " deveria mostrar status de erro.");
                }
            }
            System.out.println("Assertiva: Todas as 10 linhas da tabela de detalhes foram verificadas com sucesso.");

        } catch (Exception e) {
            fail("Erro durante o teste de verificação de detalhes: " + e.getMessage());
        }
    }

    @AfterEach
    public void tearDown() {
        if (navegador != null) {
            navegador.quit();
            System.out.println("Navegador fechado e recursos liberados.");
        }
    }
}
