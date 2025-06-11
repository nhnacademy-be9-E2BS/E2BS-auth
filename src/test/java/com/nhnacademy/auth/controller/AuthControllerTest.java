package com.nhnacademy.auth.controller;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.apache.catalina.connector.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.auth.model.dto.request.RequestJwtTokenDTO;
import com.nhnacademy.auth.model.dto.response.ResponseJwtTokenDTO;
import com.nhnacademy.auth.service.JwtService;

@ActiveProfiles("dev")
@WithMockUser(username = "user", roles = {"ADMIN", "MEMBER", "USER"})
@WebMvcTest(controllers = AuthController.class)
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private JwtService jwtService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("JWT 생성 및 발급 테스트")
	void createJwtTokenTest() throws Exception {

		// Given
		RequestJwtTokenDTO requestJwtTokenDTO = new RequestJwtTokenDTO("user");
		ResponseJwtTokenDTO responseJwtTokenDTO = new ResponseJwtTokenDTO("success");

		// When
		when(jwtService.saveToken(eq(requestJwtTokenDTO), any(Response.class))).thenReturn(responseJwtTokenDTO);

		// Then
		mockMvc.perform(post("/api/token")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestJwtTokenDTO)))
			.andExpect(status().isCreated());

	}

	@Test
	@DisplayName("JWT 생성 및 발급 ValidationFailedException 테스트")
	void createJwtTokenValidationFailedExceptionTest() throws Exception {

		// Given
		RequestJwtTokenDTO requestJwtTokenDTO = new RequestJwtTokenDTO(null);
		ResponseJwtTokenDTO responseJwtTokenDTO = new ResponseJwtTokenDTO("success");

		// When
		when(jwtService.saveToken(eq(requestJwtTokenDTO), any(Response.class))).thenReturn(responseJwtTokenDTO);

		// Then
		mockMvc.perform(post("/api/token")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestJwtTokenDTO)))
			.andExpect(status().is4xxClientError());

	}

}