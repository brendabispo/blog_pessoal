package com.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.model.UsuarioLogin;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioControllerTeste {

	@Autowired
	private TestRestTemplate testRestTemplate;

	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private UsuarioRepository usuarioRepository; 
	
	@BeforeAll
	void start() {
		usuarioRepository.deleteAll();
		
		usuarioService.cadastrarUsuario(
				new Usuario(0L, "Root", "root@root.com", 
						"rootroot", "link.foto.com/1234"));
	}
	
	@Test
	@DisplayName("Cadastrar um Usuário")
	public void deveCriarUmUsuario() {
		
		HttpEntity<Usuario> corpoRequisicao= new HttpEntity<Usuario>(
				new Usuario(0L, "Joana Angélica", "joana@email.com", 
						"12345678", "linkfoto.com.br/12345")); 
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);
	
	assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());
	assertEquals(corpoRequisicao.getBody().getNome(), corpoResposta.getBody().getNome());
	assertEquals(corpoRequisicao.getBody().getUsuario(), corpoResposta.getBody().getUsuario());
	
	}
	
	@Test
	@DisplayName("Não deve permitir duplicação do Usuário")
	public void naoDeveDuplicarUsuario() {
		
		usuarioService.cadastrarUsuario(
				new Usuario(0L, "Maria", "maria@root.com", 
						"12345678", "link.foto.com/12345"));
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(
				new Usuario(0L, "Maria", "maria@root.com", 
						"12345678", "link.foto.com/12345"));
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);
		
		assertEquals(HttpStatus.BAD_REQUEST, corpoResposta.getStatusCode());
	}
	
	@Test
	@DisplayName("Listar todos os Usuários")
	public void deveMostrarTodosUsuarios() {
		
		usuarioService.cadastrarUsuario(
				new Usuario(0L, "Celina", "celina@root.com", 
						"12345678", "link.foto.com/12345"));
		
		usuarioService.cadastrarUsuario(
				new Usuario(0L, "Marlene", "marlene@root.com", 
						"12345678", "link.foto.com/123456"));
		
		ResponseEntity<String> resposta = testRestTemplate
				.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuarios/all", HttpMethod.GET, null, String.class);
		
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
	}
	
	@Test
	@DisplayName("Atualizar um Usuário")
	public void deveAtualizarUmUsuario() {
		
		Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(
				new Usuario(0L, "Everaldo", "everaldo@root.com", 
						"12345678", "link.foto.com/12345"));
		
		Usuario usuarioUpdate = new Usuario(usuarioCadastrado.get().getId(), 
				"Everaldo Silva", "everaldo@root.com", 
				"12345678", "link.foto.com/12345");
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(usuarioUpdate);
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuarios/atualizar", HttpMethod.PUT, corpoRequisicao, Usuario.class);
		
		assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
		assertEquals(corpoRequisicao.getBody().getNome(), corpoResposta.getBody().getNome());
		assertEquals(corpoRequisicao.getBody().getUsuario(), corpoResposta.getBody().getUsuario());
	}

	//Desafio
	
	@Test
	@DisplayName("Buscar Usuário por ID")
	public void deveBuscarUmUsuarioPorId() {
		
		Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(
				new Usuario(0L, "Neto", "neto@root.com", 
						"12345678", "link.foto.com/12345"));
		
		Usuario usuarioId = new Usuario(usuarioCadastrado.get().getId(), 
				"Neto", "neto@root.com", 
				"12345678", "link.foto.com/12345");
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(usuarioId);
		

		ResponseEntity<String> resposta = testRestTemplate
				.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuarios/1", HttpMethod.GET, corpoRequisicao, String.class);
		
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		
	}
	
	@Test
	@DisplayName("Logar")
	public void deveLogar() {
		
	}
		
		
}
	



