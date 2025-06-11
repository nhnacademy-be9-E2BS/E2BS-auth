package com.nhnacademy.auth.common.config;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

class RedisConfigTest {

	private final RedisConfig redisConfig = new RedisConfig();

	@Test
	@DisplayName("Redis Object Mapper 메서드 테스트")
	void redisObjectMapperTest() {

		// Given

		// When
		ObjectMapper objectMapper = redisConfig.redisObjectMapper();

		// Then
		assertThat(objectMapper.isEnabled(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)).isTrue();
		assertThat(objectMapper.isEnabled(DeserializationFeature.READ_ENUMS_USING_TO_STRING)).isTrue();

	}

	@Test
	@DisplayName("Redis Template 메서드 테스트")
	void redisTemplateTest() {

		// Given
		RedisConnectionFactory mockConnectionFactory = mock(RedisConnectionFactory.class);
		ObjectMapper objectMapper = redisConfig.redisObjectMapper();

		// When
		RedisTemplate<String, Object> redisTemplate = redisConfig.redisTemplate(mockConnectionFactory, objectMapper);

		// Then
		assertThat(redisTemplate.getConnectionFactory()).isEqualTo(mockConnectionFactory);
		assertThat(redisTemplate.getKeySerializer()).isInstanceOf(StringRedisSerializer.class);
		assertThat(redisTemplate.getValueSerializer()).isInstanceOf(GenericJackson2JsonRedisSerializer.class);
		assertThat(redisTemplate.getHashKeySerializer()).isInstanceOf(StringRedisSerializer.class);
		assertThat(redisTemplate.getHashValueSerializer()).isInstanceOf(GenericJackson2JsonRedisSerializer.class);

	}
}