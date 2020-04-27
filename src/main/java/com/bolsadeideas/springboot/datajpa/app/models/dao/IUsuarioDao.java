package com.bolsadeideas.springboot.datajpa.app.models.dao;

import org.springframework.data.repository.CrudRepository;

import com.bolsadeideas.springboot.datajpa.app.models.entity.Usuario;

public interface IUsuarioDao extends CrudRepository<Usuario, Long>{
	
	public Usuario findByUsername(String username);

}
