package com.acme.parking.api;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
      .components(new Components())
      .info(new Info()
        .version("v1")
        .title("Toll Parking API")
        .contact(new Contact()
          .name("Huseyin KILIC")
          .email("hklc86@gmail.com")
          .url("https://www.linkedin.com/in/huseyin-kilic-msc-psm/"))
        .description("This API allows managing parking spaces and parking information for a toll parking area. " +
          "<br/> A typical parking flow would be like:" +
          "<ol>" +
          "<li> Get an available parking space for a given car type: <code>GET /v1/parkingSpaces?type=GASOLINE</code>" +
          "<li> Start parking on this parking space: <code>POST /v1/parkings</code>" +
          "<li> End parking using the parking id & receive billing: <code>DELETE /v1/parkings/{parkingId}</code>" +
          "</ol>")
      );
  }
}