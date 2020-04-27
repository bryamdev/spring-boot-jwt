package com.bolsadeideas.springboot.datajpa.app.view.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.bolsadeideas.springboot.datajpa.app.models.entity.Cliente;

//Esta es una clase Wrapper que sera convertida/marshalizada a XML
@XmlRootElement(name = "clientes")
public class ClienteList {
	
	//Representa el nombre de cada elemento de la lista en el XML
	@XmlElement(name = "cliente")
	private List<Cliente> clientes;

	public ClienteList() {

	}

	public ClienteList(List<Cliente> clientes) {
		super();
		this.clientes = clientes;
	}

	public List<Cliente> getClientes() {
		return clientes;
	}

}
