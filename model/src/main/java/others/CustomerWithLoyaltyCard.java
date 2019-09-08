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
public class CustomerWithLoyaltyCard {

  private Integer customerId;
  private Integer moviesNumber;
  private BigDecimal discount;
  private LocalDate loyaltyCardExpirationDate;
  private Integer loyaltyCardId;
}
