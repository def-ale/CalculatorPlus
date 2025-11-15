package br.edu.calc.plus.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RankingDTO {

	private Integer idUser;
	
	private String nome;
	
	private double bonusTotal;
	
	private long competicoes;
	
	private long tempoTotal;
	
}

