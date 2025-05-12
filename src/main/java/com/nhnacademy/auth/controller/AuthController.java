package com.nhnacademy.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.auth.exception.ValidationFailedException;
import com.nhnacademy.auth.model.dto.request.RequestJwtTokenDTO;
import com.nhnacademy.auth.model.dto.response.ResponseJwtTokenDTO;
import com.nhnacademy.auth.service.JwtService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private JwtService jwtService;

	/**
	 * front에서 JWT Token create 요청이 오면 요청을 처리하는 RestController
	 */
	@PostMapping
	public ResponseJwtTokenDTO createJwtToken(@Validated @RequestBody RequestJwtTokenDTO requestJwtTokenDTO,
		BindingResult bindingResult,
		HttpServletResponse response) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}

		return jwtService.saveToken(requestJwtTokenDTO, response);
	}

}
