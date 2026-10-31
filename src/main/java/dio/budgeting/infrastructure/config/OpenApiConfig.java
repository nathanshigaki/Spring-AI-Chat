package dio.budgeting.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Smart Budgeting API - Assistente Financeiro com Spring AI")
                        .version("1.0.0")
                        .description("API Inteligente para gestão de finanças pessoais (receitas e despesas) " +
                                "com processamento de comandos de voz (Whisper STT), raciocínio com IA (Llama 3.3 via Groq) " +
                                "e execução de regras de negócio através de Tool Calling no padrão Clean Architecture & DDD.")
                        .contact(new Contact()
                                .name("DIO Student Project")
                                .url("https://github.com/digitalinnovationone"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
