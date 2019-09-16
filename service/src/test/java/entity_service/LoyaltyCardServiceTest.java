package entity_service;

import entity.Customer;
import entity.LoyaltyCard;
import entity_repository.impl.LoyaltyCardRepository;
import exceptions.AppException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.internal.verification.InOrderWrapper;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

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
  void test4() {

    //given
    Customer customer = Customer.builder()
            .id(1)
            .name("JOHN")
            .surname("STONE")
            .age(30)
            .email("customer@gmail.com")
            .build();


    List<LoyaltyCard> loyaltyCards = getLoyaltyCards();

    ArgumentCaptor<LoyaltyCard> loyaltyCardArgumentCaptor = ArgumentCaptor.forClass(LoyaltyCard.class);

    doAnswer((Answer<Void>) invocationOnMock -> {

      loyaltyCardArgumentCaptor.getValue().setId(2);
      loyaltyCards.add(loyaltyCardArgumentCaptor.getValue());
      return null;
    }).when(loyaltyCardRepository).add(loyaltyCardArgumentCaptor.capture());

    given(loyaltyCardRepository.findAll()).willReturn(loyaltyCards);


    //when
    //then
    Assertions.assertDoesNotThrow(() -> loyaltyCardService.addLoyaltyCardForCustomer(customer));

    assertThat(customer.getLoyaltyCardId(), is(equalTo(2)));
    assertThat(loyaltyCards.get(loyaltyCards.size() - 1).getId(), is(2));

    InOrder inOrder = Mockito.inOrder(loyaltyCardRepository);

    inOrder.verify(loyaltyCardRepository, times(1)).add(loyaltyCardArgumentCaptor.getValue());
    inOrder.verify(loyaltyCardRepository, times(2)).findAll();
    inOrder.verifyNoMoreInteractions();

  }

  @Test
  @DisplayName("decrease movie number by loyalty card id : case loyalty card id is null")
  void test5() {

    //given
    Integer id = null;
    String expectedExceptionMessage = "Loyalty card id is null";

    //when
    //then
    AppException actualException = assertThrows(AppException.class, () -> loyaltyCardService.decreaseMoviesNumberByLoyaltyCardId(id));
    assertThat(actualException.getExceptionMessage(), is(equalTo(expectedExceptionMessage)));

    then(loyaltyCardRepository).shouldHaveZeroInteractions();
  }

  @Test
  @DisplayName("decrease movie number by loyalty card id : case loyalty card is not null and loyalty card doesn't exist")
  void test6() {

    //given
    Integer id = 10;

    ArgumentCaptor<Integer> idCapture = ArgumentCaptor.forClass(Integer.class);
    Optional<LoyaltyCard> loyaltyCard = Optional.empty();

    given(loyaltyCardRepository.findById(idCapture.capture()))
            .willReturn(loyaltyCard);


    //when
    //then
    assertDoesNotThrow(() -> loyaltyCardService.decreaseMoviesNumberByLoyaltyCardId(id));
    assertThat(idCapture.getValue(), is(equalTo(id)));

    then(loyaltyCardRepository).should(times(1)).findById(id);
    then(loyaltyCardRepository).shouldHaveNoMoreInteractions();
  }

  @Test
  @DisplayName("decrease movie number by loyalty card id : case loyalty card is not null and loyalty card exits")
  void test7() {

    //given
    Integer id = 10;

    ArgumentCaptor<Integer> idCapture = ArgumentCaptor.forClass(Integer.class);
    ArgumentCaptor<LoyaltyCard> loyaltyCardCapture = ArgumentCaptor.forClass(LoyaltyCard.class);
    Optional<LoyaltyCard> loyaltyCard = Optional.of(LoyaltyCard.builder()

            .id(id)
            .expirationDate(LocalDate.now().plusMonths(2))
            .discount(new BigDecimal("10"))
            .moviesNumber(2)
            .build());

    given(loyaltyCardRepository.findById(idCapture.capture()))
            .willReturn(loyaltyCard);

    doNothing().when(loyaltyCardRepository).update(loyaltyCardCapture.capture());

    //when
    //then
    assertDoesNotThrow(() -> loyaltyCardService.decreaseMoviesNumberByLoyaltyCardId(id));
    assertThat(idCapture.getValue(), is(equalTo(id)));

    InOrder inOrder = inOrder(loyaltyCardRepository);
    inOrder.verify(loyaltyCardRepository, times(1)).findById(id);
    inOrder.verify(loyaltyCardRepository, times(1)).update(loyaltyCardCapture.getValue());
    inOrder.verifyNoMoreInteractions();

  }

  private List<LoyaltyCard> getLoyaltyCards() {

    return new ArrayList<>(List.of(
            LoyaltyCard.builder()
                    .id(1)
                    .moviesNumber(1)
                    .discount(new BigDecimal("5"))
                    .expirationDate(LocalDate.of(2020, 10, 20))
                    .build()
    ));

  }

}
