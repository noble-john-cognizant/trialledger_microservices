package com.cts.trialledger.provenance;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ProvenanceServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProvenanceServiceApplication.class, args);
	}

}
