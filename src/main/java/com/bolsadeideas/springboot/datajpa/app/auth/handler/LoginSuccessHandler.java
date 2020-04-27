package com.bolsadeideas.springboot.datajpa.app.auth.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.SessionFlashMapManager;


@Component
//Heredando de la clase 'SavedRequestAwareAuthenticationSuccessHandler' se puede redirigir a la pagina anterior al login
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler{

	//Este metodo es un interceptor que se ejecuta despues de haber autenticado y antes de enviar la respuesta al cliente
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		//Es otra forma de registar un atributo en el flashRedirect para mandarlo a la vista redirigida
		SessionFlashMapManager flashMapManager = new SessionFlashMapManager();
		
		
		FlashMap flash = new FlashMap();
		flash.put("success", "Hola " + authentication.getName() + ", inició sesion correctamente!");
		
		logger.info("El usuario logeuado es: " + authentication.getName());
		
		flashMapManager.saveOutputFlashMap(flash, request, response);
		
		//Utilizando este metodo de la clase padre despues del logueo se redirige a la pagina anterior a este.
		//setUseReferer(true);
		
		super.onAuthenticationSuccess(request, response, authentication);
		
		
	}

	
}
