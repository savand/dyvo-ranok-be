package org.savand.service;

import lombok.extern.slf4j.Slf4j;
import org.savand.model.dto.FormDto;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FormSenderServiceImpl implements FormSenderService {
    @Override
    public void send(FormDto formDto) {
        log.debug("form is sent: {}", formDto);
    }
}
