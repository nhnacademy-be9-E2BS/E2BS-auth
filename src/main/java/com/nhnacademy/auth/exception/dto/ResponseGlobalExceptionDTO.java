package com.nhnacademy.auth.exception.dto;

import java.time.LocalDateTime;

public record ResponseGlobalExceptionDTO(String title, int status, LocalDateTime timeStamp) {
}
