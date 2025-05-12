package com.nhnacademy.auth.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseJwtTokenDTO {

	private String accessToken;
	private String refreshToken;

}
