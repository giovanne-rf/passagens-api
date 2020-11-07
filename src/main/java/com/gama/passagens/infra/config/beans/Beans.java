package com.gama.passagens.infra.config.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.amadeus.Amadeus;

@Configuration
public class Beans {
	@Bean
    public BCryptPasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }
	@Bean
    public Amadeus amadeus(){
        return Amadeus
				.builder("RZ3pbc22VwLAXtXqIU80DmT2hv1M2J8y", "zVyCOrYgU6ThENBK")
				.build();
    }
}
