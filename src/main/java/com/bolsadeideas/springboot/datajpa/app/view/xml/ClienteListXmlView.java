package com.bolsadeideas.springboot.datajpa.app.view.xml;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.xml.MarshallingView;

import com.bolsadeideas.springboot.datajpa.app.models.entity.Cliente;

@Component("listar.xml")
public class ClienteListXmlView extends MarshallingView{
	
	
	@Autowired	
	public ClienteListXmlView(Jaxb2Marshaller marshaller) {
		super(marshaller);
	}


	//Este metodo convierte todo lo que esta en el 'model' a XML
	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		@SuppressWarnings("unchecked")
		Page<Cliente> clientes = (Page<Cliente>) model.get("clientes");  
		
		//model.remove("titulo");
		//model.remove("page");
		//model.remove("clientes");
		
		
		model.put("clientesList", new ClienteList(clientes.getContent()) );
		
		super.renderMergedOutputModel(model, request, response);
	}
	
	
}
