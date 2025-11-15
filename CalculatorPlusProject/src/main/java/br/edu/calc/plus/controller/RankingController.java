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
@RequestMapping("/ranking")
public class RankingController {

	@Autowired
	PartidaService partidaService;

	@Autowired
	LogadoUtil logado;

	@GetMapping("")
	public String ranking(ModelMap model, Authentication authentication) {

		model.addAttribute("lista", partidaService.getRanking());

		return "ranking";
	}
	
}
