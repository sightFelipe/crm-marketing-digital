package com.crmmarketingdigitalback2024.commons.listener;
import com.crmmarketingdigitalback2024.model.UserEntity.UserEntity;
import com.crmmarketingdigitalback2024.service.user.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegistrationCompleteEventListener {
    private final JavaMailSender mailSender;
    @Setter
    private UserEntity theUser;

    public void sendPasswordResetVerificationEmail(String url) throws MessagingException, UnsupportedEncodingException {
        String subject = "Verificación para restablecer tu contraseña";
        String senderName = "Servicio de Registro de Usuarios";
        String mailContent = "<div style=\"background-color: #fce4ec; padding: 20px; border-radius: 5px;\">"
                + "<h2 style=\"color: #8e24aa;\">¡Bienvenido a CRM Valhalla!</h2>"
                + "<p style=\"color: #424242;\"><strong>Has solicitado restablecer tu contraseña.</strong></p>"
                + "<p style=\"color: #424242;\">Tienes 24 horas para utilizar este enlace y restablecer tu contraseña:</p>"
                + "<div style=\"background-color: #fff; border: 1px solid #7b1fa2; padding: 20px; margin-bottom: 20px;\">"
                + "<a href=\"" + url + "\" style=\"background-color: #7b1fa2; color: #fff; padding: 10px 20px; text-decoration: none; border-radius: 5px;\">Restablecer contraseña</a>"
                + "</div>"
                + "<p style=\"color: #424242;\">Gracias por utilizar nuestro Servicio de Registro de Usuarios.</p>"
                + "</div>";
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("juanfe092@gmail.com", senderName);
        messageHelper.setTo(theUser.getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        mailSender.send(message);
    }
}