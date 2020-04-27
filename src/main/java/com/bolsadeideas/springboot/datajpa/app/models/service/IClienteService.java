package com.bolsadeideas.springboot.datajpa.app.models.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bolsadeideas.springboot.datajpa.app.models.entity.Cliente;
import com.bolsadeideas.springboot.datajpa.app.models.entity.Factura;
import com.bolsadeideas.springboot.datajpa.app.models.entity.Producto;

public interface IClienteService {
	

	public List<Cliente> findAll();
	
	//Un Page<> es un Iterable subconjunto de la consulta
	//Debe importarse de spring.data.domain
	public Page<Cliente> findAll(Pageable pageable);

	public void save(Cliente cliente);

	public Cliente findOne(Long id);
	
	public Cliente fetchByIdWithFacturas(Long id);

	public void delete(Long id);
	
	//Metodos dao de Producto
	public List<Producto> findByNombre(String term);
	public List<Producto> findByNombreIgnoreCase(String term);
	
	public Producto findProductoByid(Long id);

	//Metodos dao de Factura
	
	public void saveFactura(Factura factura);
	
	public Factura findFacturaById(Long id);
	
	public Factura fetchByIdWithClienteWithItemFacturaWithProducto(Long id);
	
	public void deleteFactura(Long id);

}
