package br.edu.calc.plus.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.calc.plus.domain.Jogo;
import br.edu.calc.plus.domain.Partida;
import br.edu.calc.plus.domain.dto.JogoListDTO;
import br.edu.calc.plus.domain.dto.RankingDTO;
import br.edu.calc.plus.repo.JogoRepo;
import br.edu.calc.plus.repo.PartidaRepo;
import br.edu.calc.plus.repo.UsuarioRepo;
import br.edu.calc.plus.util.Util;

@Service
public class PartidaService {

	@Autowired
	private PartidaRepo pDao;

	@Autowired
	private JogoRepo jDao;

	@Autowired
	private UsuarioRepo uDao;

	@Autowired
	private JogoService jogoService;

	public String getPremioUsers() {
		try {
			return Util.formatarMoeda(pDao.getAllBonus());
		} catch (NullPointerException e) {
			return Util.formatarMoeda(0);
		}
	}

	@Transactional
	public List<JogoListDTO> getMeusJogos(int idUserLogado) {

		List<JogoListDTO> lista = new ArrayList<>();

		List<Partida> partidas = pDao.findByUsuarioId(idUserLogado);

		partidas.forEach(p -> {
			lista.add(new JogoListDTO(p.getId(), p.getData(), p.getBonificacao(), p.getAcertos(), p.getErros(), p.getTempo()));
		});

		return lista;
	}

	public boolean userJaCompetiuHoje(Integer idUserLogado) {

		LocalDateTime dtIni = LocalDate.now().atStartOfDay();
		LocalDateTime dtFim = LocalDate.now().atTime(23, 59, 59);

		return pDao.getUsuarioCompetil(idUserLogado, dtIni, dtFim) >= 1;
	}

	@Transactional(rollbackFor = Exception.class)
	public Partida iniciarPartida(Integer idUser) throws Exception {

		Partida part = new Partida(null, LocalDateTime.now(), 0, 0);
		part.setUsuario(uDao.getById(idUser));
		try {

			pDao.save(part);

			part.setJogoList(jogoService.criarJogosAleatorio(10, idUser));

			for (Jogo jogo : part.getJogoList()) {
				jogo.setPartida(part);
				jDao.save(jogo);
			}
			pDao.save(part);
			return part;
		} catch (Exception e) {
			throw new Exception("Erro ao criar a partida :: " + e.getMessage());
		}

	}

	@Transactional(rollbackFor = Exception.class)
	public Partida savePartida(int idPartida, Integer idUser, int posicao, int idJogo, double valor) throws Exception {

		Partida p = pDao.findByIdAndUsuarioId(idPartida, idUser);

		if (p == null) {
			throw new Exception("partida não encontrada ");
		}

		Jogo j = p.getJogoList().get(posicao - 1);
		j.setResposta(valor);
		
		if (j.isCorrect()) {
            p.addBonus(j.getBonus());
        } else {
            p.removeBonus(j.getBonus() / 2);
        }

		jDao.save(j);
		pDao.save(p);

		return p;
	}

	@Transactional(rollbackFor = Exception.class)
	public Partida FinalizaPartida(int idPartida, Integer idUser, LocalDateTime inicio) throws Exception {

		Partida p = pDao.findByIdAndUsuarioId(idPartida, idUser);

		if (p == null) {
			throw new Exception("partida não encontrada ");
		}
		
		long segundos = inicio.until(LocalDateTime.now(), ChronoUnit.SECONDS);
		p.setTempo( segundos );
		
		if (p.getAcertos() == p.getJogoList().size()) {
            p.setBonificacao(p.getBonificacao() * 2);
        }
		
		pDao.save(p);

		return p;
	}

	@Transactional(rollbackFor = Exception.class)
	public Partida getPartida(int idPartida, Integer idUser) throws Exception {
		Partida p = pDao.findByIdAndUsuarioId(idPartida, idUser);

		if (p == null) {
			throw new Exception("partida não encontrada ");
		}
		p.getJogoList();
		return p;
	}

	public List<RankingDTO> getRanking() {
		
		return pDao.getRanking();
	}

}
