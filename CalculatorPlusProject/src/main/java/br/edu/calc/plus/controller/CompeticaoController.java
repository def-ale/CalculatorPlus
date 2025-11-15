package br.edu.calc.plus.controller;

import java.time.LocalDateTime;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.calc.plus.domain.Partida;
import br.edu.calc.plus.service.JogoService;
import br.edu.calc.plus.service.PartidaService;
import br.edu.calc.plus.util.LogadoUtil;

//@PreAuthorize("isAuthenticated()")
@Controller
@RequestMapping("/competicao")
public class CompeticaoController {

	@Autowired
	JogoService jogoService;

	@Autowired
	PartidaService partidaService;

	@Autowired
	LogadoUtil logado;

	@GetMapping("")
	public String meusJogos(ModelMap model, Authentication authentication) {

		model.addAttribute("lista", partidaService.getMeusJogos(logado.getIdUserLogado(authentication)));

		return "minhasCompeticoes";
	}
	
	@GetMapping("/{id}/detalhe")
	public String detalhePartida(@PathVariable(name = "id", required = false) int id, ModelMap model, Authentication authentication, RedirectAttributes attr) {

		Integer idUser = logado.getIdUserLogado(authentication);

		Partida p;
		try {
			p = partidaService.getPartida(id, idUser);
		} catch (Exception e) {
			attr.addFlashAttribute("error", e.getMessage());
			return "redirect:/competicao";
		}
		
		model.addAttribute("partida", p);

		return "detalheJogo";
	}

	@GetMapping("/new")
	public String novoJogo(ModelMap model, Authentication authentication, RedirectAttributes attr, HttpSession sessao) {

		Integer idUser = logado.getIdUserLogado(authentication);
		if (partidaService.userJaCompetiuHoje(idUser)) {

			attr.addFlashAttribute("success", "Já Participou da competição hoje!!! Aguarde até amanhã");
			return "redirect:/competicao";

		}

		Partida p;
		try {
			p = partidaService.iniciarPartida(idUser);
		} catch (Exception e) {
			attr.addFlashAttribute("error", "Erro ao iniciar partida, " + e.getMessage());
			return "redirect:/competicao";
		}

		model.addAttribute("idPartida", p.getId());
		model.addAttribute("jogo", p.getJogoList().get(0));
		model.addAttribute("pergunta", 1);
		sessao.setAttribute("tempoInicio", LocalDateTime.now());
		

		return "jogar";
	}

	@PostMapping("/next")
	public String nextJogo(@RequestParam(value = "cpResposta", required = true) String cpResposta,
			@RequestParam(value = "idp", required = true) String idp,
			@RequestParam(value = "idjogo", required = true) String idjogo,
			@RequestParam(value = "perg", required = true) String pergunta, ModelMap model,
			Authentication authentication, RedirectAttributes attr, HttpSession sessao) {

		Integer idUser = logado.getIdUserLogado(authentication);
		Partida p;
		try {
			int idPartida = Integer.parseInt(idp);
			int idJogo = Integer.parseInt(idjogo);
			int numPerg = Integer.parseInt(pergunta);
			double valor = Double.parseDouble(cpResposta);
			
			if (numPerg <=0 || numPerg>10) {
				throw new Exception("Erro na passagem de parâmetro");
			}

			p = partidaService.savePartida(idPartida, idUser, numPerg, idJogo, valor);

			if (numPerg == 10) {
				LocalDateTime inicio = (LocalDateTime) sessao.getAttribute("tempoInicio");
				partidaService.FinalizaPartida(idPartida, idUser, inicio);
				attr.addFlashAttribute("success", "Fim da participação por hoje, Até amanhã");
				return "redirect:/competicao/"+idPartida+"/detalhe";
			}

			numPerg++;
			model.addAttribute("idPartida", p.getId());
			model.addAttribute("jogo", p.getJogoList().get(numPerg - 1));
			model.addAttribute("pergunta", numPerg );

			return "jogar";
		} catch (Exception e) {
			attr.addFlashAttribute("error", "Erro ao salvar a resposta, " + e.getMessage());
			return "redirect:/competicao";
		}

	}

}
