package com.nhnacademy.auth.common.advice;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.nhnacademy.auth.exception.ValidationFailedException;
import com.nhnacademy.auth.exception.dto.ResponseGlobalExceptionDTO;

@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * 이미 존재하는 경우의 에러 핸들러
	 */
	@ExceptionHandler({ValidationFailedException.class})
	public ResponseEntity<Void> handleAlreadyExistsException(Exception ex) {
		ResponseGlobalExceptionDTO body = new ResponseGlobalExceptionDTO(ex.getMessage(),
			HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	}

}
