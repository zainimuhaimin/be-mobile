package id.co.hilmi.bemobile.config.resttemplate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestTemplateConfig {

    @Value(value = "${rest.client.connect.timeout:3}")
    private int restClientConnectTimeout;
    @Value(value = "${rest.client.read.timeout:5}")
    private int restClientReadTimeout;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        var requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setOutputStreaming(false);
        ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(requestFactory);
        var restTemplate = restTemplateBuilder
                .setReadTimeout(Duration.ofSeconds(restClientReadTimeout))
                .setConnectTimeout(Duration.ofSeconds(restClientConnectTimeout))
                .build();
        restTemplate.setRequestFactory(factory);
        return restTemplate;
    }
}
