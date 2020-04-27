package com.bolsadeideas.springboot.datajpa.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.bolsadeideas.springboot.datajpa.app.auth.filter.JWTAuthenticationFilter;
import com.bolsadeideas.springboot.datajpa.app.auth.filter.JWTAuthorizationFilter;
import com.bolsadeideas.springboot.datajpa.app.auth.service.IJWTService;
import com.bolsadeideas.springboot.datajpa.app.models.service.JpaUserDetailsService;

//Con esta anotacion se activan y delega la seguridad con las anotaciones '@Secured'
//...con la propiedad prePostEnabled activa la anotacion '@PreAuthorized' que hace lo mismo que Secured
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter{
	
	//@Autowired
	//private LoginSuccessHandler loginSuccessHandler;
	
	@Autowired
	private BCryptPasswordEncoder bCryptEncoder;
	
	/*//Usado en autenticacion con jdbc
	@Autowired
	private DataSource dataSource;
	*/
	
	@Autowired
	private JpaUserDetailsService userDetailsService;

	@Autowired
	private IJWTService jwtService;
	
	//Este metodo permite añadir usuarios al contexto de seguridad
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder builder) throws Exception {
		
		
		//Autenticacion con JPA
		builder.userDetailsService(userDetailsService)
		.passwordEncoder(bCryptEncoder);
		
		
		
		//Autenticacion con JDBC
		/*
		builder.jdbcAuthentication()
		.dataSource(dataSource)
		.passwordEncoder(bCryptEncoder)
		.usersByUsernameQuery("select username, password, enabled from users where username = ?")
		.authoritiesByUsernameQuery("select u.username, a.authority from authorities a inner join users u on (a.user_id=u.id) where u.username = ?");
		*/
		
		
		//Autenticacion en memoria
		/*
		BCryptPasswordEncoder encoder = this.bCryptEncoder;
		
		//permite crear los usuarios
		
		//Expresion Lambda cada vez que recibe un password lo encripta y lo retorna
		UserBuilder users = User.builder().passwordEncoder(password ->encoder.encode(password));
		//UserBuilder users = User.builder().passwordEncoder(encoder::encode);
		
		builder.inMemoryAuthentication()
		.withUser(users.username("admin").password("123456").roles("ADMIN", "USER"))
		.withUser(users.username("bryam").password("123456").roles("USER"));
		*/
	
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		
		
		http.authorizeRequests().antMatchers("/", "/css/**", "/js/**", "/images/**", "/listar", "/locale", "/listar-rest").permitAll()
		/*.antMatchers("/ver/**").hasAnyRole("USER")*/
		/*.antMatchers("/upload/**").hasAnyRole("USER")*/
		/*.antMatchers("/form/**").hasAnyRole("ADMIN")*/
		/*.antMatchers("/eliminar/**").hasAnyRole("ADMIN")*/
		/*.antMatchers("/factura/**").hasAnyRole("ADMIN")*/
		.anyRequest().authenticated()
		/*.and()
		.formLogin()
			.successHandler(loginSuccessHandler)
			.loginPage("/login")
			.permitAll()
		.and()
		.logout()
			.permitAll()
		.and()
		.exceptionHandling()
			.accessDeniedPage("/error_403")
		*/	
		.and()
		.addFilter(new JWTAuthenticationFilter(authenticationManager(), jwtService))//se tegistra el filtro y se le pasa el Authe.Manager de S.Security 
		.addFilter(new JWTAuthorizationFilter(authenticationManager(), jwtService))//Filtro para la auhorization
		.csrf().disable()//Se desactiva la proteccion CSRF en los formularios para trabajar con JWT
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); //Se deshabilita el uso de sesiones en el back
		
		
		
	}

	
	
	
}
