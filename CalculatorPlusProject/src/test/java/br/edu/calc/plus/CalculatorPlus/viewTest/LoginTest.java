package br.edu.calc.plus.CalculatorPlus.viewTest;

import br.edu.calc.plus.domain.Usuario;
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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Tag("ui")
public class LoginTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UsuarioRepo usuarioRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private WebDriver driver;
    private String baseUrl;

    private final String validUsername = "daves";
    private final String validPassword = "123456";

    @BeforeEach
    public void setup() {
        // Configura a URL base com a porta aleatória
        baseUrl = "http://localhost:" + port;

        // Limpa o repositório e cria um usuário de teste
        usuarioRepo.deleteAll();
        Usuario user = new Usuario();
        user.setLogin(validUsername);
        user.setSenha(passwordEncoder.encode(validPassword));
        user.setNome("Test User");
        user.setEmail("test@test.com");
        user.setCidade("Cidade Teste");
        user.setDataNascimento(LocalDate.of(2000, 1, 1)); // Adicionado
        usuarioRepo.save(user);

        // Configura o Selenium WebDriver
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();
        options.setBinary("C:/Program Files/Mozilla Firefox/firefox.exe");
        options.addArguments("--start-maximized");
        //options.addArguments("--headless");

        driver = new FirefoxDriver(options);
    }

    @Test
    public void testLoginSucesso() {
        System.out.println("Iniciando teste de Login com Sucesso...");
        driver.get(baseUrl + "/login");

        try {
            WebElement usernameInput = driver.findElement(By.id("username"));
            WebElement passwordInput = driver.findElement(By.id("password"));
            WebElement loginButton = driver.findElement(By.cssSelector(".btn-primary.btn-round"));

            usernameInput.sendKeys(validUsername);
            passwordInput.sendKeys(validPassword);
            loginButton.click();

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            By logoutLinkLocator = By.xpath("//a[@href='/logout']");
            wait.until(ExpectedConditions.visibilityOfElementLocated(logoutLinkLocator));

            System.out.println("Login realizado com sucesso.");
        } catch (Exception e) {
            fail("Falha no Login: " + e.getMessage());
        }
    }

    @Test
    public void testLoginFalho() {
        System.out.println("Iniciando teste de Falha no Login...");
        driver.get(baseUrl + "/login");

        try {
            driver.findElement(By.id("username")).sendKeys("usuario_invalido");
            driver.findElement(By.id("password")).sendKeys("senha_errada");
            driver.findElement(By.cssSelector(".btn-primary.btn-round")).click();

            Thread.sleep(2000);

            boolean isStillOnLoginPage = driver.getCurrentUrl().contains("/login");
            boolean isLoginErrorMsgPresent = !driver.findElements(By.cssSelector("div.alert-danger")).isEmpty();

            assertTrue(isStillOnLoginPage && isLoginErrorMsgPresent,
                    "Falha no teste: deveria permanecer na página de login com mensagem de erro.");
            System.out.println("Falha no login validada corretamente.");
        } catch (Exception e) {
            fail("Erro durante o teste de Falha no Login: " + e.getMessage());
        }
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}