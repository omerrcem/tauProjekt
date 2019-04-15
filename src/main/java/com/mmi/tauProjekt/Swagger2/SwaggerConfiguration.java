package com.mmi.tauProjekt.Swagger2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.*;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.paths.RelativePathProvider;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.ServletContext;

import java.time.LocalDate;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static springfox.documentation.builders.PathSelectors.regex;


@Configuration
@EnableSwagger2
@Profile({"!customer"})
public class SwaggerConfiguration extends WebMvcConfigurerAdapter {

    @Autowired
    private ServletContext servletContext;


    @Bean
    public Docket api() {

        return new Docket(DocumentationType.SWAGGER_2)
                .host("localhost:8080")
                .directModelSubstitute(LocalDate.class, Date.class)
                .pathProvider(new RelativePathProvider(servletContext) {
                    @Override
                    public String getApplicationBasePath() {
                        return "/customers";
                    }
                })
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }


}