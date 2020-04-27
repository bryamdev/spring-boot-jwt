package com.bolsadeideas.springboot.datajpa.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.bolsadeideas.springboot.datajpa.app.models.service.IUploadFileService;

@SpringBootApplication
public class SpringBootDataJpaApplication implements CommandLineRunner {

	@Autowired
	IUploadFileService uploadFileService;
	
	@Autowired
	private BCryptPasswordEncoder bCryptEncoder;
		
	private Logger log = LoggerFactory.getLogger(getClass());
	
	public static void main(String[] args) {
		SpringApplication.run(SpringBootDataJpaApplication.class, args);
	}

	//Este metodo se ejecuta cada vez que la aplicacion se inicia
	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		uploadFileService.deleteAll();
		uploadFileService.init();	
		
		log.info("Se inicializó el directorio");
		
		String password = "12345";
		String encodedPassword = bCryptEncoder.encode(password);
		
		System.out.println(bCryptEncoder.matches(password, encodedPassword));
		
		for(int i=0; i<2; i++) {
			String encodedPass = bCryptEncoder.encode(password);
			System.out.println("EncondedPass "+ i + ": " + encodedPass);
		}
	}

}
