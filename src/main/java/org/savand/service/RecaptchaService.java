package org.savand.service;

import java.io.IOException;

public interface RecaptchaService {
    void validate(String token) throws IOException, InterruptedException;
}
