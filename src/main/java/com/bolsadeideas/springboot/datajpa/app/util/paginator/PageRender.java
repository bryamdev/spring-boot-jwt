package com.bolsadeideas.springboot.datajpa.app.util.paginator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;

public class PageRender<T> {

	private String url;
	private Page<T> page;
	
	private int totalPaginas;
	private int numElementosPorPagina;
	private int paginaActual;
	
	private List<PageItem> paginas;
	
	
	public PageRender(String url, Page<T> page) {
		this.url = url;
		this.page = page;
		this.paginas = new ArrayList<PageItem>();
		
		numElementosPorPagina = page.getSize();
		totalPaginas = page.getTotalPages();
		
		//empieza desde 0 pero para mostrar a la vista se le suma 1
		paginaActual = page.getNumber() + 1;
		
		
		
		int desde, hasta;
		//Mostramos todo el paginador
		if(totalPaginas <= numElementosPorPagina) {
			desde = 1;
			hasta = totalPaginas;	
		}else {
			//Se muestra por rangos 
			//Rango inicial
			if(paginaActual <= numElementosPorPagina/2) {
				desde = 1;
				hasta = numElementosPorPagina;
			//Rango final
			}else if(paginaActual >= totalPaginas - numElementosPorPagina/2){
				desde = totalPaginas - numElementosPorPagina + 1;
				hasta = numElementosPorPagina;
			//Rango intermedio
			}else {
				desde = paginaActual - numElementosPorPagina/2;
				hasta = numElementosPorPagina;
			}
		}
		
		for(int i = 0; i < hasta; i++) {
			paginas.add(new PageItem(desde+i, paginaActual == desde+i));
		}	
		
	}


	public String getUrl() {
		return url;
	}


	public int getTotalPaginas() {
		return totalPaginas;
	}


	public int getPaginaActual() {
		return paginaActual;
	}


	public List<PageItem> getPaginas() {
		return paginas;
	}
	
	public boolean isFirst() {
		return page.isFirst();
	}
	
	public boolean isLast() {
		return page.isLast();
	}
	
	public boolean hasNext() {
		return page.hasNext();
	}
	
	public boolean hasPrevious() {
		return page.hasPrevious();
	}
	
	
	
	
	
	

	
}
