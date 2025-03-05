package org.savand.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RecaptchaValidateDtoResponse(String name, RiskAnalysis riskAnalysis) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RiskAnalysis(double score) {

    }

}
