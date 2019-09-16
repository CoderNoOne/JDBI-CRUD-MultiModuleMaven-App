package entity_service;

import entity_repository.impl.LoyaltyCardRepository;
import exceptions.AppException;
import lombok.RequiredArgsConstructor;
import entity.Customer;
import entity.LoyaltyCard;
import validators.impl.LoyaltyCardValidator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static others.SimulateTimeFlowUtils.getClock;
import static others.UserDataUtils.getString;
import static others.UserDataUtils.printMessage;

@RequiredArgsConstructor
public class LoyaltyCardService {

  private final int LOYALTY_CARD_MIN_MOVIE_NUMBER = 3;
  private final LoyaltyCardRepository loyaltyCardRepository;

  private LoyaltyCard createLoyaltyCard(BigDecimal discount, LocalDate expirationDate, Integer moviesNumber) {
    return LoyaltyCard.builder().discount(discount).expirationDate(expirationDate).moviesNumber(moviesNumber).build();
  }

  private boolean addLoyaltyCard(BigDecimal discount, LocalDate expirationDate, Integer moviesNumber) {
    var loyaltyCard = createLoyaltyCard(discount, expirationDate, moviesNumber);
    boolean isValid = new LoyaltyCardValidator().validateEntity(loyaltyCard, false);

    if (isValid) {
      loyaltyCardRepository.add(loyaltyCard);
    }
    return isValid;

  }

  public void addLoyaltyCardForCustomer(Customer customer) {

    if (customer == null) {
      throw new AppException("Customer is null");
    }

    if (addNewLoyaltyCard()) {
      customer.setLoyaltyCardId(getNewlyCreatedLoyaltyCardId());
    }
  }

  private boolean addNewLoyaltyCard() {
    return addLoyaltyCard(new BigDecimal("5"), LocalDate.now(getClock()).plusMonths(3), 2);
  }

  private Integer getNewlyCreatedLoyaltyCardId() {
    return loyaltyCardRepository.findAll().get(loyaltyCardRepository.findAll().size() - 1).getId();
  }

  public void decreaseMoviesNumberByLoyaltyCardId(Integer loyaltyCardId) {

    if (loyaltyCardId == null) {
      throw new AppException("Loyalty card id is null");
    }

    var loyaltyCardOptional = loyaltyCardRepository.findById(loyaltyCardId);

    if (loyaltyCardOptional.isPresent() && loyaltyCardOptional.get().getExpirationDate().compareTo(LocalDate.now(getClock())) > 0) {
      var loyaltyCard = loyaltyCardOptional.get();
      loyaltyCard.setMoviesNumber(loyaltyCard.getMoviesNumber() - 1);
      loyaltyCardRepository.update(loyaltyCard);
    }
  }

  public Optional<LoyaltyCard> findLoyaltyCardById(Integer id) {
    if (id == null) {
      throw new AppException("Id is null");
    }

    return loyaltyCardRepository.findById(id);
  }

  public int getLoyaltyMinMovieCard() {
    return LOYALTY_CARD_MIN_MOVIE_NUMBER;
  }
}
