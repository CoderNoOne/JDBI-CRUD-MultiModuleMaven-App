package enums;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public enum CustomerField {
  NAME, SURNAME, AGE, EMAIL;

  public static CustomerField fromString(String value){

    CustomerField customerField = null;

    try{
      customerField = valueOf(value);
    }catch (Exception e){
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
    }

    return customerField;
  }
}
