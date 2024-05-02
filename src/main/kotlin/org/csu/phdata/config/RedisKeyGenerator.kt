package org.csu.phdata.config

import org.springframework.cache.interceptor.KeyGenerator
import org.springframework.stereotype.Component
import java.lang.reflect.Method

@Component("customKeyGenerator")
class RedisKeyGenerator: KeyGenerator {
    override fun generate(target: Any, method: Method, vararg params: Any?): Any {
        return buildString {
            append(target.javaClass.simpleName)
            append(".")
            append(method.name)
            params.forEach { append("#").append(it.toString()) }
        }
    }
}