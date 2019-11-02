package account.control;

import java.math.BigDecimal;

public class AccountUtils {

    private AccountUtils() {

    }

    public static BigDecimal convertDoubleToBigDecimal(Double value) {
        String valueAsString = value.toString();
        return new BigDecimal(valueAsString);
    }
}
