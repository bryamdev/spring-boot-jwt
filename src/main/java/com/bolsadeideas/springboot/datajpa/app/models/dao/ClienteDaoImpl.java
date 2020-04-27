package com.bolsadeideas.springboot.datajpa.app.models.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bolsadeideas.springboot.datajpa.app.models.entity.Cliente;

//Esta clase NO esta en uso y es la implimentacion de una interfaz para hacer uso de EntiyManager
//Queda como ejemplo de como usar el objeto EntityManager y las anotaciones
@Repository
public class ClienteDaoImpl {
	
	//inyecta la instancia con la configuracion de persisntencia de spring(Datasource...)
	@PersistenceContext
	private EntityManager em;
	
	@SuppressWarnings("unchecked")
	//@Override
	public List<Cliente> findAll() {
		
		//se pasa como parametro el nombre de la clase que mapea la tabla
		return em.createQuery("from Cliente").getResultList();
	}
	
	//@Override
	public Cliente findOne(Long id) {
		return em.find(Cliente.class, id);
	}

	//@Override
	public void save(Cliente cliente) {
		
		if(cliente.getId() != null && cliente.getId() > 0) {
			em.merge(cliente);//actualizar
		}else {
			em.persist(cliente);//insertar
		}
	}

	//@Override
	@Transactional
	public void delete(Long id) {
		Cliente cliente = findOne(id);
		em.remove(cliente);		
	}	

}
