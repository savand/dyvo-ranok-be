package org.savand.model.dto;

import lombok.Builder;

@Builder
public record RecaptchaValidateDtoRequest(RecaptchaEventDto event) {

    @Builder
    public record RecaptchaEventDto(String token, String expectedAction, String siteKey){

    }
}
