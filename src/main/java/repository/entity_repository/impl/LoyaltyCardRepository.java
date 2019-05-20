package repository.entity_repository.impl;

import connection.DbConnection;
import exceptions.AppException;
import model.entity.LoyaltyCard;
import org.jdbi.v3.core.Jdbi;
import repository.entity_repository.AbstractCrudRepository;

public class LoyaltyCardRepository extends AbstractCrudRepository<LoyaltyCard> {
  private Jdbi jdbi = DbConnection.getInstance().getJdbi();

  @Override
  public void add(LoyaltyCard loyaltyCard) {

    if (loyaltyCard == null) {
      throw new AppException("loyaltyCard object is null");
    }

    jdbi.withHandle(handle -> handle
            .createUpdate("insert into loyalty_cards (expiration_date, discount, movies_number) values (:expirationDate, :discount, :moviesNumber)")
            .bind("expirationDate", loyaltyCard.getExpirationDate())
            .bind("discount", loyaltyCard.getDiscount())
            .bind("moviesNumber", loyaltyCard.getMoviesNumber())
            .execute());
  }

  @Override
  public void update(LoyaltyCard loyaltyCard) {

    if (loyaltyCard == null) {
      throw new AppException("loyaltyCard object is null");
    }

    if (loyaltyCard.getId() == null) {
      throw new AppException("loyalty Card id is null");
    }

    jdbi.withHandle(handle -> handle
            .createQuery("select * from loyalty_cards where id = :id")
            .bind("id", loyaltyCard.getId())
            .mapToBean(LoyaltyCard.class)
            .findFirst())
            .ifPresent(loyaltyCardFromDb -> jdbi.withHandle(handle -> handle
                    .createUpdate("update loyalty_cards set expiration_date = :expirationDate, discount = :discount, movies_number = :moviesNumber where id = :id")
                    .bind("expirationDate", loyaltyCard.getExpirationDate() == null ? loyaltyCardFromDb.getExpirationDate() : loyaltyCard.getExpirationDate())
                    .bind("discount", loyaltyCard.getDiscount() == null ? loyaltyCardFromDb.getDiscount() : loyaltyCard.getDiscount())
                    .bind("moviesNumber", loyaltyCard.getMoviesNumber())
                    .bind("id", loyaltyCard.getId())
                    .execute()));
  }
}
