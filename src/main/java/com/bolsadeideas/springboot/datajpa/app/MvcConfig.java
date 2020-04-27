package com.bolsadeideas.springboot.datajpa.app;


import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

//esta clase permite añadir la ruta de un recurso estatico externo para que sea accesible desde la vista
@Configuration
public class MvcConfig implements WebMvcConfigurer{

	
	//private final Logger log = LoggerFactory.getLogger(getClass());

	/*
	
	//Este metodo permite añadir un recurso estatico al proyecto
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// TODO Auto-generated method stub
		WebMvcConfigurer.super.addResourceHandlers(registry);
		
		//'toUri' concatena el path absoluto con el subfijo 'file:/'
		String resourcePath = Paths.get("uploads").toAbsolutePath().toUri().toString(); 
		
		//con '/uploads/' se puede acceder desde la vista a los recursos externos.
		
		/*
		* registry.addResourceHandler("/uploads/**")
		* .addResourceLocations("file:/C:/temp/uploads/");
		*/
		
	
	/*
		registry.addResourceHandler("/uploads/**")
		.addResourceLocations(resourcePath);
		
		log.info("Añadiendo al proyecto el recurso estatico: " + resourcePath);
	}
	*/
	
	//Permite registrar controladores estaticos que sirven para solo cargar la vista
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		
		registry.addViewController("/error_403").setViewName("error_403");	
	}
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
		
	//Ese metodo se encarga de guardar el locale (conf regional) en la sesion
	@Bean
	public LocaleResolver localeResolver() {
		//El locale en este caso se guarda en la session
		SessionLocaleResolver localeResolver = new SessionLocaleResolver();
		localeResolver.setDefaultLocale(new Locale("es", "ES"));
		
		return localeResolver;
	}
	
	//INTERCEPTOR
	//se encarga de guardar el nuevo locale
	//Se ejecuta antes de ejecutar unmetodo Handler de algun controlador
	//... si en la peticion viene un parametro especifico (lang)
	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		
		LocaleChangeInterceptor localeInterceptor = new LocaleChangeInterceptor();
		localeInterceptor.setParamName("lang");
		
		return localeInterceptor;
	}
	
	//Registra el interceptor
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// TODO Auto-generated method stub
		registry.addInterceptor(localeChangeInterceptor());
	}
	
	//Metodo para registar el bean para convertir objetos a XML(marshalizar)	
	@Bean
	public Jaxb2Marshaller Jaxb2Marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setClassesToBeBound(new Class[] {com.bolsadeideas.springboot.datajpa.app.view.xml.ClienteList.class});
		return marshaller;
	}


	

}
