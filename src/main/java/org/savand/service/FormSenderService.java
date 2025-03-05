package org.savand.service;

import jakarta.validation.Valid;
import org.savand.model.dto.FormDto;

public interface FormSenderService {
    void send(@Valid FormDto formDto);
}
