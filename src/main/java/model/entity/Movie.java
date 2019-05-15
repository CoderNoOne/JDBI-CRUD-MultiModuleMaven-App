package model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Movie {

  private Integer id;
  private String title;
  private String genre;
  private BigDecimal price;
  private Integer duration;
  private LocalDate releaseDate;

  @Override
  public String toString() {
    return MessageFormat.format(
            "\nMovie id - {0}\n" +
                    "Movie title - {1}\n" +
                    "Movie genre- {2}\n" +
                    "Movie price - {3}\n" +
                    "Movie duration - {4}\n" +
                    "Movie releaseDate - {5}",

            Objects.nonNull(id) ? id : "Movie object not persisted in db yet",
            title,
            genre,
            price,
            duration,
            releaseDate
    );
  }
}
