package br.edu.calc.plus.service;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.calc.plus.domain.Usuario;
import br.edu.calc.plus.domain.dto.UserDTO;
import br.edu.calc.plus.repo.UsuarioRepo;

@Service
public class UsuarioService {

	
	 @Autowired
	 private UsuarioRepo uDao;
	 
	 
	public long getCountUsers() {
		
		return uDao.count();
	}


	@Transactional
	public void save(@Valid UserDTO usuario) {
		
		Usuario user = usuario.ConvertUsuario();
		
		uDao.save(user);
		
	}

}
