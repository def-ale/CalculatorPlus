/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.calc.plus.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 *
 * @author daves
 */
public class Util {

    public static Date LocalDateTimeToDate(LocalDateTime dateToConvert) {
        return java.sql.Timestamp.valueOf(dateToConvert);
    }

    public static String formatarMoeda(double valor) {
        return new DecimalFormat("Cr$ #,##0.00").format(valor);
    }

    public static String formatEmK(long numero) {
        if (numero >= 1000) {
            int valor = (int) (numero / 1000);
            return "+" + valor + "k";
        } else {
            return numero + "";
        }

    }

    public static Date toDate(String parameter) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        return sdf.parse(parameter);
    }

    public static String formatarData(Date data) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        return sdf.format(data);
    }

    public static BigDecimal truncateDecimal(double x, int numberofDecimals) {
        if (x > 0) {
            return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_FLOOR);
        } else {
            return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_CEILING);
        }
    }

    public static long tempoEmMinutos(LocalTime entrada, LocalTime saida) {
        Duration duracao = Duration.between(entrada, saida);
        return duracao.toMinutes();
    }

	public static String formatarData(LocalDateTime data) {
				
		return data.format( DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss") );
	}

}
