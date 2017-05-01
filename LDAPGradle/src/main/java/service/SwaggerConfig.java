package service;

import org.apache.commons.collections.functors.OrPredicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@EnableAutoConfiguration
public class SwaggerConfig {
   @Autowired
   private TypeResolver typeResolver;

   @Bean
   public Docket secureApi() {
       return new Docket(DocumentationType.SWAGGER_2)
    		   		//.groupName("Ldap")
                   .apiInfo(secureapiInfo())
                   .select()
                   .apis(RequestHandlerSelectors.any())
                   .paths(Predicates.or(PathSelectors.regex("/ldap/.*"),PathSelectors.regex("/security/.*")))
                   .build();
   }

   private ApiInfo secureapiInfo() {
       return new ApiInfoBuilder()
               .title("EAI Services API")
               .description("Secure API documentation")
               .termsOfServiceUrl("sga.jain@mbusa.com")
               .contact("EAI TEAM")
               .license("MBUSA")
               .licenseUrl("https://mbusa.com")
               .version("1.0")
               .build();
   }
  
   
   /*@Bean
   public Docket ldapApi() {
       return new Docket(DocumentationType.SWAGGER_2)
    		   		.groupName("Secure")
                   .apiInfo(ldapapiInfo())
                   .select()
                   .apis(RequestHandlerSelectors.any())
                   .paths(PathSelectors.regex("/ldap/.*"))
                   .build();
   }

   private ApiInfo ldapapiInfo() {
       return new ApiInfoBuilder()
               .title("EAI Services API")
               .description("Ldap API documentation")
               .termsOfServiceUrl("sga.jain@mbusa.com")
               .contact("EAI TEAM")
               .license("MBUSA")
               .licenseUrl("https://mbusa.com")
               .version("1.0")
               .build();
   }*/
}