package com.nhnacademy.auth.common.config;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.nhnacademy.auth.service.JwtService;

@WebMvcTest
@Import(SecurityConfig.class)
class SecurityConfigTest {

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@MockitoBean
	private JwtService jwtService;

	@Test
	@DisplayName("Security Filter Chain 테스트")
	void securityFilterChainTest() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context)
			.apply(springSecurity())
			.build();

		mockMvc.perform(get("/"))
			.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("Password Encoder 메서드 테스트")
	void passwordEncoderTest() {
		String password = "1234";
		String encoded = passwordEncoder.encode(password);

		Assertions.assertThat(encoded).isNotEqualTo(password);
		Assertions.assertThat(passwordEncoder.matches(password, encoded)).isTrue();
	}
}