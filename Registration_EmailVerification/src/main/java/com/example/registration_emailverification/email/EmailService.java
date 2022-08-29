package com.example.registration_emailverification.email;

import com.example.registration_emailverification.repository.EmailSender;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@Service
@AllArgsConstructor
public class EmailService implements EmailSender {
    private final static Logger LOGGER = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;

    @Override
    @Async
    public void send(String to, String email) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());
            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject("Confirm your email");
            helper.setFrom("doanducminh11082002@gmail.com");
            mailSender.send(mimeMessage);
        } catch (MessagingException exception) {
            LOGGER.error("failed to send email ", exception);
        }
    }
    /*
    Async : Nói rằng điểu này sẽ ơ trạng thái k đồng bộ, không muốn điều
    này chặn khách hàng nên sử dụng chúng như một hàng đợi để có thể gửi
    lại email
     */
}
