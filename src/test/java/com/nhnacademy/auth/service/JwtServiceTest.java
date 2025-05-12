package com.nhnacademy.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.nhnacademy.auth.jwt.provider.JwtTokenProvider;
import com.nhnacademy.auth.model.dto.request.RequestJwtTokenDTO;
import com.nhnacademy.auth.model.dto.response.ResponseJwtTokenDTO;

import jakarta.servlet.http.HttpServletResponse;

class JwtServiceTest {

	@Mock
	private JwtTokenProvider jwtTokenProvider;
	@Mock
	private RedisTemplate<String, String> redisTemplate;
	@Mock
	private SecretKey accessSecretKey;
	@Mock
	private SecretKey refreshSecretKey;
	@Mock
	private HttpServletResponse response;
	@Mock
	private ValueOperations<String, String> valueOperations;

	@InjectMocks
	private JwtService jwtService;

	private final long accessExpiration = 600000;
	private final long refreshExpiration = 3600000;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
	}

	@Test
	@DisplayName("Jwt Service success saveToken method Test")
	void successSaveTokenTest() throws Exception {

		// Given
		RequestJwtTokenDTO requestJwtTokenDTO = new RequestJwtTokenDTO("nhn1", "ROLE_USER");
		String accessToken = "access";
		String refreshToken = "refresh";

		// When
		when(jwtTokenProvider.provideAccessToken(any(), anyLong(), any())).thenReturn(accessToken);
		when(jwtTokenProvider.provideRefreshToken(any(), anyLong(), any())).thenReturn(refreshToken);

		ResponseJwtTokenDTO responseJwtTokenDTO = jwtService.saveToken(requestJwtTokenDTO, response);

		// Then
		assertThat(responseJwtTokenDTO.getAccessToken()).isEqualTo(accessToken);
		assertThat(responseJwtTokenDTO.getRefreshToken()).isEqualTo(refreshToken);

	}

}