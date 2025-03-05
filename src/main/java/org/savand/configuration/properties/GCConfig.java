package org.savand.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.gc")
@Getter
@Setter
public class GCConfig {

    private String apiKey;
    private String recaptchaSiteKey;

}
