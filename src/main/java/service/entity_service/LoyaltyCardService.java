package service.entity_service;

import exceptions.AppException;
import lombok.RequiredArgsConstructor;
import model.entity.Customer;
import model.entity.LoyaltyCard;
import repository.impl.LoyaltyCardRepository;
import utils.UserDataUtils;
import validators.impl.LoyaltyCardValidator;

import java.math.BigDecimal;
import java.time.LocalDate;

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


  public void verifyIfCustomerCanGetLoyaltyCard(Integer ticketsNumber) {

    if (ticketsNumber >= LOYALTY_CARD_MIN_MOVIE_NUMBER) {
      switch (UserDataUtils.getString("Do you want to add a loyalty card? (y/n)").toLowerCase()) {
        case "y" -> addNewLoyaltyCard();
        case "n" -> System.out.println("TOO BAD. MAYBE NEXT TIME!");
        default -> verifyIfCustomerCanGetLoyaltyCard(ticketsNumber);/*throw new AppException("ACTION NOT DEFINED");*/
      }
    }

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
