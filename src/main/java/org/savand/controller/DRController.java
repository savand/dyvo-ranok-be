package org.savand.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.savand.model.dto.FormDto;
import org.savand.service.FormSenderService;
import org.savand.service.RecaptchaService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("v1")
@RequiredArgsConstructor
@Slf4j
@Validated
@CrossOrigin("#{@corsConfig.allowedOrigin}")
public class DRController {

    private final RecaptchaService recaptchaService;
    private final FormSenderService formSenderService;

    @PostMapping("form")
    public ResponseEntity<String> downloadLocallyForBatchRequest(@RequestHeader String token,
                                                                 @Valid @RequestBody FormDto formDto) throws IOException, InterruptedException {
        recaptchaService.validate(token);

        formSenderService.send(formDto);

        return ResponseEntity.ok().build();
    }

}
