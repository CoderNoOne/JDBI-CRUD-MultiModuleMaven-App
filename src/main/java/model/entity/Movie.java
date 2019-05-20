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
public class Movie implements Comparable<Movie>{

  private Integer id;
  private String title;
  private String genre;
  private BigDecimal price;
  private Integer duration;
  private LocalDate releaseDate;

  @Override
  public int compareTo(Movie o) {
    return this.getTitle().compareTo(o.getTitle());
  }
}
