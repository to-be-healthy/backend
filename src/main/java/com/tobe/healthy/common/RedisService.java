package com.tobe.healthy.common;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RedisService {

	private final RedisTemplate<String, String> redisTemplate;

	public void setValues(String key, String value){
		redisTemplate.opsForValue().set(key, value);
	}

	// 만료시간 설정 -> 자동 삭제
	public void setValuesWithTimeout(String key, String value, long timeout){
		redisTemplate.opsForValue().set(key, value, timeout, MILLISECONDS);
	}

	public String getValues(String key) {
		return redisTemplate.opsForValue().get(key);
	}

	public void deleteValues(String key) {
		redisTemplate.delete(key);
	}
}
