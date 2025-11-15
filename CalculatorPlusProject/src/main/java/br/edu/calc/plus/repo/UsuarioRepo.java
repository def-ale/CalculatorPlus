package br.edu.calc.plus.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.calc.plus.domain.Usuario;
import java.util.Optional;

public interface UsuarioRepo extends JpaRepository<Usuario, Integer>{

    public Optional<Usuario> findByNome(String nome);

	public Optional<Usuario> findByLogin(String login);
    

}
