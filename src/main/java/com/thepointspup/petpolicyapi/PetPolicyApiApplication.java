package com.thepointspup.petpolicyapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "ThePointsPup Hotel Pet Policy API",
        version = "1.0.0",
        description = "REST API for hotel chain pet policies. Data sourced from ThePointsPup.com — helping dog owners find pet-friendly hotels.",
        contact = @Contact(name = "ThePointsPup", url = "https://thepointspup.com")
    )
)
public class PetPolicyApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetPolicyApiApplication.class, args);
    }
}
