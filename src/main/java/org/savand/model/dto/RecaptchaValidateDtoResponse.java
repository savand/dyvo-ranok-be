package org.savand.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RecaptchaValidateDtoResponse(String name, RiskAnalysis riskAnalysis, TokenProperties tokenProperties) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RiskAnalysis(double score) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TokenProperties(boolean valid, String invalidReason, String createTime) {
    }

}
