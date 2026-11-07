package app.services;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import app.config.EnvConfig;
import jakarta.activation.DataHandler;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;

public class BrevoEmailService {

    private static final String LOGO_CONTENT_ID = "jcash-logo";

    public boolean sendOtp(String recipient, String otp) {
        String host = setting("BREVO_SMTP_HOST", "smtp-relay.brevo.com");
        String port = setting("BREVO_SMTP_PORT", "587");
        String login = EnvConfig.get("BREVO_SMTP_LOGIN");
        String password = EnvConfig.get("BREVO_SMTP_PASSWORD");
        String senderEmail = EnvConfig.get("BREVO_SENDER_EMAIL");
        String senderName = setting("BREVO_SENDER_NAME", "JCash Bank");

        if (login.isBlank() || password.isBlank() || senderEmail.isBlank()) {
            throw new IllegalStateException(
                    "Set BREVO_SMTP_LOGIN, BREVO_SMTP_PASSWORD, and BREVO_SENDER_EMAIL in .env.");
        }

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.connectiontimeout", "15000");
        properties.put("mail.smtp.timeout", "15000");
        properties.put("mail.smtp.writetimeout", "15000");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(login, password);
            }
        });

        try {
            MimeMessage email = new MimeMessage(session);
            email.setFrom(new InternetAddress(senderEmail, senderName, StandardCharsets.UTF_8.name()));
            email.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient, false));
            email.setSubject("Your JCash verification code", StandardCharsets.UTF_8.name());

            String html = readTemplate()
                    .replace("{{OTP}}", otp)
                    .replace("{{LOGO}}", "<img src=\"cid:" + LOGO_CONTENT_ID
                            + "\" width=\"58\" height=\"58\" alt=\"JCash Bank\" style=\"display:block;width:58px;height:58px;border:0;border-radius:14px\">");

            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(html, "text/html; charset=UTF-8");

            MimeBodyPart logoPart = new MimeBodyPart();
            logoPart.setDataHandler(new DataHandler(new ByteArrayDataSource(readLogo(), "image/png")));
            logoPart.setFileName("circle_logo.png");
            logoPart.setHeader("Content-ID", "<" + LOGO_CONTENT_ID + ">");
            logoPart.setDisposition(MimeBodyPart.INLINE);

            MimeMultipart related = new MimeMultipart("related");
            related.addBodyPart(htmlPart);
            related.addBodyPart(logoPart);
            email.setContent(related);

            Transport.send(email);
            return true;
        } catch (MessagingException | IOException e) {
            throw new IllegalStateException("Could not send verification email through Brevo SMTP.", e);
        }
    }

    private String setting(String name, String fallback) {
        String value = EnvConfig.get(name);
        return value.isBlank() ? fallback : value;
    }

    private String readTemplate() throws IOException {
        try (var input = BrevoEmailService.class.getResourceAsStream("/app/email/password-reset.html")) {
            if (input == null) {
                throw new IllegalStateException("Password reset email template is missing.");
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private byte[] readLogo() throws IOException {
        String configuredPath = EnvConfig.get("BREVO_LOGO_PATH");
        if (!configuredPath.isBlank()) {
            Path path = Path.of(configuredPath);
            if (Files.isRegularFile(path)) {
                return Files.readAllBytes(path);
            }
            throw new IllegalStateException("BREVO_LOGO_PATH does not point to a readable image file.");
        }

        try (var input = BrevoEmailService.class.getResourceAsStream("/app/assets/circle_logo.png")) {
            if (input == null) {
                throw new IllegalStateException("JCash email logo is missing.");
            }
            return input.readAllBytes();
        }
    }
}
