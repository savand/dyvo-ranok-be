package org.savand.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.savand.configuration.properties.GCConfig;
import org.savand.exception.InvalidCaptchaException;
import org.savand.model.dto.RecaptchaValidateDtoRequest;
import org.savand.model.dto.RecaptchaValidateDtoResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecaptchaServiceImpl implements RecaptchaService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final GCConfig gcConfig;

    @Override
    public void validate(String token) throws IOException, InterruptedException {

        RecaptchaValidateDtoRequest formValidationDtoRequest = RecaptchaValidateDtoRequest.builder().event(
                RecaptchaValidateDtoRequest.RecaptchaEventDto.builder()
                        .expectedAction("send_form")
                        .token(token)
                        .siteKey(gcConfig.getRecaptchaSiteKey())
                        .build()

        ).build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://recaptchaenterprise.googleapis.com/v1/projects/blissful-star-414119/assessments?key=" + gcConfig.getApiKey()))
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(formValidationDtoRequest)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        RecaptchaValidateDtoResponse recaptchaValidateDtoResponse = null;

        if (response.statusCode() == 200) {

            recaptchaValidateDtoResponse = objectMapper.readValue(response.body(), RecaptchaValidateDtoResponse.class);

            if(recaptchaValidateDtoResponse.riskAnalysis().score() > 0.6) {
                log.debug("Response from recaptcha: {}", recaptchaValidateDtoResponse);

                return;
            }
        }

        throw new InvalidCaptchaException(String.format("Failed recaptcha validation with the status: %d and message :%s",
                response.statusCode(),
                recaptchaValidateDtoResponse));


    }

}
