package com.bolsadeideas.springboot.datajpa.app.models.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "clientes")
public class Cliente implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	//con strategy se indica forma en que el motor de la db generara el autoincrement
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private Long id;

	//@Column(name="nombre") //se puede especificar las propiedades de la columna
	@NotEmpty
	private String nombre;
	
	@NotEmpty
	private String apellido;
	
	@NotEmpty
	@Email
	private String email;

	//La hora de creacion
	@NotNull
	@Column(name = "create_at")
	@Temporal(TemporalType.DATE) // Indica el formato en que se guaradara la fecha en la tabla
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date createAt;
	
	private String foto;
	
	//One (clase actual), un cliente puede tener muchas facturas 'ToMany'
	//CascadeType permite persistir o eliminar elementos hijos
	//mappedBy crea llave foranea en la tabla facturas(se le pasa el nombre del atributo en la otra clase/tabla)
	//... hace la relacion bidireccional
	@OneToMany(mappedBy = "cliente", fetch=FetchType.LAZY, cascade = CascadeType.ALL)
	//@JsonIgnore //Se omite al momento de parsear a JSON
	@JsonManagedReference //Permite hacer un control con la relacion de clases(actual es la padre que se quiere moestrar) 
	private List<Factura> facturas;
	
	
	
	public Cliente() {
		
		this.facturas = new ArrayList<Factura>();
	}

	//Este metodo se ejcutara antes de hacer la persistencia de los datos
	@PrePersist
	public void prePersist() {
		//createAt = new Date();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	public List<Factura> getFacturas() {
		return facturas;
	}

	public void setFacturas(List<Factura> facturas) {
		this.facturas = facturas;
	}
	
	public void agregarFactura(Factura factura) {
		this.facturas.add(factura);
	}

	@Override
	public String toString() {
		return nombre + " " + apellido;
	}
	
	
	
	
	

}
