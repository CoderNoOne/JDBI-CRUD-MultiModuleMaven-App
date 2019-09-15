package entity_service;

import entity.Customer;
import entity_repository.impl.CustomerRepository;
import exceptions.AppException;
import org.junit.jupiter.api.Assertions;
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

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@DisplayName("Test cases for customer service")
@Tag("Services")
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {


  @Mock
  private CustomerRepository customerRepository;

  @InjectMocks
  private CustomerService customerService;


  @Test
  @DisplayName("Delete customer by id")
  void test1() {

    //given
    Integer id = 10;

    //when
    //then
    Assertions.assertDoesNotThrow(() -> customerService.deleteCustomer(id));
    then(customerRepository).should(times(1)).delete(id);
    then(customerRepository).shouldHaveNoMoreInteractions();

  }

  @Test
  @DisplayName("Delete customer by id : case - customer id is null")
  void test2() {

    //given
    Integer id = null;
    String expectedExceptionMessage = "Customer id is null";

    //when
    //then
    AppException actualException = assertThrows(AppException.class, () -> customerService.deleteCustomer(id));
    assertThat(actualException.getExceptionMessage(), is(equalTo(expectedExceptionMessage)));
    then(customerRepository).shouldHaveZeroInteractions();

  }

  @Test
  @DisplayName("Get all customers")
  void test3() {

    //given

    List<Customer> expectedResult = List.of(
            Customer.builder()
                    .id(1)
                    .name("John")
                    .surname("Stone")
                    .age(20)
                    .email("stone@gmail.com")
                    .build(),

            Customer.builder()
                    .id(2)
                    .name("Alice")
                    .surname("Green")
                    .age(20)
                    .email("alice@gmail.com")
                    .build());


    given(customerRepository.findAll())
            .willReturn(expectedResult);

    //when
    //then
    Assertions.assertDoesNotThrow(() -> {
      List<Customer> actualCustomers = customerService.getAllCustomers();
      assertThat(actualCustomers, is(equalTo(expectedResult)));
    });

    then(customerRepository).should(times(1)).findAll();
    then(customerRepository).shouldHaveNoMoreInteractions();
  }

  @Test
  @DisplayName("Find customer by Id : case Id is null")
  void test4() {

    //given
    Integer id = null;
    String expectedExceptionMessage = "Customer id is null";

    //when
    //then
    AppException actualException = assertThrows(AppException.class, () -> customerService.findCustomerById(id));
    assertThat(actualException.getExceptionMessage(), is(equalTo(expectedExceptionMessage)));

    then(customerRepository).shouldHaveZeroInteractions();
  }

  @Test
  @DisplayName("Find customer by id : case id is not null")
  void test5() {

    //given
    Integer id = 10;

    given(customerRepository.findById(id))
            .willReturn(Optional.empty());

    //when
    //then
    Assertions.assertDoesNotThrow(() -> {
      Optional<Customer> actualCustomer = customerService.findCustomerById(id);
      assertThat(actualCustomer, is(equalTo(Optional.empty())));
    });

    then(customerRepository).should(times(1)).findById(id);
    then(customerRepository).shouldHaveNoMoreInteractions();

  }

  @Test
  @DisplayName("Get customer from user input : case - incorrect (null) input arguments")
  void test6() {

    //given
    String name = null;
    String surname = "Stone";
    String email = "stone@gmail.com";

    String expectedExceptionMessage = String.format("Not valid input arguments (null): %s, %s, %s", name, surname, email);

    //when
    //then
    AppException actualException = assertThrows(AppException.class, () -> customerService.getCustomerFromUserInput(name, surname, email));
    assertThat(actualException.getExceptionMessage(), is(equalTo(expectedExceptionMessage)));

    then(customerRepository).shouldHaveZeroInteractions();
  }

  @Test
  @DisplayName("Get customer from user input : case - correct (non null) input arguments; customer doesn't exist in DB")
  void test7() {

    //given
    String name = "John";
    String surname = "Stone";
    String email = "stone@gmail.com";
    String expectedExceptionMessage = String.format("Customer with name: %s, surname %s, email %s is not registered in a db", name, surname, email);

    given(customerRepository.findByNameSurnameAndEmail(name, surname, email))
            .willReturn(Optional.empty());

    //when
    AppException actualException = assertThrows(AppException.class, () ->
            customerService.getCustomerFromUserInput(name, surname, email));

    assertThat(actualException.getExceptionMessage(), is(equalTo(expectedExceptionMessage)));

    //then
    then(customerRepository).should(times(1)).findByNameSurnameAndEmail(name, surname, email);
    then(customerRepository).shouldHaveNoMoreInteractions();

  }

  @Test
  @DisplayName("Get customer from user input : case - correct (non null) input arguments; customer exists in DB")
  void test8() {

    //given
    String name = "John";
    String surname = "Stone";
    String email = "stone@gmail.com";

    Optional<Customer> customer = Optional.of(
            Customer.builder()
                    .name(name)
                    .surname(surname)
                    .age(30)
                    .loyaltyCardId(2)
                    .build()

    );
    given(customerRepository.findByNameSurnameAndEmail(name, surname, email))
            .willReturn(customer);

    //when
    Assertions.assertDoesNotThrow(() -> {
      Customer actualResult = customerService.getCustomerFromUserInput(name, surname, email);
      assertThat(actualResult, is(equalTo(customer.get())));
    });


    then(customerRepository).should(times(1)).findByNameSurnameAndEmail(name, surname, email);
    then(customerRepository).shouldHaveNoMoreInteractions();

  }

  @Test
  @DisplayName("Update Customer : case Customer is null")
  void test9() {

    //given
    Customer customer = null;
    var expectedExceptionMessage = "Customer is null";

    //when
    //then
    AppException actualException = assertThrows(AppException.class, () -> customerService.updateCustomer(customer));
    assertThat(actualException.getExceptionMessage(), is(equalTo(expectedExceptionMessage)));

    then(customerRepository).shouldHaveZeroInteractions();
  }

  @Test
  @DisplayName("Update customer : case Customer is not valid")
  void test10() {

    //given
    Customer customer = Customer.builder()
            .age(-1)
            .name("john")
            .build();

    //when
    //then
    Assertions.assertDoesNotThrow(() -> {
      boolean isCorrect = customerService.updateCustomer(customer);
      assertThat(isCorrect, is(false));
    });

    then(customerRepository).shouldHaveZeroInteractions();

  }

  @Test
  @DisplayName("Update customer : case customer is valid")
  void test11() {

    //given
    Customer customer = Customer.builder()
            .name("JOHN")
            .surname("STONE")
            .age(30)
            .email("j.stone@gmail.com")
            .build();

    //when
    //then
    Assertions.assertDoesNotThrow(() -> {
      boolean actualResult = customerService.updateCustomer(customer);
      assertThat(actualResult, is(equalTo(true)));
    });

    then(customerRepository).should(times(1)).update(customer);
    then(customerRepository).shouldHaveNoMoreInteractions();
  }

  @Test
  @DisplayName("Add customer, case : null arguments")
  void test12() {

    //given
    String name = null;
    String surname = null;
    Integer age = null;
    String email = null;
    String expectedExceptionMessage = "Arguments (name, surname, age, email) cannot be null";

    //when
    //then
    AppException actualException = assertThrows(AppException.class, () -> {
      boolean actualResult = customerService.addCustomer(name, surname, age, email);
      assertThat(actualResult, is(equalTo(false)));
    });
    assertThat(actualException.getExceptionMessage(), is(equalTo(expectedExceptionMessage)));

    then(customerRepository).shouldHaveZeroInteractions();

  }

  @Test
  @DisplayName("Add customer, case : non null but not valid arguments")
  void test13() {

    //given
    String name = "john";
    String surname = "stone";
    Integer age = 15;
    String email = "ad";

    //when
    //then
    Assertions.assertDoesNotThrow(() -> {
      boolean actualResult = customerService.addCustomer(name, surname, age, email);
      assertThat(actualResult, is(false));
    });

    then(customerRepository).shouldHaveZeroInteractions();
  }


  @Test
  @DisplayName("Add customer, case : valid arguments and email is unique")
  void test14() {

    //given

    String name = "JOHN";
    String surname = "STONE";
    Integer age = 30;
    String email = "j.stone@gmail.com";
    ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

    given(customerRepository.findCustomerByEmail(email))
            .willReturn(Optional.empty());

    //when
    //then
    Assertions.assertDoesNotThrow(() -> {
      boolean actualResult = customerService.addCustomer(name, surname, age, email);
      assertThat(actualResult, is(true));
    });

    then(customerRepository)
            .should(times(1))
            .findCustomerByEmail(email);

    then(customerRepository)
            .should(times(1))
            .add(customerArgumentCaptor.capture());

    then(customerRepository)
            .shouldHaveNoMoreInteractions();

    assertThat(CustomerMatcher.matches(name, surname, age, email).matchesSafely(customerArgumentCaptor.getValue()), is(true));
  }

  @Test
  @DisplayName("Add customer, case : valid arguments and email is not unique")
  void test15() {

    //given
    String name = "JOHN";
    String surname = "STONE";
    Integer age = 30;
    String email = "j.stone@gmail.com";


    given(customerRepository.findCustomerByEmail(email))
            .willReturn(Optional.of(
                    Customer.builder()
                            .id(1)
                            .name(name)
                            .surname(surname)
                            .age(age)
                            .email(email)
                            .build()));

    //when
    //then
    Assertions.assertDoesNotThrow(() -> {
      boolean actualResult = customerService.addCustomer(name, surname, age, email);
      assertThat(actualResult, is(false));
    });

    then(customerRepository)
            .should(times(1))
            .findCustomerByEmail(email);

    then(customerRepository)
            .shouldHaveNoMoreInteractions();

  }
}
