package entity_service;

import entity.Customer;
import entity.LoyaltyCard;
import entity_repository.impl.CustomerRepository;
import entity_repository.impl.LoyaltyCardRepository;
import exceptions.AppException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@DisplayName("Test cases for loyalty card service")
@Tag("Services")
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
@ExtendWith(MockitoExtension.class)
class LoyaltyCardServiceTest {


  @Mock
  private LoyaltyCardRepository loyaltyCardRepository;

  @InjectMocks
  private LoyaltyCardService loyaltyCardService;


  @Test
  @DisplayName("Find loyalty card by id : case id is null")
  void test1() {

    //given
    Integer id = null;
    String expectedExceptionMessage = "Id is null";

    //when
    //then
    AppException actualException = assertThrows(AppException.class, () -> loyaltyCardService.findLoyaltyCardById(id));
    assertThat(actualException.getExceptionMessage(), is(equalTo(expectedExceptionMessage)));

    then(loyaltyCardRepository).shouldHaveZeroInteractions();
  }


  @Test
  @DisplayName("Find loyalty card by id : case id is not null")
  void test2() {

    //given
    Integer id = 2;

    LocalDate date = LocalDate.of(2020, 10, 20);

    Optional<LoyaltyCard> expectedResult = Optional.of(LoyaltyCard.builder()
            .id(1)
            .discount(new BigDecimal("0.2"))
            .moviesNumber(2)
            .expirationDate(date)
            .build());

    given(loyaltyCardRepository.findById(id))
            .willReturn(expectedResult);

    //when
    //then
    Assertions.assertDoesNotThrow(() -> {
      Optional<LoyaltyCard> actualResult = loyaltyCardService.findLoyaltyCardById(id);
      assertThat(expectedResult, is(equalTo(actualResult)));
    });

    then(loyaltyCardRepository).should(times(1)).findById(id);
    then(loyaltyCardRepository).shouldHaveNoMoreInteractions();
  }

  @Test
  @DisplayName("Add loyalty card for customer : case customer is null")
  void test3() {

    //given
    Customer customer = null;
    String expectedExceptionMessage = "Customer is null";

    //when
    //then
    AppException actualException = assertThrows(AppException.class, () -> loyaltyCardService.addLoyaltyCardForCustomer(customer));
    assertThat(actualException.getExceptionMessage(), is(equalTo(expectedExceptionMessage)));


    then(loyaltyCardRepository).shouldHaveZeroInteractions();
  }

  @Test
  @DisplayName("Add loyalty card for customer : case customer is not null")
  void test4(){

    //given


    //when

    //then


  }

}
