package org.csu.phdata.config

import lombok.Getter
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.*
import org.springframework.data.redis.serializer.RedisSerializer


@Configuration
@EnableCaching
class RedisTemplateConfiguration {
    /**
     * 默认过期时长，单位：秒
     */

    @Getter
    private var expire: Long = (60 * 60 * 24).toLong()

    @Bean
    fun redisTemplate(factory: RedisConnectionFactory?): RedisTemplate<String, Any> {
        val redisTemplate = RedisTemplate<String, Any>()
        redisTemplate.keySerializer = RedisSerializer.string()
        redisTemplate.hashKeySerializer = RedisSerializer.string()
        redisTemplate.valueSerializer = RedisSerializer.java()
        redisTemplate.hashValueSerializer = RedisSerializer.java()
        redisTemplate.setConnectionFactory(factory!!)
        return redisTemplate
    }

    @Bean
    fun hashOperations(redisTemplate: RedisTemplate<String?, Any?>): HashOperations<String?, String, Any> {
        return redisTemplate.opsForHash()
    }

    @Bean
    fun valueOperations(redisTemplate: RedisTemplate<String?, String?>): ValueOperations<String?, String?> {
        return redisTemplate.opsForValue()
    }

    @Bean
    fun listOperations(redisTemplate: RedisTemplate<String?, Any?>): ListOperations<String?, Any?> {
        return redisTemplate.opsForList()
    }

    @Bean
    fun setOperations(redisTemplate: RedisTemplate<String?, Any?>): SetOperations<String?, Any?> {
        return redisTemplate.opsForSet()
    }

    @Bean
    fun zSetOperations(redisTemplate: RedisTemplate<String?, Any?>): ZSetOperations<String?, Any?> {
        return redisTemplate.opsForZSet()
    }

}