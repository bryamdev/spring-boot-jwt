package com.bolsadeideas.springboot.datajpa.app.controllers;

import java.security.Principal;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {
	
	@Autowired
	private MessageSource messageSource;
	
	//Principal contiene el usuario logeado
	@GetMapping("/login")
	public String login(@RequestParam(name = "error", required = false) String error,
			@RequestParam(name = "logout", required = false) String logout,
			Model model, Principal principal, 
			RedirectAttributes flash, Locale locale) {
		
		if(error != null) {
			model.addAttribute("danger", "Credenciales incorrectas!");
		}
		if(logout != null) {
			model.addAttribute("info", "Ha cerrado sesion con exito!");
		}
		
		if(principal != null) {
			flash.addFlashAttribute("info", "El usuario ya esta logeado!");
			return "redirect:/";
		}
		
		model.addAttribute("titulo", messageSource.getMessage("text.login.titulo", null, locale));
		model.addAttribute("boton", messageSource.getMessage("text.login.button", null, locale));
		return "login";
	}

}
