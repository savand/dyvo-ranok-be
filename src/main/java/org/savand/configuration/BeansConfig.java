package org.savand.configuration;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;

@Configuration
@RequiredArgsConstructor
public class BeansConfig {

    @Value("#{servletContext.contextPath}")
    private String servletContext;


    @Bean
    public HttpClient httpClient() {
        return HttpClient.newHttpClient();
    }


}
