package org.csu.phdata.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class OpenApiConfig {
    @Bean
    fun springOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("公共卫生事件可视化接口文档")
                    .description("公共卫生事件可视化接口文档")
                    .version("1.0.1")
            )
    }
}