package repository.impl;

import connection.DbConnection;
import exceptions.AppException;
import model.LoyaltyCard;
import org.jdbi.v3.core.Jdbi;
import repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public class LoyaltyCardRepository implements CrudRepository<LoyaltyCard> {
  private Jdbi jdbi = DbConnection.getInstance().getJdbi();

  @Override
  public void add(LoyaltyCard loyaltyCard) {

    if (loyaltyCard == null) {
      throw new AppException("loyaltyCard object is null");
    }

    jdbi.withHandle(handle -> handle
            .createUpdate("insert into loyaltyCards (expiration_date, discount, movies_number) values (?, ?, ?)")
            .bind(0, loyaltyCard.getExpirationDate())
            .bind(1, loyaltyCard.getDiscount())
            .bind(2, loyaltyCard.getMoviesNumber())
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
                    .createUpdate("update loyalty_cards set expiration_date = ?, discount = ?, movies_number = ? where id = ?")
                    .bind(0, loyaltyCard.getExpirationDate() == null ? loyaltyCardFromDb.getExpirationDate() : loyaltyCard.getExpirationDate())
                    .bind(1, loyaltyCard.getDiscount() == null ? loyaltyCardFromDb.getDiscount() : loyaltyCard.getDiscount())
                    .bind(2, loyaltyCard.getMoviesNumber() == null ? loyaltyCardFromDb.getMoviesNumber() : loyaltyCard.getMoviesNumber())
                    .execute()));

  }

  @Override
  public void delete(Integer id) {

    if (id == null) {
      throw new AppException("id is null");
    }

    jdbi.withHandle(handle -> handle
            .createUpdate("delete from loyalty_cards where id = :id")
            .bind("id", id)
            .execute());
  }

  @Override
  public Optional<LoyaltyCard> findById(Integer id) {

    if (id == null) {
      throw new AppException("id is null");
    }

    return jdbi.withHandle(handle -> handle
            .createQuery("select * from loyalty_cards where id = :id")
            .bind("id", id)
            .mapToBean(LoyaltyCard.class)
            .findFirst());
  }

  @Override
  public List<LoyaltyCard> findAll() {
    return jdbi.withHandle(handle -> handle
            .createQuery("select * from loyalty_cards")
            .mapToBean(LoyaltyCard.class)
            .list());
  }
}
