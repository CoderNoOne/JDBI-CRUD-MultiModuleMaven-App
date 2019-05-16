package service.entity_service;

import exceptions.AppException;
import lombok.RequiredArgsConstructor;
import model.entity.Customer;
import model.entity.LoyaltyCard;
import repository.impl.LoyaltyCardRepository;
import utils.UserDataUtils;
import validators.impl.LoyaltyCardValidator;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Optional;

@RequiredArgsConstructor
public class LoyaltyCardService {

  private final int LOYALTY_CARD_MIN_MOVIE_NUMBER = 5;
  private final LoyaltyCardRepository loyaltyCardRepository;

  private LoyaltyCard createLoyaltyCard(BigDecimal discount, LocalDate expirationDate, Integer moviesNumber) {
    return LoyaltyCard.builder()
            .discount(discount)
            .expirationDate(expirationDate)
            .moviesNumber(moviesNumber)
            .build();
  }

  private boolean addLoyaltyCard(BigDecimal discount, LocalDate expirationDate, Integer moviesNumber) {
    var loyaltyCard = createLoyaltyCard(discount, expirationDate, moviesNumber);
    boolean isValid = new LoyaltyCardValidator().validateEntity(loyaltyCard);

    if (isValid) {
      loyaltyCardRepository.add(loyaltyCard);
    }
    return isValid;
  }


  public void verifyIfCustomerCanGetLoyaltyCard(Integer ticketsNumber, Integer customerId) {

    if (/*odczyt z pliku jesli nie ma id customera to wez ticketsNumber z argumentu metody, w przeciwnym razie wez ticketsNumber - wartosc odczytana z pliku*/ticketsNumber >= LOYALTY_CARD_MIN_MOVIE_NUMBER) {
      switch (UserDataUtils.getString("Do you want to add a loyalty card? (y/n)").toLowerCase()) {
        case "y" -> {
          addNewLoyaltyCard();
          //zapis do pliku aktualnej wartosci ticketsNumber dla klienta
        }
        case "n" -> System.out.println("TOO BAD. MAYBE NEXT TIME!");
        default -> verifyIfCustomerCanGetLoyaltyCard(ticketsNumber, customerId);/*throw new AppException("ACTION NOT DEFINED");*/
      }
    }

  }

  private Integer readTicketsNumberFromFileByCustomerId(int customerId) {

    String ticketsNumber = "-1"; //jeżęli taka wartość customera o danym Id nie ma w zapisanego w pliku
    try {
      ticketsNumber = Files.readAllLines(Paths.get("properties.txt")).stream()
              .filter(line -> line.startsWith(String.valueOf(customerId)))
              .map(line -> line.substring((line.indexOf('=') + 1)))
              .findFirst().orElse(ticketsNumber);

    } catch (IOException e) {
      e.printStackTrace();
      throw new AppException("Error with properties.txt file reading");
    }

    return Integer.parseInt(ticketsNumber);
  }

  private void addOrUpdateTicketsNumberToFileBoughtByCustomer(int ticketsNumber, int customerId) {


  }

  private boolean addNewLoyaltyCard() {
    return addLoyaltyCard(new BigDecimal("5"), LocalDate.now().plusMonths(3), 2);

  }

  public Integer getNewlyCreatedLoyaltyCardId() {
    return loyaltyCardRepository.findAll().get(loyaltyCardRepository.findAll().size() - 1).getId();
  }

  public boolean doCustomerPosesActiveLoyaltyCardByCustomerId(Integer customerId) {
    return loyaltyCardRepository.getCustomerWithLoyaltyCardInfoByCustomerId(customerId).isPresent() &&
            loyaltyCardRepository.getCustomerWithLoyaltyCardInfoByCustomerId(customerId).get().getLoyaltyCardExpirationDate().compareTo(LocalDate.now()) > 0;
  }
}
