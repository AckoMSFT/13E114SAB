package rs.etf.sab.operations;


import rs.etf.sab.student.DBUtils;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

public interface OrderOperations {
    int addArticle(int orderId, int articleId, int count);

    int removeArticle(int orderId, int articleId);

    List<Integer> getItems(int orderId);

    int completeOrder(int orderId);

    BigDecimal getFinalPrice(int orderId);

    BigDecimal getDiscountSum(int orderId);

    String getState(int orderId);

    Calendar getSentTime(int orderId);

    Calendar getRecievedTime(int orderId);

    int getBuyer(int orderId);

    int getLocation(int orderId);
    void findShortestPath(int orderId, int orderAssemblyCity, int orderDestinationCity);

    List<Integer> getPath(int orderId);

    int getArticleCount(int orderId, int articleId);
}
