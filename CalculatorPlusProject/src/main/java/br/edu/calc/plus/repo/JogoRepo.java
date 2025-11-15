package br.edu.calc.plus.repo;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.edu.calc.plus.domain.Jogo;
import br.edu.calc.plus.domain.dto.JogoListDTO;

public interface JogoRepo extends JpaRepository<Jogo, Integer>{

	String src = "br.edu.calc.plus.domain.dto.";
	
	@Query("select count(j) from Jogo j where j.resultado <> j.resposta")
	long getAllErros();

	@Query("select count(j) from Jogo j where j.resultado = j.resposta")
	long getAllAcertos();

	@Query("select count(j) from Jogo j where j.resultado = j.resposta and j.partida.data >= :paran and j.partida.data <= :paran1")
	long getAcertosDia(@Param("paran") LocalDateTime dtIni, @Param("paran1") LocalDateTime dtFim);

	@Query("select count(j) from Jogo j where j.resultado <> j.resposta and j.partida.data >= :paran and j.partida.data <= :paran1")
	long getErrosDia(@Param("paran") LocalDateTime dtIni, @Param("paran1") LocalDateTime dtFim);

	@Query("select count(j) from Jogo j where j.resultado = j.resposta and j.partida.usuario.id = :id")
	long getAllAcertosUser(@Param("id") int id);

	
//	@Query("select new "+src+"JogoListDTO(j.partida.id, j.partida.data, j.partida.bonificacao, 0, 0 )  from Jogo j where j.partida.usuario.id = :id")
//	List<JogoListDTO> getMeusJogos(@Param("id") long idUserLogado);

	
   
    

}
