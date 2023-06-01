package rs.etf.sab.student;

import rs.etf.sab.operations.TransactionOperations;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipError;

public class ia130010_TransactionOperations implements TransactionOperations {

    private BigDecimal ZERO = new BigDecimal(0).setScale(3);

    private BigDecimal MINUS_ONE = new BigDecimal(-1).setScale(3);

    /*
        java.math.BigDecimal getBuyerTransactionsAmmount(int buyerId)
        Gets sum of all transactions amounts for buyer

        Parameters:
        buyerId - buyer's id

        Returns:
        sum of all transactions, 0 if there are not transactions, -1 if failure
     */
    @Override
    public BigDecimal getBuyerTransactionsAmmount(int buyerId) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT SUM([Transaction].Amount) FROM [Transaction] [Transaction], [Order] [Order]\n" +
                "WHERE [Transaction].OrderId = [Order].Id AND [Order].BuyerId = " + buyerId + " AND [Transaction].Type = " + OrderHelper.TransactionType.BUYER.getType();
        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(query)) {
                if (resultSet.next()) {
                    BigDecimal amount = resultSet.getBigDecimal(1);
                    if (amount == null) {
                        return ZERO;
                    }
                    return amount.setScale(3);
                } else {
                    return ZERO;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return MINUS_ONE;
    }

    /*
        java.math.BigDecimal getShopTransactionsAmmount(int shopId)
        Gets sum of all transactions amounts for shop

        Parameters:
        shopId - shop's id

        Returns:
        sum of all transactions, 0 if there are not transactions, -1 if failure
     */
    @Override
    public BigDecimal getShopTransactionsAmmount(int shopId) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT SUM(Amount) FROM [dbo].[Transaction] WHERE ShopId = " + shopId;
        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(query)) {
                if (resultSet.next()) {
                    BigDecimal amount = resultSet.getBigDecimal(1);
                    if (amount == null) {
                        return ZERO;
                    }
                    return amount.setScale(3);
                } else {
                    return ZERO;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return MINUS_ONE;
    }

    /*
        java.util.List<java.lang.Integer> getTransationsForBuyer(int buyerId)
        Gets all transactions for buyer

        Parameters:
        buyerId - buyer id

        Returns:
        list of transations ids, null if failure
     */
    @Override
    public List<Integer> getTransationsForBuyer(int buyerId) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT Id FROM [dbo].[Transaction] WHERE BuyerId = " + buyerId + " ";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            List<Integer> ids = new ArrayList<>();
            while (resultSet.next()) {
                ids.add(resultSet.getInt(1));
            }
            return ids;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
        int getTransactionForBuyersOrder(int orderId)
        Gets transaction that buyer made for paying an order.

        Parameters:
        orderId - order's id

        Returns:
        transaction's id, -1 if failure
     */
    @Override
    public int getTransactionForBuyersOrder(int orderId) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT Id FROM [dbo].[Transaction] WHERE OrderId = " + orderId + " AND Type = " + OrderHelper.TransactionType.BUYER.getType();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*
        int getTransactionForShopAndOrder(int orderId, int shopId)
        Gets transaction for recieved order that system made to shop.

        Parameters:
        orderId - order's id
        shopId - shop's id

        Returns:
        transaction's id, -1 if failure
     */
    @Override
    public int getTransactionForShopAndOrder(int orderId, int shopId) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT Id FROM [dbo].[Transaction] WHERE OrderId = " + orderId + " AND ShopId = " + shopId;
        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(query)) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*
        java.util.List<java.lang.Integer> getTransationsForShop(int shopId)
        Gets all transactions for shop

        Parameters:
        shopId - buyer id

        Returns:
        list of transations ids, null if failure
     */
    @Override
    public List<Integer> getTransationsForShop(int shopId) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT Id FROM [dbo].[Transaction] WHERE ShopId = " + shopId + " ";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (!resultSet.next()) return null;
            List<Integer> ids = new ArrayList<>();
            ids.add(resultSet.getInt(1));
            while (resultSet.next()) {
                ids.add(resultSet.getInt(1));
            }
            return ids;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
        java.util.Calendar getTimeOfExecution​(int transactionId)
        get transaction's execution time. Execution time must be equal to order's recieve time.

        Parameters:
        transactionId - transaction's id

        Returns:
        time of execution, null if payment is not done or if failure
     */
    @Override
    public Calendar getTimeOfExecution(int transactionId) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT ExecutionTime FROM [dbo].[Transaction] WHERE Id = " + transactionId;
        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(query)) {
                Calendar calendar = Calendar.getInstance();
                calendar.clear();
                if (resultSet.next()) {
                    Date date = resultSet.getDate(1);
                    if (date == null) {
                        return null;
                    }
                    calendar.setTimeInMillis(date.getTime());
                    return calendar;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
        java.math.BigDecimal getAmmountThatBuyerPayedForOrder​(int orderId)
        Gets sum that buyer payed for an order

        Parameters:
        orderId - order's id

        Returns:
        ammount buyer payed for an order
     */
    @Override
    public BigDecimal getAmmountThatBuyerPayedForOrder(int orderId) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT Amount FROM [dbo].[Transaction] WHERE OrderId = " + orderId;
        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(query)) {
                if (resultSet.next()) {
                    return resultSet.getBigDecimal(1).setScale(3);
                } else {
                    return ZERO;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ZERO;
    }

    /*
        java.math.BigDecimal getAmmountThatShopRecievedForOrder​(int shopId, int orderId)
        Gets sum that shop recieved for an order

        Parameters:
        shopId - shop's id
        orderId - order's id

        Returns:
        ammount shop recieved for an order
     */
    @Override
    public BigDecimal getAmmountThatShopRecievedForOrder(int shopId, int orderId) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT SUM(Amount) FROM [dbo].[Transaction] WHERE Type = " + OrderHelper.TransactionType.SHOP.getType() + " AND ShopId = " +
                shopId + " AND OrderId = " + orderId;
        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(query)) {
                Calendar calendar = Calendar.getInstance();
                calendar.clear();
                if (resultSet.next()) {
                    return resultSet.getBigDecimal(1).setScale(3);
                } else {
                    return ZERO;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ZERO;
    }

    /*
        java.math.BigDecimal getTransactionAmount​(int transactionId)
        Gets transaction's amount.

        Parameters:
        transactionId - transaction's id

        Returns:
        ammount that is transferd via transaction
     */
    @Override
    public BigDecimal getTransactionAmount(int transactionId) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT Amount FROM [dbo].[Transaction] WHERE Id = " + transactionId;
        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(query)) {
                Calendar calendar = Calendar.getInstance();
                calendar.clear();
                if (resultSet.next()) {
                    return resultSet.getBigDecimal(1).setScale(3);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
        java.math.BigDecimal getSystemProfit()
        Gets system profit. System profit calculation is based only on arrived orders.

        Returns:
        system profit.
     */
    @Override
    public BigDecimal getSystemProfit() {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "{ ? = CALL dbo.fn_CalculateSystemProfit() }";
        try (CallableStatement callableStatement = connection.prepareCall(query);) {
            callableStatement.registerOutParameter(1, Types.DECIMAL);
            callableStatement.execute();
            BigDecimal systemProfit = callableStatement.getBigDecimal(1);
            if (systemProfit == null) {
                return ZERO;
            }
            return systemProfit.setScale(3);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ZERO;
    }
}
