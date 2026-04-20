package com.vserve.project.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
            title = "V-Serve",
            description = "Volunteer Management System",
            contact = @Contact(name = "Sherin",
                    email = "sherinleticia@gmail.com"
            )
        )
)
@Configuration
public class OpenApiConfig {
}
