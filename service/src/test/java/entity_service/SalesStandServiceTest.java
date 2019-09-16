package entity_service;

import entity.SalesStand;
import entity_repository.impl.SalesStandRepository;
import exceptions.AppException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;


@DisplayName("Test cases for movie service")
@Tag("Services")
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
@ExtendWith(MockitoExtension.class)
class SalesStandServiceTest {


  @Mock
  private SalesStandRepository salesStandRepository;

  @InjectMocks
  private SalesStandService salesStandService;

  @Test
  @DisplayName("add new sale : case arguments are not valid (null)")
  void test1() {

    //given
    Integer movieId = null;
    Integer customerId = null;
    LocalDateTime localDateTime = null;
    String expectedExceptionMessage = "Arguments cannot be null";

    //when
    //then
    AppException actualException = assertThrows(AppException.class, () -> salesStandService.addNewSale(movieId, customerId, localDateTime));
    assertThat(actualException.getExceptionMessage(), is(equalTo(expectedExceptionMessage)));


    then(salesStandRepository).shouldHaveZeroInteractions();
  }

  @Test
  @DisplayName("add new sale : case arguments are non null but start time is not valid)")
  void test2() {

    //given
    Integer movieId = 1;
    Integer customerId = 1;

    LocalDateTime startTime = (LocalDate.now().minusMonths(3)).atStartOfDay();
    String expectedExceptionMessage = "Movie start date time is not valid";

    //when
    //then
    AppException actualException = assertThrows(AppException.class, () ->
            salesStandService.addNewSale(movieId, customerId, startTime));


    assertThat(actualException.getExceptionMessage(), is(equalTo(expectedExceptionMessage)));

  }

  @Test
  @DisplayName("add new sale : case arguments are not null and start time is valid")
  void test3() {

    //given
    Integer movieId = 1;
    Integer customerId = 1;

    LocalDateTime startTime = LocalDate.now().plusDays(1).atTime(13, 30);

    ArgumentCaptor<SalesStand> salesStandArgumentCaptor = ArgumentCaptor.forClass(SalesStand.class);
    doNothing().when(salesStandRepository).add(salesStandArgumentCaptor.capture());

    //when
    //then
    assertDoesNotThrow(() ->
            salesStandService.addNewSale(movieId, customerId, startTime));


    then(salesStandRepository).should(times(1)).add(SalesStand.builder().movieId(movieId).customerId(customerId).startDateTime(startTime).build());
    then(salesStandRepository).shouldHaveNoMoreInteractions();
  }

}
