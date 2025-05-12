package com.nhnacademy.auth.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Configuration
public class RedisConfig {

	/***
	 * 직렬화, 역직렬화할 때 ENUM 타입들은 toString 메소드 사용
	 * @return
	 */
	@Bean
	public ObjectMapper redisObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
		objectMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);

		return objectMapper;
	}

	/***
	 *
	 * @param objectMapper : ObjectMapper는 Java 객체를 JSON으로 직렬화하거나, JSON을 JAVA 객체로 역직렬화할 때 사용하는 Jackson 라이브러리 핵심 도구이다
	 *                     Redis나 HTTP API에서 JSON 변환할 때 ObjectMapper가 필수로 사용된다
	 *                     Redis에서 데이터를 저장할 때 객체를 JSON으로 변환하고, 꺼낼 때 JSON을 객체로 변환하려면 ObjectMapper가 필요하다
	 *
	 */
	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory,
		ObjectMapper objectMapper) {
		RedisTemplate<String, Object> sessionRedisTemplate = new RedisTemplate<>();
		sessionRedisTemplate.setConnectionFactory(redisConnectionFactory);
		sessionRedisTemplate.setKeySerializer(new StringRedisSerializer());
		sessionRedisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));

		sessionRedisTemplate.setHashKeySerializer(new StringRedisSerializer());
		sessionRedisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));

		return sessionRedisTemplate;
	}

}
