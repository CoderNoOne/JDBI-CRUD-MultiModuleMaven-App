package model.others;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovieWithSalesStand {

  private Integer movieId;
  private String movieTitle;
  private String movieGenre;
  private BigDecimal moviePrice;
  private Integer movieDuration;
  private LocalDate movieReleaseDate;
}
