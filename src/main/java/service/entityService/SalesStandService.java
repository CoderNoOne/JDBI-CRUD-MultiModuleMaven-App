package service.entityService;

import lombok.RequiredArgsConstructor;
import model.CustomerWithMoviesAndSalesStand;
import model.SalesStand;
import repository.impl.SalesStandRepository;

import java.util.List;

@RequiredArgsConstructor
public class SalesStandService {

  private final SalesStandRepository salesStandRepository;

  public List<CustomerWithMoviesAndSalesStand> ticketsTransactionHistory(Integer id){
    return salesStandRepository.getAllTicketsByCustomerId(id);
  }


}
