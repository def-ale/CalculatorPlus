package br.edu.calc.plus.domain.dto;

import java.time.LocalDateTime;

import br.edu.calc.plus.util.Util;
import groovy.transform.builder.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JogoListDTO {

	private int idPartida;

	private LocalDateTime dataJogo;

	private double bonificacao;

	private int acertos, erros;
	
	private long tempo;

	public String getDataFormatada() {
		return Util.formatarData(dataJogo);
	}

}
