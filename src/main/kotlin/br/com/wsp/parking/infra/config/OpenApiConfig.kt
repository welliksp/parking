package br.com.wsp.parking.infra.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Parking Management API")
                    .description("API para gerenciamento de estacionamento com cálculo de receita e registro de eventos")
                    .version("1.0.0")
                    .contact(
                        Contact()
                            .name("Parking Team")
                            .email("contact@parking.com")
                    )
                    .license(
                        License()
                            .name("Apache 2.0")
                            .url("https://www.apache.org/licenses/LICENSE-2.0.html")
                    )
            )
    }
}

