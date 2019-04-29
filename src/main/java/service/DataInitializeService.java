package service;

public class DataInitializeService {

    public void init()
    {
      deleteLoyaltyCards();
      deleteSalesStands();
      initMovies("");
      initCustomers("");
    }

    private void initMovies(String moviesJsonFilename) {
      // z pliku json konwertujesz dane i ladujesz do tabeli movies wczesniej
      // czyszczac cala jej dotychczasowa zawartosc
    }

    private void initCustomers(String customersJsonFilenam){}

    private void deleteLoyaltyCards() {}
    private void deleteSalesStands() {}
}
