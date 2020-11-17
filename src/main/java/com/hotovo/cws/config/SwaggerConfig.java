package com.hotovo.cws.config;

import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class SwaggerConfig {


	@Bean
	public Docket walletApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.hotovo.cws.controller"))
				.paths(PathSelectors.any())
				.build()
				.apiInfo(metaData());
	}

	private ApiInfo metaData() {
		return new ApiInfo(
				"Crypto Wallet Simulator API",
				"Crypto Wallet Simulator API descripton",
				"1.0",
				null,
				new Contact("Ivan Ulbricht", "http://ivan-ulbricht.sk", "iviak.ulbricht@gmail.com"),
				null,
				null,
				Collections.emptyList());
	}
}
