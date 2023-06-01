package rs.etf.sab.student;

import rs.etf.sab.operations.GeneralOperations;
import rs.etf.sab.operations.OrderOperations;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class ia130010_OrderOperations implements OrderOperations {

    GeneralOperations generalOperations;

    public ia130010_OrderOperations(GeneralOperations generalOperations) {
        this.generalOperations = generalOperations;
    }

    @Override
    public int addArticle(int orderId, int articleId, int count) {
        // TODO (acko): Check if shops have capacity
        int shopArticleCount = getShopArticleCount(articleId);
        if (shopArticleCount < count) {
            return -1;
        }
        // Update shop article count
        // TODO (acko): Make this a batch
        updateShopArticleCount(articleId, -count);
        int itemId = getItemId(orderId, articleId);
        if (itemId != -1) {
            // Already exists an Item for this orderId and this articleId
            Connection connection = DBUtils.getInstance().getConnection();
            String query = "UPDATE [dbo].Item SET Count = Count + ? WHERE Id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, count);
                preparedStatement.setInt(2, itemId);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                return -1;
            }
        } else {
            // Need to create a new item
            Connection connection = DBUtils.getInstance().getConnection();
            String query = "INSERT INTO [dbo].[Item] (orderId, articleId, count) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setInt(1, orderId);
                preparedStatement.setInt(2, articleId);
                preparedStatement.setInt(3, count);
                preparedStatement.executeUpdate();
                try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        itemId = resultSet.getInt(1);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return -1;
            }
        }
        return itemId;
    }

    @Override
    public int removeArticle(int orderId, int articleId) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "DELETE FROM [dbo].[Item] WHERE OrderId = ? AND ArticleId = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, orderId);
            preparedStatement.setInt(2, articleId);
            int success = preparedStatement.executeUpdate();
            if (success != 1) {
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
        // TODO (acko): Make this a batch... Needs to be atomic.
        int orderArticleCount = getArticleCount(orderId, articleId);
        String increaseArticleCountQuery = "UPDATE [dbo].[Article] SET Count = Count + ? WHERE Id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(increaseArticleCountQuery)) {
            preparedStatement.setInt(1, orderArticleCount);
            preparedStatement.setInt(2, articleId);
            int success = preparedStatement.executeUpdate();
            if (success != 1) {
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
        return 1;
    }

    @Override
    public List<Integer> getItems(int orderId) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT Id FROM [dbo].[Item] WHERE OrderId = " + orderId;
        ArrayList<Integer> ids = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (!resultSet.next()) return null;
            ids.add(resultSet.getInt(1));
            while (resultSet.next()) {
                ids.add(resultSet.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return ids;
    }

    @Override
    public int completeOrder(int orderId) {
        int buyerId = getBuyer(orderId);
        if (buyerId == -1) {
            // Could not find buyer
            return -1;
        }
        int orderDestinationCity = OrderHelper.getOrderDestinationCity(buyerId);
        if (orderDestinationCity == -1) {
            // Could not find order destination city
            return -1;
        }
        // Find order assembly city
        Connection connection = DBUtils.getInstance().getConnection();
        String findOrderAssemblyCityQuery = "{ CALL SP_FIND_ORDER_ASSEMBLY_CITY (?, ?) }";
        try (CallableStatement callableStatementFindOrderAssemblyCity = connection.prepareCall(findOrderAssemblyCityQuery)) {
            callableStatementFindOrderAssemblyCity.setInt(1, orderId);
            callableStatementFindOrderAssemblyCity.setInt(2, orderDestinationCity);
            callableStatementFindOrderAssemblyCity.execute();
            findOrderAssemblyCityQuery = "SELECT AssemblyCityId FROM [dbo].[Order] WHERE Id = " + orderId;
            int orderAssemblyCity = -1;
            try (Statement statementFindOrderAssemblyCity = connection.createStatement();
                 ResultSet resultSetFindOrderAssemblyCity = statementFindOrderAssemblyCity.executeQuery(findOrderAssemblyCityQuery)) {
                if(resultSetFindOrderAssemblyCity.next()) {
                    orderAssemblyCity = resultSetFindOrderAssemblyCity.getInt(1);
                }
            }
            if (orderAssemblyCity == -1) {
                return -1;
            }
            System.out.println("orderAssemblyCity: " + orderAssemblyCity);
            System.out.println("orderDestinationCity: " + orderDestinationCity);
            // Update order state to "sent"
            String updateOrderStateQuery = "UPDATE [dbo].[Order] SET State = '" + OrderHelper.OrderState.SENT.getState() + "' WHERE Id = " + orderId;
            try (Statement statementUpdateOrderState = connection.createStatement()) {
                statementUpdateOrderState.execute(updateOrderStateQuery);
            }
            String findItemOriginCitiesQuery =
                    "SELECT DISTINCT(Shop.CityId) FROM [dbo].[Shop] Shop, [dbo].[Article] Article, [dbo].[Item] Item, [dbo].[Order] [Order]\n" +
                    "WHERE Shop.Id = Article.ShopId AND Item.ArticleId = Article.Id AND [Order].Id = Item.OrderId\n" +
                    "AND [Order].Id = " + orderId;
            try (
                    Statement statementFindItemOriginCities = connection.createStatement();
                    ResultSet resultSetFindItemOriginCities = statementFindItemOriginCities.executeQuery(findItemOriginCitiesQuery)) {
                while (resultSetFindItemOriginCities.next()) {
                    int itemOriginCity = resultSetFindItemOriginCities.getInt(1);
                    System.out.println("itemOriginCity: " + itemOriginCity);
                    String findShortestPathQuery = "{ CALL SP_FIND_SHORTEST_PATH (?, ?, ?, ?) }";
                    try(CallableStatement callableStatementFindShortestPath = connection.prepareCall(findShortestPathQuery)) {
                        callableStatementFindShortestPath.setInt(1, itemOriginCity);
                        callableStatementFindShortestPath.setInt(2, orderAssemblyCity);
                        callableStatementFindShortestPath.setInt(3, orderId);
                        callableStatementFindShortestPath.setBoolean(4, true);
                        callableStatementFindShortestPath.execute();
                    }
                }
            }
            String findShortestPathOrder = "{ CALL SP_FIND_SHORTEST_PATH (?, ?, ?, ?) }";
            try (CallableStatement callableStatementFindShortestPathOrder = connection.prepareCall(findShortestPathOrder)) {
                callableStatementFindShortestPathOrder.setInt(1, orderAssemblyCity);
                callableStatementFindShortestPathOrder.setInt(2, orderDestinationCity);
                callableStatementFindShortestPathOrder.setInt(3, orderId);
                callableStatementFindShortestPathOrder.setBoolean(4, false);
                callableStatementFindShortestPathOrder.execute();
            }
            String updateOrderSentTimeQuery = "UPDATE [dbo].[Order] SET SentTime = ? WHERE Id = ?";
            Timestamp currentTime = new Timestamp(this.generalOperations.getCurrentTime().getTimeInMillis());
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateOrderSentTimeQuery)) {
                preparedStatement.setTimestamp(1, currentTime);
                preparedStatement.setInt(2, orderId);
                preparedStatement.executeUpdate();
                // TODO (acko): Rollback?
            }
            // Find all items and create record of their transit
            String findItemsQuery = "SELECT Id, Path FROM [dbo].[Item] WHERE OrderId = " + orderId;
            try (Statement statement = connection.createStatement()) {
                try (ResultSet resultSet = statement.executeQuery(findItemsQuery)) {
                    while (resultSet.next()) {
                        int itemId = resultSet.getInt(1);
                        String path = resultSet.getString(2);
                        System.out.println("itemId: " + itemId);
                        System.out.println("path: " + path);
                        List<Integer> pathElements = new LinkedList<>();
                        for (String element: path.split(",")) {
                            pathElements.add(Integer.valueOf(element));
                        }
                        if (pathElements.size() == 1) {
                            // No need to put this item in transit
                            continue;
                        }
                        int origin = pathElements.get(0);
                        int destination = pathElements.get(1);
                        int currentEdge = -1, currentDistance = -1;
                        String findCurrentEdge = "SELECT Id, Distance FROM [dbo].[ConnectedCities] WHERE CityId1 = " + origin + " AND CityId2 = " + destination;
                        try (Statement statementFindCurrentEdge = connection.createStatement()) {
                            try (ResultSet resultSetFindCurrentEdge = statementFindCurrentEdge.executeQuery(findCurrentEdge)) {
                                if (resultSetFindCurrentEdge.next()) {
                                    currentEdge = resultSetFindCurrentEdge.getInt(1);
                                    currentDistance = resultSetFindCurrentEdge.getInt(2);
                                }
                            }
                        }
                        System.out.println("currentEdge: " + currentEdge);
                        System.out.println("currentDistance: " + currentDistance);
                        String insertTransitQuery = "INSERT INTO Transit (OrderId, ItemId, EdgeId, DaysLeft, Type) VALUES (?, ?, ?, ?, ?)";
                        try (PreparedStatement preparedStatement = connection.prepareStatement(insertTransitQuery)) {
                            preparedStatement.setInt(1, orderId);
                            preparedStatement.setInt(2, itemId);
                            preparedStatement.setInt(3, currentEdge);
                            preparedStatement.setInt(4, currentDistance);
                            preparedStatement.setInt(5, OrderHelper.TransitType.ITEM.getType());
                            preparedStatement.executeUpdate();
                        }
                    }
                }
            }
            BigDecimal finalPrice = getFinalPrice(orderId);
            System.out.println("Final price: " + finalPrice);
            String insertBuyerTransactionQuery = "INSERT INTO [dbo].[Transaction] (BuyerId, ExecutionTime, Amount, OrderId, Type) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertBuyerTransactionQuery)) {
                preparedStatement.setInt(1, buyerId);
                preparedStatement.setTimestamp(2, currentTime);
                preparedStatement.setBigDecimal(3, finalPrice);
                preparedStatement.setInt(4, orderId);
                preparedStatement.setInt(5, OrderHelper.TransactionType.BUYER.getType());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
        return 1;
    }

    @Override
    public BigDecimal getFinalPrice(int orderId) {
        String state = getState(orderId);
        if (!OrderHelper.isOrderCompleted(state)) {
            return new BigDecimal(-1).setScale(3);
        }
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "{ CALL SP_FINAL_PRICE (?, ?) }";
        try (CallableStatement callableStatement = connection.prepareCall(query)) {
            callableStatement.setInt(1, orderId);
            callableStatement.registerOutParameter(2, Types.DECIMAL);
            callableStatement.execute();
            return callableStatement.getBigDecimal(2).setScale(3);
        } catch (SQLException e) {
            e.printStackTrace();
            return new BigDecimal(-1).setScale(3);
        }
    }

    @Override
    public BigDecimal getDiscountSum(int orderId) {
        String state = getState(orderId);
        if (!OrderHelper.isOrderCompleted(state)) {
            return new BigDecimal(-1).setScale(3);
        }
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "{ ? = CALL dbo.fn_CalculateDiscount(?) }";
        try (CallableStatement callableStatement = connection.prepareCall(query);) {
            callableStatement.setInt(2, orderId);
            callableStatement.registerOutParameter(1, Types.DECIMAL);
            callableStatement.execute();
            return callableStatement.getBigDecimal(1).setScale(3);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new BigDecimal(-1).setScale(3);
    }

    @Override
    public String getState(int orderId) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT State FROM [dbo].[Order] WHERE Id = " + orderId;
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
            else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Calendar getSentTime(int orderId) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT SentTime FROM [dbo].[Order] WHERE Id = " + orderId;
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                Calendar calendar = Calendar.getInstance();
                calendar.clear();
                Timestamp timestamp = resultSet.getTimestamp(1);
                if (timestamp == null) {
                    return null;
                }
                calendar.setTimeInMillis(timestamp.getTime());
                return calendar;
            }
            else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Calendar getRecievedTime(int orderId) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT ReceivedTime FROM [dbo].[Order] WHERE Id = " + orderId;
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                Calendar calendar = Calendar.getInstance();
                calendar.clear();
                Timestamp timestamp = resultSet.getTimestamp(1);
                if (timestamp == null) {
                    return null;
                }
                calendar.setTimeInMillis(timestamp.getTime());
                return calendar;
            }
            else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int getBuyer(int orderId) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT BuyerId FROM [dbo].[Order] WHERE Id = " + orderId;
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            else {
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public int getLocation(int orderId) {
        String state = getState(orderId);
        if (state.equals(OrderHelper.OrderState.CREATED.getState())) {
            return -1;
        }
        if (state.equals(OrderHelper.OrderState.ARRIVED.getState())) {
            int buyer = getBuyer(orderId);
            int buyerCity = OrderHelper.getBuyerCity(buyer);
            return buyerCity;
        }
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT ConnectedCities.CityId1 FROM Transit Transit, ConnectedCities ConnectedCities WHERE Transit.OrderId = " + orderId
                + " AND Transit.Type = " + OrderHelper.TransitType.ORDER.getType() + " AND Transit.EdgeId = ConnectedCities.Id";
        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(query)) {
                if (resultSet.next()) {
                    // Order is in transit
                    return resultSet.getInt(1);
                } else {
                    // Order is still being assembled
                    return getOrderAssemblyCity(orderId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void findShortestPath(int orderId, int orderAssemblyCity, int orderDestinationCity) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "{ call SP_FIND_SHORTEST_PATH (?, ?, ?, ?) }";
        try(CallableStatement callableStatement = connection.prepareCall(query)) {
            callableStatement.setInt(1, orderAssemblyCity);
            callableStatement.setInt(2, orderDestinationCity);
            callableStatement.setInt(3, orderId);
            callableStatement.setBoolean(4, false);
            callableStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Integer> getPath(int orderId) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT Path FROM [dbo].[Order] WHERE Id = " + orderId;
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                String pathCSV = resultSet.getString(1);
                List<Integer> path = new LinkedList<>();
                for (String pathElement: pathCSV.split(",")) {
                    path.add(Integer.valueOf(pathElement));
                }
                return path;
            }
            else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int getArticleCount(int orderId, int articleId) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT Count FROM [dbo].[Item] WHERE OrderId = " + orderId + " AND ArticleId = " + articleId;
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            else {
                return 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int getItemId(int orderId, int articleId) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT Id FROM [dbo].[Item] WHERE OrderId = " + orderId + " AND ArticleId = " + articleId;
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            else {
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int getShopArticleCount(int articleId) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT Count FROM [dbo].[Article] WHERE Id = " + articleId;
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            else {
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int updateShopArticleCount(int articleId, int increment) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "UPDATE [dbo].[Article] SET Count = Count + ? WHERE Id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, increment);
            preparedStatement.setInt(2, articleId);
            int success = preparedStatement.executeUpdate();
            return success;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int getOrderAssemblyCity(int orderId) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT AssemblyCityId FROM [dbo].[Order] WHERE Id = " + orderId;
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            else {
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
