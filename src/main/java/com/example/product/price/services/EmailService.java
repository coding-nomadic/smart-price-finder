package com.example.product.price.services;

import com.example.product.price.models.ContactForm;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendContactEmail(ContactForm form) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo("tnzngdw@gmail.com"); // Replace with your email
        helper.setSubject("New Contact Form Submission");

        // Thymeleaf context
        Context context = new Context();
        context.setVariable("name", form.getName());
        context.setVariable("email", form.getEmail());
        context.setVariable("message", form.getMessage());

        String htmlContent = templateEngine.process("contact", context);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}