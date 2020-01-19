package others;

import exceptions.AppException;
import lombok.extern.slf4j.Slf4j;
import entity.Movie;
import tickets_data_filtering.MovieFilteringCriterion;
import entity.JoinedEntitiesUtils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static j2html.TagCreator.*;
import static others.UserDataUtils.printMessage;

@Slf4j
public final class EmailUtils {

  private static final String emailAddress = "mailForApplication1908@gmail.com";
  private static final String emailPassword = "notyourbusiness1908";

  private EmailUtils() {
  }

  public static void sendMoviePurchaseConfirmation(String recipient, String subject, Movie movie, LocalDateTime startDateTime) {

    var customerWithMoviesAndSalesStand = JoinedEntitiesUtils.convertMovieToCustomerWithMoviesAndSalesStands(movie, startDateTime);
    var htmlTable = createHtmlTable(Collections.singletonList(customerWithMoviesAndSalesStand));
    sendAsHtml(recipient, subject, htmlTable);
  }


  public static void sendSummaryTableByFilters(String recipient, String subject, List<CustomerWithMoviesAndSalesStand> allFilteredTickets, Map<MovieFilteringCriterion, List<?>> filters) {

    var htmlContent = h3("Filtered by:").render() +
            tbody().with(
                    th("Filter type"),
                    th("Filter values").with(
                            each(filters, i -> tr(
                                    td(i.getKey().name()),
                                    td(i.getValue().toString()))))).render() +
            h3("The results are:").render() +
            createHtmlTable(allFilteredTickets);

    sendAsHtml(recipient, subject, htmlContent);
  }

  public static void sendAllSummaryTable(String recipient, String subject, List<CustomerWithMoviesAndSalesStand> customerWithMoviesAndSalesStandsList) {
    String htmlContent = String.join(h1("YOUR ALL SUMMARY HISTORY").render(), createHtmlTable(customerWithMoviesAndSalesStandsList));
    sendAsHtml(recipient, subject, htmlContent);
  }

  private static String createHtmlTable(List<CustomerWithMoviesAndSalesStand> customerWithMoviesAndSalesStandsList) {

    return
            tbody().with(th("Movie title"),
                    th("Movie genre"),
                    th("Movie duration"),
                    th("Movie price"),
                    th("Movie release date"),
                    th("Movie start date time").with(
                            each(customerWithMoviesAndSalesStandsList, i -> tr(
                                    td(i.getMovieTitle()),
                                    td(i.getMovieGenre()),
                                    td(i.getMovieDuration().toString()),
                                    td(i.getTicketPrice().toString()),
                                    td(i.getMovieReleaseDate().toString()),
                                    td(i.getStartDateTime().toString()))))).renderFormatted();
  }

  private static void sendAsHtml(String recipient, String subject, String htmlContent) {

    try {
      printMessage("Sending email to " + recipient + " ...");

      Session session = createSession();

      MimeMessage mimeMessage = new MimeMessage(session);
      prepareEmailMessage(mimeMessage, recipient, subject, htmlContent);

      Transport.send(mimeMessage);
      printMessage("Email has been sent!");
    } catch (Exception e) {
      log.error(e.getMessage(), e);
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
