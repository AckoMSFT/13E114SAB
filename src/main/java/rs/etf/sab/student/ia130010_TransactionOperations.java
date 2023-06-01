package rs.etf.sab.student;

import rs.etf.sab.operations.TransactionOperations;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ia130010_TransactionOperations implements TransactionOperations {
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
                        return new BigDecimal(0).setScale(3);
                    }
                    return amount.setScale(3);
                } else {
                    return new BigDecimal(0).setScale(3);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new BigDecimal(0).setScale(3);
    }

    @Override
    public BigDecimal getShopTransactionsAmmount(int shopId) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT SUM(Amount) FROM [dbo].[Transaction] WHERE ShopId = " + shopId;
        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(query)) {
                if (resultSet.next()) {
                    BigDecimal amount = resultSet.getBigDecimal(1);
                    if (amount == null) {
                        return new BigDecimal(0).setScale(3);
                    }
                    return amount.setScale(3);
                } else {
                    return new BigDecimal(0).setScale(3);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new BigDecimal(0).setScale(3);
    }

    @Override
    public List<Integer> getTransationsForBuyer(int buyerId) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT Id FROM [dbo].[Transaction] WHERE BuyerId = " + buyerId + " ";
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

    @Override
    public int getTransactionForBuyersOrder(int orderId) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT Id FROM [dbo].[Transaction] WHERE OrderId = " + orderId + " AND Type = " + OrderHelper.TransactionType.BUYER.getType();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (!resultSet.next()) return -1;
            return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

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

    @Override
    public BigDecimal getAmmountThatBuyerPayedForOrder(int orderId) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT Amount FROM [dbo].[Transaction] WHERE OrderId = " + orderId;
        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(query)) {
                if (resultSet.next()) {
                    return resultSet.getBigDecimal(1).setScale(3);
                } else {
                    return new BigDecimal(0).setScale(3);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new BigDecimal(0).setScale(3);
    }

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
                    return new BigDecimal(0).setScale(3);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new BigDecimal(0).setScale(3);
    }

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

    @Override
    public BigDecimal getSystemProfit() {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "{ ? = CALL dbo.fn_CalculateSystemProfit() }";
        try (CallableStatement callableStatement = connection.prepareCall(query);) {
            callableStatement.registerOutParameter(1, Types.DECIMAL);
            callableStatement.execute();
            BigDecimal systemProfit = callableStatement.getBigDecimal(1);
            if (systemProfit == null) {
                return new BigDecimal(0).setScale(3);
            }
            return systemProfit.setScale(3);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new BigDecimal(0).setScale(3);
    }
}
