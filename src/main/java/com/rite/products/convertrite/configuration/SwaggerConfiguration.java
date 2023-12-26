package com.rite.products.convertrite.configuration;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.paths.RelativePathProvider;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {
	
	private  ServletContext servletContext;

	@Value("${application-hostname}")
	private String hostName;
	@Value("${context-path}")
	private String path;

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				//.host(hostName)
				.globalOperationParameters(
						Arrays.asList(new ParameterBuilder()
								.name("X-TENANT-ID")
								.description("Description of header")
								.modelRef(new ModelRef("string"))
								.parameterType("header")
								.required(true)
								.build()))
//				.pathProvider(new RelativePathProvider(servletContext) {
//			        @Override
//			        public String getApplicationBasePath() {
//			            return path;
//			        }
//			    });
				.select()
				.apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.any())
				.build();
	}
}
