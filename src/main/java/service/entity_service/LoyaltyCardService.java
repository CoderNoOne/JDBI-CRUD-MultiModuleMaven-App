package service.entity_service;

import exceptions.AppException;
import lombok.RequiredArgsConstructor;
import model.entity.Customer;
import model.entity.LoyaltyCard;
import repository.entity_repository.impl.LoyaltyCardRepository;
import validators.impl.LoyaltyCardValidator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@RequiredArgsConstructor
public class LoyaltyCardService {

  private final int LOYALTY_CARD_MIN_MOVIE_NUMBER = 3;
  private final LoyaltyCardRepository loyaltyCardRepository;

  public int getLOYALTY_CARD_MIN_MOVIE_NUMBER() {
    return LOYALTY_CARD_MIN_MOVIE_NUMBER;
  }

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

  public void addLoyaltyCardForCustomer(Customer customer) {
    if (addNewLoyaltyCard()) {
      customer.setLoyaltyCardId(getNewlyCreatedLoyaltyCardId());
    }
  }

  private boolean addNewLoyaltyCard() {
    return addLoyaltyCard(new BigDecimal("5"), LocalDate.now().plusMonths(3), 2);

  }

  private Integer getNewlyCreatedLoyaltyCardId() {
    return loyaltyCardRepository.findAll().get(loyaltyCardRepository.findAll().size() - 1).getId();
  }

  public void decreaseMoviesNumberByLoyaltyCardId(Integer loyaltyCardId) {
    var loyaltyCardOptional = loyaltyCardRepository.findById(loyaltyCardId);

    if (loyaltyCardOptional.isPresent() && loyaltyCardOptional.get().getExpirationDate().compareTo(LocalDate.now()) > 0) {
      var loyaltyCard = loyaltyCardOptional.get();
      loyaltyCard.setMoviesNumber(loyaltyCard.getMoviesNumber() - 1);
      loyaltyCardRepository.update(loyaltyCard);
    }
  }

  public Optional<LoyaltyCard> findLoyaltyCardById(Integer id) {
    return loyaltyCardRepository.findById(id);
  }

//  private void sendMovieDetailsToCustomerEmail(String email, String subject, Movie movie, LocalDateTime startDateTime) {
//    EmailUtils.sendMoviePurchaseConfirmation(email, subject, movie, startDateTime);
//  }
}
