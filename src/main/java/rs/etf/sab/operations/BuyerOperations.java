package rs.etf.sab.operations;

import java.math.BigDecimal;
import java.util.List;

public interface BuyerOperations {
    int createBuyer(String name, int cityId);

    int setCity(int buyerId, int cityId);

    int getCity(int buyerId);

    BigDecimal increaseCredit(int buyerId, BigDecimal credit);

    int createOrder(int buyerId);

    List<Integer> getOrders(int buyerId);

    BigDecimal getCredit(int buyerId);
}
