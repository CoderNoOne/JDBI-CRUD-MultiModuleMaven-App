package service;

import exceptions.AppException;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailService {

  private static final String emailAddress = "the.mountain.057@gmail.com";
  private static final String emailPassword = "tatry321";

  private EmailService() {
  }

  public static void sendAsHtml(String recipient, String subject, String htmlContent) {

    try {
      System.out.println("Sending email ...");

      Session session = createSession();

      MimeMessage mimeMessage = new MimeMessage(session);
      prepareEmailMessage(mimeMessage, recipient, subject, htmlContent);

      Transport.send(mimeMessage);
      System.out.println("Email has been sent!");
    } catch (Exception e) {
      e.printStackTrace();
      throw new AppException("SEND AS HTML MESSAGE EXCEPTION");
    }

  }

  private static void prepareEmailMessage(MimeMessage mimeMessage, String recipient, String subject, String htmlContent) {
    try {
      mimeMessage.setContent(htmlContent, "text/html;charset=utf-8");
      mimeMessage.setFrom(new InternetAddress(emailAddress));
      mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
      mimeMessage.setSubject(subject);
    } catch (Exception e) {
      throw new AppException("PREPARE EMAIL MESSAGE EXCEPTION");
    }
  }

  private static Session createSession() {

    Properties properties = new Properties();
    properties.put("mail.smtp.starttls.enable", "true");
    properties.put("mail.smtp.host", "smtp.gmail.com");
    properties.put("mail.smtp.port", "587");
    properties.put("mail.smtp.auth", "true");

    return Session.getInstance(properties, new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(emailAddress, emailPassword);
      }
    });
  }

}
