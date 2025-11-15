package br.edu.calc.plus.CalculatorPlus;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class CalculatorPlusApplicationTests {

	@MockBean
	private PasswordEncoder passwordEncoder;
	@Test
	void contextLoads() {
	}

}
