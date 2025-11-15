package br.edu.calc.plus.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.calc.plus.domain.EOperator;
import br.edu.calc.plus.domain.Jogo;
import br.edu.calc.plus.repo.JogoRepo;
import br.edu.calc.plus.util.Util;

@Service
public class JogoService {

	@Autowired
	private JogoRepo jDao;

	public String getAllErros() {

		return Util.formatEmK(jDao.getAllErros());
	}

	public long getAllAcertos() {
		return jDao.getAllAcertos();
	}

	public long[] getAcertosUltimos10Dias() {
		long[] values = new long[10];
		for (int i = 1; i <= 10; i++) {
			LocalDateTime dtIni = LocalDate.now().minusDays(10 - i).atStartOfDay();
			LocalDateTime dtFim = LocalDate.now().minusDays(10 - i).atTime(23, 59, 59);
			values[i - 1] = jDao.getAcertosDia(dtIni, dtFim);
		}
		return values;
	}

	public long[] getErrosUltimos10Dias() {
		long[] values = new long[10];
		for (int i = 1; i <= 10; i++) {
			LocalDateTime dtIni = LocalDate.now().minusDays(10 - i).atStartOfDay();
			LocalDateTime dtFim = LocalDate.now().minusDays(10 - i).atTime(23, 59, 59);
			values[i - 1] = jDao.getErrosDia(dtIni, dtFim);
		}
		return values;
	}

	private double bonusOperacao(EOperator operador) {
		if (operador == EOperator.soma) {
			return 0.1;
		} else if (operador == EOperator.divisao) {
			return 0.9;
		}
		if (operador == EOperator.multiplicacao) {
			return 0.5;
		}
		if (operador == EOperator.subtracao) {
			return 0.2;
		}
		return 0;
	}

	private double bonusUsuario(int id) {
		final long tot = jDao.getAllAcertosUser( id );
		double perc = (tot <= 10) ? 0
				: (tot <= 50) ? 0.1 : (tot <= 100) ? 0.2 : (tot <= 150) ? 0.3 : (tot <= 200) ? 0.5 : 0.8;
		return perc;
	}

	public BigDecimal bonusInicial(EOperator operador, int id) {
		Random rand = new Random(new Date().getTime());
		double valor = rand.nextDouble();
		valor *= 9;
		valor += bonusOperacao(operador);
		valor *= (1 + bonusUsuario(id));
		return Util.truncateDecimal(valor, 2);
	}

	public List<Jogo> criarJogosAleatorio(int idx, int idUser) {
		List<Jogo> jogos = new ArrayList<>();
		Random rand = new Random(new Date().getTime());
		for (int j = 0; j < idx; j++) {
			final int v1 = rand.nextInt(50);
			final int v2 = rand.nextInt(50);
			final int pos = rand.nextInt(4);
			final EOperator op = EOperator.values()[pos];
			Jogo jogo = new Jogo(null, v1, v2, op, op.calcular(v1, v2), -1111, 0);
			jogo.setBonus(bonusInicial(op,idUser).doubleValue());
			jogos.add(jogo);
		}

		return jogos;
	}

}
