package org.savand.configuration;


import lombok.RequiredArgsConstructor;
import org.savand.configuration.properties.GCConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.net.http.HttpClient;
import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class BeansConfig {

    @Value("#{servletContext.contextPath}")
    private String servletContext;
    private final GCConfig gcConfig;


    @Bean
    public HttpClient httpClient() {
        return HttpClient.newHttpClient();
    }

    @Bean
    public JavaMailSender javaMailSender() {

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // SMTP server details
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("savka.and@gmail.com");
        mailSender.setPassword(gcConfig.getSmtpPassword());

        // SMTP properties
        Properties properties = mailSender.getJavaMailProperties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        return mailSender;
    }



}
