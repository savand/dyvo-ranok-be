package org.savand.service;

import org.junit.jupiter.api.Test;
import org.savand.model.dto.FormDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FormSenderServiceImplTest {

    @Autowired
    private FormSenderService formSenderService;

//    @Test
//    void send() {
//        formSenderService.send(FormDto.builder()
//                        .name("Test")
//                        .phoneNumber("111111111")
//                .build());
//    }
}