package model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SalesStand {

  private Integer id;
  private Integer customerId;
  private Integer movieId;
  private LocalDateTime startDateTime; // LocalDateTime

}
