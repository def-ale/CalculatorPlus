package br.edu.calc.plus.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import br.edu.calc.plus.domain.dto.UserDTO;
import br.edu.calc.plus.service.JogoService;
import br.edu.calc.plus.service.PartidaService;
import br.edu.calc.plus.service.UsuarioService;
import br.edu.calc.plus.util.LogadoUtil;

@Controller
public class HomeController {

    @Autowired
    LogadoUtil userLogado;
    
    @Autowired
    UsuarioService userService;
    @Autowired
    PartidaService partidaService;
    @Autowired
    JogoService jogoService;
        
    
    @RequestMapping(path = {"/", "/home"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String home(Authentication authentication, ModelMap model) {

        //model.addAttribute("nome", userLogado.getNomeUserLogado(authentication) );
        model.addAttribute("numeroUsers", userService.getCountUsers() );
        model.addAttribute("premiacaoUsers", partidaService.getPremioUsers() );
        model.addAttribute("numeroErros",  jogoService.getAllErros() );
        model.addAttribute("numeroAcertos", jogoService.getAllAcertos() );
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @RequestMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("msgError", "Login ou senha incorreta");
        return "login";
    }

}
