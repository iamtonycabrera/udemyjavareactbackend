package com.cursoudemy.backendcursoudemy;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.cursoudemy.backendcursoudemy.models.responses.UserRest;
import com.cursoudemy.backendcursoudemy.security.AppProperties;
import com.cursoudemy.backendcursoudemy.shared.dto.UserDto;

@SpringBootApplication
@EnableJpaAuditing
public class BackendcursoudemyApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendcursoudemyApplication.class, args);
		System.out.println("It is working now!");
	}

    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoder(){
		return new BCryptPasswordEncoder();
	}

    @Bean
    SpringApplicationContext springApplicationContext(){
		return new SpringApplicationContext();
	}

    @Bean(name = "AppProperties")
    AppProperties getAppProperties(){
		return new AppProperties();
	}

    @Bean
    ModelMapper modelMapper(){

		ModelMapper mapper = new ModelMapper(); 
		
		mapper.typeMap(UserDto.class, UserRest.class).addMappings(m -> m.skip(UserRest::setPosts));

		return mapper;
	}

}