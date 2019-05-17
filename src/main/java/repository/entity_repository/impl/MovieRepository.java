package repository.entity_repository.impl;

import connection.DbConnection;
import exceptions.AppException;
import model.entity.Movie;
import org.jdbi.v3.core.Jdbi;
import repository.entity_repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public class MovieRepository implements CrudRepository<Movie> {

  private Jdbi jdbi = DbConnection.getInstance().getJdbi();

  @Override
  public void add(Movie movie) {

    if (movie == null) {
      throw new AppException("movie object is null");
    }

    jdbi.withHandle(handle -> handle
            .createUpdate("insert into movies (title, genre, price, duration, release_date) values (:title, :genre, :price, :duration, :releaseDate)")
            .bind("title", movie.getTitle())
            .bind("genre", movie.getGenre())
            .bind("price", movie.getPrice())
            .bind("duration", movie.getDuration())
            .bind("releaseDate", movie.getReleaseDate())
            .execute());
  }

  @Override
  public void update(Movie movie) {

    if (movie == null) {
      throw new AppException("movie object is null");
    }

    if (movie.getId() == null) {
      throw new AppException("movie id is null");
    }

    jdbi.withHandle(handle -> handle
            .createQuery("select * from movies where id = :id")
            .bind("id", movie.getId())
            .mapToBean(Movie.class)
            .findFirst())
            .ifPresent(movieFromDB -> jdbi.withHandle(handle -> handle
                    .createUpdate("update movies set title = :title, genre = :genre, price = :price, duration = :duration, release_date = :releaseDate where id = :id")
                    .bind("title", movie.getTitle() == null ? movieFromDB.getTitle() : movie.getTitle())
                    .bind("genre", movie.getGenre() == null ? movieFromDB.getGenre() : movie.getGenre())
                    .bind("price", movie.getPrice() == null ? movieFromDB.getPrice() : movie.getPrice())
                    .bind("duration", movie.getDuration() == null ? movieFromDB.getDuration() : movie.getDuration())
                    .bind("releaseDate", movie.getReleaseDate() == null ? movieFromDB.getReleaseDate() : movie.getReleaseDate())
                    .bind("id", movie.getId())
                    .execute()));

  }

  @Override
  public void delete(Integer id) {

    if (id == null) {
      throw new AppException("id is null");
    }

    jdbi.withHandle(handle -> handle
            .createUpdate("delete from movies where id = :id")
            .bind("id", id)
            .execute());
  }

  @Override
  public Optional<Movie> findById(Integer id) {

    if (id == null) {
      throw new AppException("id is null");
    }

    return jdbi.withHandle(handle -> handle
            .createQuery("select * from movies where id = :id")
            .bind("id", id)
            .mapToBean(Movie.class)
            .findFirst());
  }

  @Override
  public List<Movie> findAll() {
    return jdbi.withHandle(handle -> handle
            .createQuery("select * from movies")
            .mapToBean(Movie.class)
            .list());
  }

  @Override
  public void deleteAll() {
    jdbi.withHandle(handle -> handle
            .createUpdate("delete from movies")
            .execute());
  }

}
