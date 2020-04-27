package com.bolsadeideas.springboot.datajpa.app.controllers;

import java.util.List;
import java.util.Locale;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bolsadeideas.springboot.datajpa.app.models.entity.Cliente;
import com.bolsadeideas.springboot.datajpa.app.models.entity.Factura;
import com.bolsadeideas.springboot.datajpa.app.models.entity.ItemFactura;
import com.bolsadeideas.springboot.datajpa.app.models.entity.Producto;
import com.bolsadeideas.springboot.datajpa.app.models.service.IClienteService;

@Secured("ROLE_ADMIN")
//@PreAuthorize("hasRole('ROLE_ADMIN')")
@Controller
@RequestMapping("/factura")
@SessionAttributes("factura")//el objeto se mantiene en la sesion hasta que sea persistido(metodo guardar)
public class FacturaController {
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private IClienteService clienteService;
	
	@Autowired
	private MessageSource messageSource;
	
	@GetMapping("/form/{clienteId}")
	public String crear(@PathVariable("clienteId") Long clienteId, Model model, RedirectAttributes flash, Locale locale) {
		
		Cliente cliente = clienteService.findOne(clienteId);
		
		if(cliente == null) {
			flash.addFlashAttribute("danger", "El usaurio no exite en la db!");
			return "redirect:/listar";
		}
		
		Factura factura = new Factura();
		factura.setCliente(cliente);
		
		model.addAttribute("titulo", messageSource.getMessage("text.factura.form.titulo", null, locale));
		model.addAttribute("factura", factura);
		
		return "factura/form";
	}
	
	//produces indica el tipo de respuesta se va a enviar en el cuerpo de la respuesta
	//ResponseBody evita que se carge una vista y pobla en el cuerpo del body de la respuesta el json
	@GetMapping(value = "/cargar-productos/{term}", produces = {"application/json"})
	public @ResponseBody List<Producto> cargarProductos(@PathVariable("term") String term){
		log.info("Se esta consultando un producto con el termino: " + term);
		return clienteService.findByNombreIgnoreCase(term);
	}
	
	
	
	@PostMapping("/form")
	public String guardarFactura(@Valid Factura factura, 
			BindingResult result,
			Model model,
			@RequestParam(name = "item_id[]", required = false) Long[] itemId,
			@RequestParam(name = "cantidad[]", required = false) Integer[] cantidad,
			RedirectAttributes flash,
			SessionStatus status) {
		
		if(result.hasErrors()) {
			model.addAttribute("titulo", "Formulario de factura");
			return "factura/form";
		}
		
		if(itemId == null || itemId.length == 0) {
			model.addAttribute("titulo", "Formulario de factura");
			model.addAttribute("danger", "La factura debe tener lineas");
			return "factura/form";
		}
		
		
		for(int i = 0; i<itemId.length; i++) {
			Producto producto = clienteService.findProductoByid(itemId[i]);
			
			ItemFactura item = new ItemFactura();
			item.setProducto(producto);
			item.setCantidad(cantidad[i]);
			
			factura.agregarLineas(item);
		}
		
		clienteService.saveFactura(factura);
		status.isComplete();
		flash.addFlashAttribute("info", "Se guardó la factura");
		
		
		return "redirect:/ver/"+ factura.getCliente().getId();
	}
	
	
	@GetMapping("/ver/{id}")
	public String ver(@PathVariable(name = "id") Long id, Model model, RedirectAttributes flash, Locale locale) {
		
		 
		if(id > 0) {
			Factura factura = clienteService.fetchByIdWithClienteWithItemFacturaWithProducto(id); //clienteService.findFacturaById(id);
			if(factura == null) {
				flash.addFlashAttribute("danger", "La factura no existe en la db!");
				return "redirect:/listar";
			}
			
			model.addAttribute("titulo", messageSource.getMessage("text.factura.ver.titulo", null, locale) + " " + factura.getDescripcion());
			model.addAttribute("factura", factura);
		}else {
			flash.addFlashAttribute("danger", "Id de factura erroneo!");
			return "redirect:/listar";
		}
		
		
		return "factura/ver";
	}
	
	@RequestMapping("/eliminar/{id}")
	public String eliminar(@PathVariable(name = "id") Long id, RedirectAttributes flash) {
		
		Factura factura = clienteService.findFacturaById(id);
		
		if(factura != null) {
			clienteService.deleteFactura(id);
			flash.addFlashAttribute("success", "Factura eliminada con exito!");
			return "redirect:/ver/" + factura.getCliente().getId();
			
		}
		flash.addFlashAttribute("danger", "No se pudo eliminar la factura, no existe en la db!");
		
		return "redirect:/listar";
	}
	
	
	
	//Metodo de ejemplo
	@GetMapping("/producto/{term}")
	public String obtenerPorductos(@PathVariable("term") String term, Model model) {
		
		List<Producto> productos = clienteService.findByNombre(term);
		
		model.addAttribute("titulo", "Listado de produtos");
		model.addAttribute("productos", productos);
		
		return "factura/ver";
	}
	 
}
