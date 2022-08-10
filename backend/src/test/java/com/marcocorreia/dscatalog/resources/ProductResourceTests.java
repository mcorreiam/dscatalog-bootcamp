package com.marcocorreia.dscatalog.resources;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcocorreia.dscatalog.dto.ProductDTO;
import com.marcocorreia.dscatalog.services.ProductService;
import com.marcocorreia.dscatalog.services.exceptions.ResourceNotFoundException;
import com.marcocorreia.dscatalog.tests.Factory;

@WebMvcTest(ProductResourceTests.class)
public class ProductResourceTests {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private ProductService service;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private long existingId;
	private long nonExistingId;
	private ProductDTO produtcDTO;
	private PageImpl<ProductDTO> page;
	
	@BeforeEach 
	void setUp() throws Exception {
		
		existingId = 1L;
		nonExistingId = 2L;
		
		produtcDTO = Factory.createProductDTO();
		page = new PageImpl<>(List.of(produtcDTO));
		
		when(service.findAllPaged(ArgumentMatchers.any())).thenReturn(page);
		
		when(service.findById(existingId)).thenReturn(produtcDTO);
		when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);
		
		when(service.update(eq(existingId), any())).thenReturn(produtcDTO);
		when(service.update(eq(nonExistingId), any())).thenThrow(ResourceNotFoundException.class);
		
	}
	
	@Test
	public void updateShouldReturnProductDTOWhenIdExists() throws Exception{
		
		String jsonBody = objectMapper.writeValueAsString(produtcDTO);
		
		ResultActions result = 
				mockMvc.perform(put("/products/{id}", existingId)
					.content(jsonBody)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
	}
	
	@Test
	public void updateShouldReturnNotFoundWhenIdDoesNotExists() throws Exception{
		String jsonBody = objectMapper.writeValueAsString(produtcDTO);
		
		ResultActions result = 
				mockMvc.perform(put("/products/{id}", nonExistingId)
					.content(jsonBody)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
	}
	
	@Test
	public void findByIdShouldReturnProdudtWhenIdExists() throws Exception {
		ResultActions result = 
				mockMvc.perform(get("/products/{id}", existingId)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception  {
		ResultActions result = 
				mockMvc.perform(get("/products/{id}", nonExistingId)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
	}
	
	@Test
	public void findAllShouldReturnPage() throws Exception {
		ResultActions result = 
				mockMvc.perform(get("/products")
					.accept(MediaType.APPLICATION_JSON));
		result.andExpect(status().isOk());
	}
	
	

	/*
	 * @BeforeAll static void setUpBeforeClass() throws Exception { }
	 * 
	 * @AfterAll static void tearDownAfterClass() throws Exception { }
	 * 
	 * @BeforeEach void setUp() throws Exception { }
	 * 
	 * @AfterEach void tearDown() throws Exception { }
	 * 
	 * @Test void test() { fail("Not yet implemented"); }
	 */

}

