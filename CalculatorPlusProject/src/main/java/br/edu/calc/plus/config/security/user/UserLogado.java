/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.calc.plus.config.security.user;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 *
 * @author daves
 */
@Data
public class UserLogado extends User{

    private Integer id;
    private String nome, email;
    
    public UserLogado(Integer id, String nome, String email, String login, String senha, Set<GrantedAuthority> grantedAuthorities ) {
        super(login, senha, grantedAuthorities);
        this.id = id;
        this.nome = nome;
        this.email = email;
    }
    
}
