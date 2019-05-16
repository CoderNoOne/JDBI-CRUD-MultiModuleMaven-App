package main;

import exceptions.AppException;
import repository.entity_repository.impl.CustomerRepository;
import repository.entity_repository.impl.LoyaltyCardRepository;
import repository.entity_repository.impl.MovieRepository;
import repository.entity_repository.impl.SalesStandRepository;
import repository.others.JoinedEntitiesRepository;
import service.entity_service.CustomerService;
import service.entity_service.LoyaltyCardService;
import service.entity_service.MovieService;
import service.entity_service.SalesStandService;
import service.others.JoinedEntitiesService;
import utils.MenuOptionsUtils;
import utils.UserDataUtils;

import java.util.Arrays;

public class CustomerAndMovieTableManagmentMenu {

  private final CustomerService customerService = new CustomerService(new CustomerRepository());
  private final MovieService movieService = new MovieService(new MovieRepository());
  private final LoyaltyCardService loyaltyCardService = new LoyaltyCardService(new LoyaltyCardRepository());
  private final SalesStandService salesStandService = new SalesStandService(new SalesStandRepository());
  private final JoinedEntitiesService joinedEntitiesService = new JoinedEntitiesService(new JoinedEntitiesRepository());

  void menu() {
    MenuOptionsUtils.customerAndMovieTableManagmentMenu();
    while (true) {
      try {
        int option = UserDataUtils.getInt("INPUT YOUR OPTION: ");
        switch (option) {
          case 1 -> option4_1();
          case 2 -> option4_2();
          case 3 -> option4_3();
          case 4 -> option4_4();
          case 5 -> option4_5();
          case 6 -> option4_6();
          case 7 -> option4_7();
          case 8 -> MenuOptionsUtils.customerAndMovieTableManagmentMenu();
          case 9 -> new MainMenu().mainMenu();
          default -> throw new AppException("INPUT OPTION IS NOT DEFINED");
        }
      } catch (AppException e) {
        System.out.println(e.getExceptionMessage());
        System.err.println(Arrays.toString(e.getStackTrace()));
      }
    }
  }
  private void option4_7() {

  }

  private void option4_6() {
    int movieId = UserDataUtils.getInt("Input movie id");
    movieService.findMovieById(movieId).ifPresent(System.out::println);
  }

  private void option4_5() {
    int customerId = UserDataUtils.getInt("Input customer id");
    movieService.findMovieById(customerId).ifPresent(System.out::println);
  }

  private void option4_4() {
    movieService.showAllMovies();
  }

  private void option4_3() {
    customerService.showAllCustomers();
  }

  private void option4_2() {
    Integer integer = UserDataUtils.getInt("Input movie id you want to delete from database");
    movieService.deleteMovie(integer);
  }

  private void option4_1() {
    Integer integer = UserDataUtils.getInt("Input customer id you want to delete from database");
    customerService.deleteCustomer(integer);
  }

}

