package br.edu.calc.plus.CalculatorPlus;

import br.edu.calc.plus.config.security.user.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class CalculatorPlusApplicationTests {
	@Test
	void contextLoads() {
	}

}
