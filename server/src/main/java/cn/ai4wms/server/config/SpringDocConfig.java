package cn.ai4wms.common.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*** Swagger配置
 *
 * @author Mark sunlightcs@gmail.com
 */
@Configuration
public class SpringDocConfig {
    @Bean
    public OpenAPI springSysOpenApi() {
        return new OpenAPI()
                .info(new Info().title("用户接口文档")
                        .description("用户接口文档")
                        .version("v1.0"))
                .externalDocs(new ExternalDocumentation()
                        .description("SpringShop Wiki Documentation")
                        .url("https://springshop.wiki.github.org/docs"));
    }
}