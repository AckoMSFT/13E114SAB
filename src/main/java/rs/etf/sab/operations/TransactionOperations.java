package rs.etf.sab.operations;


import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

public interface TransactionOperations {
    BigDecimal getBuyerTransactionsAmmount(int buyerId);

    BigDecimal getShopTransactionsAmmount(int shopId);

    List<Integer> getTransationsForBuyer(int buyerId);

    int getTransactionForBuyersOrder(int orderId);

    int getTransactionForShopAndOrder(int orderId, int shopId);

    List<Integer> getTransationsForShop(int shopId);

    Calendar getTimeOfExecution(int transactionId);

    BigDecimal getAmmountThatBuyerPayedForOrder(int orderId);

    BigDecimal getAmmountThatShopRecievedForOrder(int shopId, int orderId);

    BigDecimal getTransactionAmount(int transactionId);

    BigDecimal getSystemProfit();
}
