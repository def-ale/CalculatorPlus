package br.edu.calc.plus.repo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.edu.calc.plus.domain.Partida;
import br.edu.calc.plus.domain.dto.JogoListDTO;
import br.edu.calc.plus.domain.dto.RankingDTO;

public interface PartidaRepo extends JpaRepository<Partida, Integer>{

	String src = "br.edu.calc.plus.domain.dto.";
	
	@Query("select sum(p.bonificacao) from Partida p")
	Double getAllBonus();

	List<Partida> findByUsuarioId(int idUserLogado);

	@Query("select count(p) from Partida p where p.usuario.id = :id and p.data >= :paran and p.data <= :paran1")
	long getUsuarioCompetil(@Param("id")int id, @Param("paran") LocalDateTime dtIni, @Param("paran1") LocalDateTime dtFim);

	Partida findByIdAndUsuarioId(int idPergunta, Integer idUser);

	
	@Query("select new "+src+"RankingDTO(p.usuario.id, p.usuario.nome, sum(p.bonificacao) as tot, count(p), sum(p.tempo)) "
			+ " from Partida p group by p.usuario.nome order by tot desc")
	List<RankingDTO> getRanking();

   
    

}
