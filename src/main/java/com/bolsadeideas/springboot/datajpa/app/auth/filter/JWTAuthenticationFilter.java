package com.bolsadeideas.springboot.datajpa.app.auth.filter;

import java.io.IOException;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bolsadeideas.springboot.datajpa.app.auth.service.IJWTService;
import com.bolsadeideas.springboot.datajpa.app.models.entity.Usuario;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

//INTERCEPTOR
//Clase filtro encargada de hacer el login con JWT 
//Esta clase se invoca cuando la ruta del request sea '/login' por POST
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	public static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);
	
	// Componente encargado de hacer la autenticacion usando la clase
	// UserDetailsService(Proovedor de autenticacion)
	private AuthenticationManager authenticationManager;
	private IJWTService jwtService;

	public JWTAuthenticationFilter(AuthenticationManager authenticationManager, IJWTService jwtService) {
		this.authenticationManager = authenticationManager;
		setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/api/login"));
		this.jwtService = jwtService;
	}

	// Metodo encargado de hacer la autenticacion o intentar hacerlo
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {

		String username = obtainUsername(request);
		String password = obtainPassword(request);

		if (username != null && password != null) {
			logger.info("Username desde request parameter (form-data) en filtro JWTAuth: " + username);
			logger.info("Password desde request parameter (form-data) en filtro JWTAuth: " + password);
		} else {

			Usuario user = null;

			try {
				// getInputStream obiene el body del request
				// readValue() lee el body JSON y lo mapea a la clase Entity Usuario
				user = new ObjectMapper().readValue(request.getInputStream(), Usuario.class);
				username = user.getUsername();
				password = user.getPassword();

				logger.info("Username desde request (InputStream) en filtro JWTAuth: " + username);
				logger.info("Password desde request (InputStream) en filtro JWTAuth: " + password);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		username = username.trim();

		// Objeto contenedor de las credenciales enviadas
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

		return authenticationManager.authenticate(authToken);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {

		// Formas para crear la llave secreta 'secreto' para el token
		// Key secretKeyAut = Keys.secretKeyFor(SignatureAlgorithm.HS512); // Genera una Key random
		// SecretKey secretKey = Keys.hmacShaKeyFor("Mi.Clave.Secreta".getBytes());
		// Lanza excepcion por el tamaño de la llave(128 bits)
		/*SecretKey secretKey = new SecretKeySpec("Mi.Clave.Secreta".getBytes(),
		SignatureAlgorithm.HS256.getJcaName());*/

		String token = jwtService.createToken(authResult);

		response.addHeader("Authorization", "Bearer " + token);

		Map<String, Object> body = new HashMap<String, Object>();
		body.put("token", token);
		body.put("User", (User) authResult.getPrincipal());
		body.put("mensaje", String.format("Hola %s has iniciado sesion con exito", authResult.getName()));

		ObjectMapper formateadorJson = new ObjectMapper();
		String json = formateadorJson.writeValueAsString(body);

		response.getWriter().write(json);
		response.setStatus(200);
		response.setContentType("application/json");

	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		
		Map<String, Object>	body = new HashMap<>();
		body.put("mensaje", "Credenciales de acceso incorrectas");
		body.put("error", failed.getMessage());

		response.getWriter().write(new ObjectMapper().writeValueAsString(body));
		response.setStatus(401);
		response.setContentType("application/json");

	}

	
	

}
