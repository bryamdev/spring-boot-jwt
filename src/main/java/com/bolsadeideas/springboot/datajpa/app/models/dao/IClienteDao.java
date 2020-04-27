package com.bolsadeideas.springboot.datajpa.app.models.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.bolsadeideas.springboot.datajpa.app.models.entity.Cliente;

//CrudRepository es una interfaz de la api spring data-jpa
//Es de tipo generico se especifica <tipo de entidad, tipo de llave primaria>
//No hace falta anotarla como componente, ni definir ningun metodo abstracto solo si son para consultas personalizadas(metodos)
//Modificacion: PagingAndSortingRepository añade paginacion de resultados
public interface IClienteDao extends PagingAndSortingRepository<Cliente, Long>{
	
	//Con inner join tienen que existir datos en ambas tablas...
	//... left join trae el registro asi no exista datos en la tabla secundaria
	//El fetch indica que se hace una consulta a las tablas relacionadas en un solo fecth o consulta
	@Query("select c from Cliente c left join fetch c.facturas f where c.id = ?1")
	public Cliente fetchByIdWithFacturas(Long id);
	
	/*
	//Metodos para interface a implemetar con EntityManager
	public List<Cliente> findAll();
	
	public void save(Cliente cliente);
	
	public Cliente findOne(Long id);
	
	public void delete(Long id);
	*/

}
