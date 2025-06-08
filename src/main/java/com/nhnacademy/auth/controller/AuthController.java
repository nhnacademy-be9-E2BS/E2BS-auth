package com.nhnacademy.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Tag(name = "JWT 생성 및 발급", description = "JWT 생성 및 발급 기능 제공")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/token")
public class AuthController {

	private final JwtService jwtService;

	/**
	 * front에서 JWT Token create 요청이 오면 요청을 처리하는 RestController
	 */
	@Operation(summary = "JWT 생성 및 발급", description = "JWT 생성 및 발급 요청 처리",
		responses = {
			@ApiResponse(responseCode = "201", description = "JWT 생성 및 발급 요청에 따른 성공 응답"),
			@ApiResponse(responseCode = "400", description = "입력값 검증 실패", content = @Content(schema = @Schema(implementation = ValidationFailedException.class)))
		})
	@PostMapping
	public ResponseEntity<ResponseJwtTokenDTO> createJwtToken(@Validated
		@Parameter(description = "JWT 생성 및 발급 DTO", required = true, schema = @Schema(implementation = RequestJwtTokenDTO.class))
		@RequestBody RequestJwtTokenDTO requestJwtTokenDTO,
		BindingResult bindingResult,
		HttpServletResponse response) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}

		ResponseJwtTokenDTO responseJwtTokenDTO = jwtService.saveToken(requestJwtTokenDTO, response);

		return ResponseEntity.status(HttpStatus.CREATED).body(responseJwtTokenDTO);
	}

}
