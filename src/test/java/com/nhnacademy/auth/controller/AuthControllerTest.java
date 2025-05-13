package com.nhnacademy.auth.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.auth.model.dto.request.RequestJwtTokenDTO;
import com.nhnacademy.auth.model.dto.response.ResponseJwtTokenDTO;
import com.nhnacademy.auth.service.JwtService;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Spring Security 보안 필터를 꺼서 인증없이 테스트 가능
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@MockitoBean
	private JwtService jwtService;

	@Test
	@DisplayName("/api/auth path success status response Test")
	void successCreateJwtToken() throws Exception {

		// Given
		RequestJwtTokenDTO requestDto = new RequestJwtTokenDTO("nhn1", "ROLE_USER");
		ResponseJwtTokenDTO responseDto = new ResponseJwtTokenDTO("access", "refresh");

		// When
		// Mockito.when(jwtService.saveToken(any(RequestJwtTokenDTO.class), any(HttpServletResponse.class)))
		// 	.thenReturn(responseDto);

		// Then
		mockMvc.perform(post("/api/auth")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.accessToken").value("access"))
			.andExpect(jsonPath("$.refreshToken").value("refresh"));

	}

	@Test
	@DisplayName("Request DTO Validation Failed Exception Test")
	void authControllerValidationFailedTest() throws Exception {

		// Given
		String requestJson = """
			{
			    "memberId": ,
			    "memberRole": "MEMBER"
			}
			""";

		// When

		// Then
		mockMvc.perform(post("/api/auth")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson))
			.andExpect(status().isBadRequest());

	}
}
