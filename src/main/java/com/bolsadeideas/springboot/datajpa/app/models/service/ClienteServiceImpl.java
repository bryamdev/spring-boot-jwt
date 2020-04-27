package com.bolsadeideas.springboot.datajpa.app.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bolsadeideas.springboot.datajpa.app.models.dao.IClienteDao;
import com.bolsadeideas.springboot.datajpa.app.models.dao.IFacturaDao;
import com.bolsadeideas.springboot.datajpa.app.models.dao.IProductoDao;
import com.bolsadeideas.springboot.datajpa.app.models.entity.Cliente;
import com.bolsadeideas.springboot.datajpa.app.models.entity.Factura;
import com.bolsadeideas.springboot.datajpa.app.models.entity.Producto;

@Service
public class ClienteServiceImpl implements IClienteService{
	
	@Autowired
	IClienteDao clienteDao;
	
	@Autowired
	IProductoDao productoDao;
	
	@Autowired
	IFacturaDao facturaDao;

	
	@Override
	@Transactional(readOnly=true)//Envuelve el contenido del metodo dentro de una transaccion
	public List<Cliente> findAll() {
		
		return (List<Cliente>) clienteDao.findAll();
	}
	
	@Override
	@Transactional(readOnly = true)
	public Cliente findOne(Long id) {
		
		//Si no existe el registro en la DB con ese id retorna un objeto nulo
		return clienteDao.findById(id).orElse(null);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Cliente fetchByIdWithFacturas(Long id) {
		return clienteDao.fetchByIdWithFacturas(id);
	}

	
	@Override
	@Transactional
	public void save(Cliente cliente) {
		
		clienteDao.save(cliente);
		
	}

	
	@Override
	@Transactional
	public void delete(Long id) {
		
		clienteDao.deleteById(id);		
	}

	
	@Override
	@Transactional(readOnly=true)
	public Page<Cliente> findAll(Pageable pageable) {
		
		
		return clienteDao.findAll(pageable);
	}

	
	//Metodos dao de Producto
	@Override
	@Transactional(readOnly = true)
	public List<Producto> findByNombre(String term) {
		
		return productoDao.findByNombre(term);
	}

	
	@Override
	@Transactional(readOnly = true)
	public List<Producto> findByNombreIgnoreCase(String term) {
		
		return productoDao.findByNombreLikeIgnoreCase("%" + term + "%");
	}

	
	@Override
	@Transactional(readOnly = true)
	public Producto findProductoByid(Long id) {
		
		return productoDao.findById(id).orElse(null);
	}

	//Metodos dao Factura

	
	@Override
	@Transactional
	public void saveFactura(Factura factura) {

		facturaDao.save(factura);
	}

	
	@Override
	@Transactional(readOnly = true)
	public Factura findFacturaById(Long id) {
		
		return facturaDao.findById(id).orElse(null);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Factura fetchByIdWithClienteWithItemFacturaWithProducto(Long id) {
		
		return facturaDao.fetchByIdWithClienteWithItemFacturaWithProducto(id);
	}
	
	
	@Override
	@Transactional
	public void deleteFactura(Long id) {
			
		facturaDao.deleteById(id);;
		
	}
	
	

}
