package com.pr.moviecatalogservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
@EnableEurekaClient
@EnableCircuitBreaker
public class MovieCatalogServiceApplication {

    @Bean // producer (has something that others will need)
    @LoadBalanced // service discovery in a load balanced way.
    // URL is just a hint about what service you need to discover. Now RestTemplate is gonna look for hints about
    // which service to call when you gibe the URL. We have told RestTemplate to call Eureka instead going to a service directly.
    public RestTemplate getRestTemplate() {
//        return new RestTemplate();
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(3000); // 3 seconds as a timeout. If for example, 3 requests come in,
        // then one request is timed out

        return new RestTemplate(clientHttpRequestFactory); // it's all good as long as the response comes back within 3
        // seconds. It throws an error if the response takes more time
    }

//    @Bean
//    public WebClient.Builder getWebClientBuilder() {
//        return WebClient.builder();
//    }

    public static void main(String[] args) {
        SpringApplication.run(MovieCatalogServiceApplication.class, args);
    }

}
