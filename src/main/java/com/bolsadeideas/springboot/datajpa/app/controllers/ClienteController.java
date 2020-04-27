package com.bolsadeideas.springboot.datajpa.app.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bolsadeideas.springboot.datajpa.app.models.entity.Cliente;
import com.bolsadeideas.springboot.datajpa.app.models.service.IClienteService;
import com.bolsadeideas.springboot.datajpa.app.models.service.IUploadFileService;
import com.bolsadeideas.springboot.datajpa.app.util.paginator.PageRender;
import org.slf4j.Logger;


@Controller
//se guarda en la sesion el objeto 'cliente' cada vez que lo pasa a la vista al llamar los metodos crear, editar o listar, 
@SessionAttributes("cliente")
public class ClienteController {
	
	//Con esta objeto se pueden mostrar mensajes por consola en tiempo de ejecucion
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private IClienteService clienteService;
	
	@Autowired
	private IUploadFileService uploadFileService;
	
	//Permite obtener el valor del mensaje para el locale
	@Autowired
	private MessageSource messageSource;
	
	
	//Este metodo retorna en la respuesta la imagen envuelta en un objeto Response
	//con ".+" para que Spring no trunque la extencion del archivo
	//@Secured permite que solo el rol descrito pueda acceder al recurso 
	@Secured("ROLE_USER")
	@GetMapping(value = "/uploads/{filename:.+}")
	public ResponseEntity<Resource> cargarFoto(@PathVariable(name = "filename") String filename) {
		
		Resource recurso = null;
		
		try {
			recurso = uploadFileService.load(filename);
			if(!recurso.exists() || !recurso.isReadable()) {
				throw new RuntimeException("ERROR: No se puede cargar la imagen: " + recurso.getFilename());
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"")
				.body(recurso);	
	}
	
	@Secured("ROLE_USER")
	@GetMapping("/ver/{id}")
	public String ver(@PathVariable(name = "id") Long id, RedirectAttributes flash, Model model, Locale locale) {
		
		Cliente cliente = clienteService.fetchByIdWithFacturas(id); //clienteService.findOne(id);
		
		if(cliente == null) {
			flash.addFlashAttribute("danger", "No se encontro un cliente con id: " + id);
			return "redirect:/listar";
		}
		
		model.addAttribute("titulo", messageSource.getMessage("text.cliente.ver.titulo", null, locale) + " " + cliente.getNombre());
		model.addAttribute("cliente", cliente);
		
		return "ver";
	}
	
	@GetMapping(value= "/listar-rest")
	@ResponseBody
	public List<Cliente> listarRest() {
		return clienteService.findAll();
	}
	
	
	//Con el objeto authetication se puede obtener el usuario autenticado
	@RequestMapping(value= {"/", "/listar"}, method = RequestMethod.GET)
	public String listar(@RequestParam(name="page", defaultValue = "0") int page, Model model,
			Authentication authentication,
			HttpServletRequest request,
			Locale locale) {
		
		if(authentication != null) {
			log.info("Obteniendo desde parametro inyectado el usuario logeado: " + authentication.getName());
		}
		
		Authentication aut = SecurityContextHolder.getContext().getAuthentication();
		
		if(aut != null) {
			log.info("Obteniendo estaticamente el usuario logeado: " + aut.getName());
		}
		
		//Validando el rol con el metodo 'hasRole()'
		if(hasRole("ROLE_ADMIN")){
			log.info("El usuario: " + aut.getName() + " SI tiene permiso!");
		}else {
			log.info("El usuario: " + aut.getName() + " NO tiene permiso!");
		}
		
		//EL contructor de la clase recibe como paremetro el objeto 'request' y una cadena con el prefijo del rol
		SecurityContextHolderAwareRequestWrapper securityContext = new SecurityContextHolderAwareRequestWrapper(request, "");
		
		//Validado el rol con el objeto securityContext
		if(securityContext.isUserInRole("ROLE_ADMIN")){
			log.info("De la forma securityContext -> El usuario: " + aut.getName() + " SI tiene permiso!");
		}else {
			log.info("De la forma securityContext -> El usuario: " + aut.getName() + " NO tiene permiso!");
		}
		
		//Validado el rol con el objeto request
		if(request.isUserInRole("ROLE_ADMIN")){
			log.info("De la forma httpServletRequest -> El usuario: " + aut.getName() + " SI tiene permiso!");
		}else {
			log.info("De la forma httpServletRequest -> El usuario: " + aut.getName() + " NO tiene permiso!");
		}
		
		
		
		
		log.info("Listando clientes!");
		
		Pageable pageRequest = PageRequest.of(page, 5);
		Page<Cliente> clientes = clienteService.findAll(pageRequest);
		
		PageRender<Cliente> pageRender = new PageRender<>("/listar", clientes);
		
		model.addAttribute("titulo", messageSource.getMessage("text.cliente.listar.titulo", null, locale));
		model.addAttribute("clientes", clientes);
		//model.addAttribute("clientes", clienteService.findAll());
		model.addAttribute("page", pageRender);
		model.addAttribute("nPagina", page);//añadido para tomar el numero de la pagina actual para generar el CSV
		return "listar";
	}
	
	@Secured("ROLE_ADMIN")
	@GetMapping("/form")
	public String crear(Model model, Locale locale) {
		
		Cliente cliente = new Cliente();
		model.addAttribute("titulo", messageSource.getMessage("text.cliente.form.titulo", null, locale));
		model.addAttribute("boton",messageSource.getMessage("text.cliente.form.button.crear", null, locale));
		model.addAttribute("cliente", cliente);
		return "form";
	}
	
	@Secured("ROLE_ADMIN")
	@GetMapping("/form/{id}")
	public String editar(@PathVariable(name="id") Long id, Model model, RedirectAttributes flash, Locale locale) {
		
		Cliente cliente = null;
		if(id > 0) {
			cliente = clienteService.findOne(id);
			if(cliente == null) {
				flash.addFlashAttribute("warning", "No se encotro un cliente con ID ".concat(Long.toString(id)));
				return "redirect:/listar";
			}
		}else {
			flash.addFlashAttribute("danger", "El ID del cliente no puede ser 0!");
			return "redirect:/listar";
		}
		
		model.addAttribute("titulo", messageSource.getMessage("text.cliente.form.titulo", null, locale));
		model.addAttribute("boton",messageSource.getMessage("text.cliente.form.button.editar", null, locale));
		model.addAttribute("cliente", cliente);
		return "form";
	}
	
	
	//Se anota el objeto cliente con @VAlid para hablitar la validacion de los campos 
	//con el objeto Bindingresult se puede validar si hay algun error y hacer una accion(enviar mensajes a la vista)
	//El objeto Cliente se pasa auto.. si la vista anterior tenia como atributo un objeto del mismo nombre de la clase
	//El objeto status permite manipular el estado de la session del cliente(Navegador)
	//El objeto flash permite enviar atributos a una vista redirigida
	//MultipartFile recibe el archivo enviado por un parametro en el request para luego procesarlo
	@Secured("ROLE_ADMIN")
	@RequestMapping(value="/form", method = RequestMethod.POST)
	public String guardar(@Valid Cliente cliente, BindingResult result, Model model, @RequestParam("file") MultipartFile foto, RedirectAttributes flash, SessionStatus status) {
		if(result.hasErrors()) {
			model.addAttribute("titulo", "Formulario de cliente");
			return "form";
		}
		
		
		if(!foto.isEmpty()) {
			
			if(cliente.getId()!=null && !cliente.getFoto().isEmpty() && cliente.getFoto() != null) {
				
				if(uploadFileService.delete(cliente.getFoto())) {
					flash.addFlashAttribute("warning", "La foto antigua se eliminó con exito!");
				}	
			}
			
			String uniqueFilename = null;
			
			try {				
				uniqueFilename = uploadFileService.copy(foto);			
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			flash.addFlashAttribute("info", "Se guardó correctamente la imagen: " + uniqueFilename);	
			cliente.setFoto(uniqueFilename);
			
		}else {
			if(cliente.getId()== null) {
				cliente.setFoto("");
			}
		}
		
		Long idPrePersist = cliente.getId();
		clienteService.save(cliente);
		
		String mensajeFlash = (idPrePersist == null)? "Cliente creado con exito!" : "Cliente editado con exito!";
		flash.addFlashAttribute("success", mensajeFlash);
		
		//marca la sesion como completa, la elimina junto con sus objetos (cliente)
		status.isComplete();
		
		return "redirect:listar";
	}
	
	@Secured("ROLE_ADMIN")
	@RequestMapping("/eliminar/{id}")
	public String eliminar(@PathVariable(name="id") Long id, RedirectAttributes flash) {
		if(id > 0) {
			
			Cliente cliente = clienteService.findOne(id);
			
			if(cliente != null) {
				String fotoName = cliente.getFoto();
				
				if(!fotoName.isEmpty()) {
					
					if(uploadFileService.delete(fotoName)) {
						flash.addFlashAttribute("info", "La foto " + fotoName + " se eliminó con exito!");
					}
				}
				clienteService.delete(id);
				flash.addFlashAttribute("success", "Cliente eliminado con exito!");
			}else {
				flash.addFlashAttribute("danger", "No exite un cliente con ID: " + id);
			}
			
		}else {
			flash.addFlashAttribute("danger", "No se pude eliminar un cliente con ID incorrecto!");
		}
		
		return "redirect:/listar";
	}
	
	//Ete metodo valida si un usuario tiene un determinado rol
	public boolean hasRole(String role) {
		
		SecurityContext securityContext = SecurityContextHolder.getContext(); 
		
		if(securityContext == null) {
			return false;
		}
		
		Authentication authentication = securityContext.getAuthentication();
		
		if(authentication == null) {
			return false;
		}
		
		//Autorities son los roles que tiene el usuario logueado.
		//Nota: La coleccion contiene objetos de cualquier tipo que herede de GrantedAuthority
		Collection<? extends GrantedAuthority> autorities = authentication.getAuthorities();
		
		boolean tieneRole = false;
		
		for(GrantedAuthority autoritie : autorities) {
			log.info("El usuario: " + authentication.getName() + " - tiene el rol: " + autoritie.getAuthority());
			
			if(role.equals(autoritie.getAuthority())) {
				tieneRole = true;
			}
			
		}
		
		
		return tieneRole;
	}
	

}
