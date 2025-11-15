package br.edu.calc.plus.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.calc.plus.service.JogoService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
public class ApiResource {


    @Autowired
    JogoService jogoService;
	
	@GetMapping("/data")
	public ResponseEntity<long[][]> getDataGraph() {

		long[][] vetor = new long[2][10];

            vetor[1] = jogoService.getAcertosUltimos10Dias();
            vetor[0] = jogoService.getErrosUltimos10Dias();
            
            return ResponseEntity.ok(vetor);

	}

	
}
