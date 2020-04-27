package com.bolsadeideas.springboot.datajpa.app.models.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadFileServiceImpl implements IUploadFileService{
	
	//Con esta objeto se pueden mostrar mensajes por consola en tiempo de ejecucion
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private final static String UPLOADS_FOLDER = "uploads";

	@Override
	public Resource load(String filename) throws MalformedURLException{
		
		Path rootPath = getPath(filename);
		
		Resource recurso = null;
		
		recurso = new UrlResource(rootPath.toUri());
		
		
		return recurso;
	}

	@Override
	public String copy(MultipartFile file) throws IOException{
		
		String uniqueFilename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
		
		Path rootPath = getPath(uniqueFilename);
		log.info("rootPath: " + rootPath);
		
		
		Files.copy(file.getInputStream(), rootPath);
		
		
		return uniqueFilename;
	}

	@Override
	public boolean delete(String filename) {
		
		Path rootPath = getPath(filename);
		
		File archivo = rootPath.toFile();
		if(archivo.exists() && archivo.canRead()) {
			return archivo.delete();
		}
		
		// TODO Auto-generated method stub
		return false;
	}
	
	
	public Path getPath(String filename) {
		return Paths.get(UPLOADS_FOLDER).resolve(filename).toAbsolutePath();
	}

	@Override
	public void deleteAll() {
		//Elimina todos los archivos del directorio que se pasa como paramtero
		FileSystemUtils.deleteRecursively(Paths.get(UPLOADS_FOLDER).toFile());	
	}

	@Override
	public void init() throws IOException {
		
		Files.createDirectory(Paths.get(UPLOADS_FOLDER));
	}
	
	
}
