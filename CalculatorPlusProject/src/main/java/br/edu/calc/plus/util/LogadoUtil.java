/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.calc.plus.util;

import br.edu.calc.plus.config.security.user.UserLogado;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

/**
 *
 * @author daves
 */
@Service
public class LogadoUtil {
	 
    public Integer getIdUserLogado(Authentication authentication){
     
        return ((UserLogado)authentication.getPrincipal()).getId();
    }
     
    public String getNomeUserLogado(Authentication authentication){
     
        return ((UserLogado)authentication.getPrincipal()).getNome();
    }
     
    public String getEmailUserLogado(Authentication authentication){
     
        return ((UserLogado)authentication.getPrincipal()).getEmail();
    }
     
    public boolean eHAdministrador(Authentication authentication){     
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
    
}
