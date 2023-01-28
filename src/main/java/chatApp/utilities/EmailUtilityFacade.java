package chatApp.utilities;

import chatApp.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;

import static chatApp.utilities.Utility.*;

@Component
public class EmailUtilityFacade {
    private static JavaMailSender mailSender;
    private static SimpleMailMessage preConfiguredMessage;


    @Autowired
    private JavaMailSender autowiredMailSender;
    @Autowired
    private SimpleMailMessage autowiredPreConfiguredMessage;

    @PostConstruct
    private void init() {
        mailSender = autowiredMailSender;
        preConfiguredMessage = autowiredPreConfiguredMessage;
    }


    /**
     * Chains a message and sends to an email with a token, uses the JAVAMAIL library
     *
     * @param email - the user's email to send the verification token
     * @param verifyCode - the verification code
     */
    public static void sendMessage(String email, String verifyCode) {
        preConfiguredMessage.setFrom(innerSystemEmail);
        preConfiguredMessage.setTo(email);
        preConfiguredMessage.setSubject(emailContent);
        preConfiguredMessage.setText(verifyCode);
        mailSender.send(preConfiguredMessage);
    }
}
