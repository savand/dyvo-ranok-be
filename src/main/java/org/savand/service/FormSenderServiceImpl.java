package org.savand.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.savand.model.dto.FormDto;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class FormSenderServiceImpl implements FormSenderService {

    private static final String MAIL_ADDRESS = "dyvoranok@gmail.com";

    private final JavaMailSender emailSender;

    @Override
    public void send(FormDto formDto) {

        log.debug("form is sent: {}", formDto);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(MAIL_ADDRESS);
        message.setTo(MAIL_ADDRESS);
        message.setSubject("Замовлення із форми сайту");

        message.setText(String.format("Замовлення від %s. Зв'язатись за номером телефону: %s",
                formDto.userName(),
                formDto.phoneNumber()));

        emailSender.send(message);

        log.debug("form is successfully sent: {}", formDto);
    }
}
