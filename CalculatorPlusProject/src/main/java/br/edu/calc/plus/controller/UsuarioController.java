package br.edu.calc.plus.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.calc.plus.domain.dto.UserDTO;
import br.edu.calc.plus.service.UsuarioService;

//@PreAuthorize("isAuthenticated()")
@Controller
@RequestMapping("/user")
public class UsuarioController {
    
    @Autowired
    UsuarioService userService;
    @Autowired
    private PasswordEncoder password;
   
    
    @GetMapping("")
    public String cadastro(@ModelAttribute("usuario") UserDTO usuario,
            ModelMap model) {
        return "cadastro";
    }

    @PostMapping("")
    public String saveUser( @Valid @ModelAttribute("usuario") UserDTO usuario, BindingResult result, 
            RedirectAttributes attr, ModelMap model,Authentication authentication) {

        if (result.hasErrors()) {
            return "cadastro";
        }
        
        usuario.setSenha( password.encode( usuario.getSenha() ) );
        
        userService.save(usuario);
                
        attr.addFlashAttribute("success", "cadastro criado com sucesso.");
        
        return "redirect:/home";
    }


}
