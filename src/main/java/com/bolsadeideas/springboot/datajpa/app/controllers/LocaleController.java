package com.bolsadeideas.springboot.datajpa.app.controllers;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LocaleController {
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	@GetMapping("/locale")
	public String locale(HttpServletRequest request) {
		//En el header "referer" del request esta la url desde donde se envio el request
		String ultimaUrl = request.getHeader("referer");
		log.info("La ulima url fue: " + ultimaUrl);
		return "redirect:" + ultimaUrl;
	}

}
