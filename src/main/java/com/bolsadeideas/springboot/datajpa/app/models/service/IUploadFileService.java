package com.bolsadeideas.springboot.datajpa.app.models.service;

import java.io.IOException;
import java.net.MalformedURLException;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface IUploadFileService{

	//Obtiene la imagen del sitema de archivos y la convierta a recurso para la respuesta con ResponseEntity
	public Resource load(String filename) throws MalformedURLException;
	
	//Copia el archivo al nuevo directorio y retorna su nuevo nombre
	public String copy(MultipartFile file) throws IOException;
	
	//Elimina la imagen del directorio
	public boolean delete(String filename);
	
	public void deleteAll() ;
	
	public void init() throws IOException;
}
