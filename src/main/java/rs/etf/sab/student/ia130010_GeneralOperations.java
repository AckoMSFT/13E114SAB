package rs.etf.sab.student;

import rs.etf.sab.operations.GeneralOperations;

import java.sql.*;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class ia130010_GeneralOperations implements GeneralOperations {

    Calendar currentTime;

    public ia130010_GeneralOperations() {
        currentTime = null;
    }

    /*
        void setInitialTime(java.util.Calendar time)
        Sets initial time

        Parameters:
        time - time
     */
    @Override
    public void setInitialTime(Calendar time) {
        currentTime = Calendar.getInstance();
        currentTime.clear();
        currentTime.setTimeInMillis(time.getTimeInMillis());
    }

    /*
        java.util.Calendar time(int days)
        Time to pass in simulation.

        Parameters:
        days - number of days that will pass in simulation after this method call

        Returns:
        current time
     */
    @Override
    public Calendar time(int days) {
        currentTime.add(Calendar.DAY_OF_MONTH, days);
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT Id, OrderId, ItemId, EdgeId, DaysLeft, Type FROM Transit";
        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(query)) {
                connection.setAutoCommit(false);
                while (resultSet.next()) {
                    int transitId = resultSet.getInt(1);
                    int orderId = resultSet.getInt(2);
                    int itemId = resultSet.getInt(3);
                    int edgeId = resultSet.getInt(4);
                    int daysLeftInTransit = resultSet.getInt(5);
                    int type = resultSet.getInt(6);
                    System.out.println("transitId: " + transitId);
                    System.out.println("orderId: " + orderId);
                    System.out.println("itemId: " + itemId);
                    System.out.println("edgeId: " + edgeId);
                    System.out.println("daysLeftInTransit: " + daysLeftInTransit);
                    System.out.println("type: " + type);
                    if (daysLeftInTransit == 0) {
                        continue;
                    }
                    boolean isOrder = type == OrderHelper.TransitType.ORDER.getType();
                    String path = null;
                    String pathQuery = null;
                    if (isOrder) {
                        pathQuery = "SELECT Path From [dbo].[Order] WHERE Id = " + orderId;
                    } else {
                        pathQuery = "SELECT Path From [dbo].[Item] WHERE Id = " + itemId;
                    }
                    try (Statement pathStatement = connection.createStatement()) {
                        try (ResultSet resultSetPath = pathStatement.executeQuery(pathQuery)) {
                            if (resultSetPath.next()) {
                                path = resultSetPath.getString(1);
                            }
                        }
                    }
                    System.out.println("path: " + path);
                    List<Integer> pathElements = new LinkedList<>();
                    for (String pathElement: path.split(",")) {
                        pathElements.add(Integer.valueOf(pathElement));
                    }
                    int daysLeftInSimulation = days;
                    while (daysLeftInSimulation > 0) {
                        System.out.println("daysLeftInTransit: " + daysLeftInTransit);
                        System.out.println("daysLeftInSimulation: " + daysLeftInSimulation);
                        if (daysLeftInTransit > daysLeftInSimulation) {
                            String updateDaysLeftQuery = "UPDATE [dbo].[Transit] SET DaysLeft = DaysLeft - " + daysLeftInSimulation + " WHERE Id = " + transitId;
                            try (Statement statementUpdateDaysLeft = connection.createStatement()) {
                                statementUpdateDaysLeft.executeUpdate(updateDaysLeftQuery);
                            }
                            break;
                        }
                        daysLeftInSimulation -= daysLeftInTransit;
                        // We need to change the location of the item or order in transit
                        int currentCity = -1;
                        String getCurrentCityQuery = "SELECT CityId2 FROM [dbo].[ConnectedCities] WHERE id = " + edgeId;
                        try (Statement getCurrentCityStatement = connection.createStatement()) {
                            try (ResultSet resultSetGetCurrentCity = getCurrentCityStatement.executeQuery(getCurrentCityQuery)) {
                                if (resultSetGetCurrentCity.next()) {
                                    currentCity = resultSetGetCurrentCity.getInt(1);
                                }
                            }
                        }
                        System.out.println("currentCity: " + currentCity);
                        int idx = 0;
                        while (idx < pathElements.size()) {
                            if (pathElements.get(idx) == currentCity) {
                                break;
                            }
                            idx++;
                        }
                        System.out.println("idx: " + idx);
                        if (idx + 1 != pathElements.size()) {
                            System.out.println("Need to update the edge");
                            int nextCity = pathElements.get(idx + 1);
                            int nextEdge = -1, nextDistance = -1;
                            String getNextEdge = "SELECT Id, Distance FROM [dbo].[ConnectedCities] WHERE CityId1 = " + currentCity + " AND CityId2 = " + nextCity;
                            try (Statement getNextEdgeStatement = connection.createStatement()) {
                                try (ResultSet resultSetGetNextEdge = getNextEdgeStatement.executeQuery(getNextEdge)) {
                                    if (resultSetGetNextEdge.next()) {
                                        nextEdge = resultSetGetNextEdge.getInt(1);
                                        nextDistance = resultSetGetNextEdge.getInt(2);
                                    }
                                }
                            }
                            System.out.println("nextCity: " + nextCity + " nextDistance: " + nextDistance);
                            daysLeftInTransit = nextDistance;
                            edgeId = nextEdge;
                            String updateTransitQuery = "UPDATE [dbo].[Transit] SET EdgeId = " + nextEdge + ", DaysLeft = " + nextDistance + " WHERE Id = " + transitId;
                            try (Statement updateTransitStatement = connection.createStatement()) {
                                updateTransitStatement.executeUpdate(updateTransitQuery);
                            }
                        } else {
                            // We have reached the destination for this transit
                            System.out.println("We have reached the destination for transit: " + transitId);
                            if (!isOrder) {
                                // Item
                                String updateTransitQuery = "UPDATE [dbo].[Transit] SET DaysLeft = 0 WHERE Id = " + transitId;
                                try (Statement updateTransitStatement = connection.createStatement()) {
                                    updateTransitStatement.executeUpdate(updateTransitQuery);
                                }
                                // Has the order been assembled?
                                String hasTheOrderBeenAssembledQuery = "SELECT TOP 1 Id FROM Transit WHERE OrderId = " + orderId
                                        + " AND DaysLeft > 0 AND Type = " + OrderHelper.TransitType.ITEM.getType();
                                boolean hasTheOrderBeenAssembled = true;
                                try (Statement hasTheOrderBeenAssembledStatement = connection.createStatement()) {
                                    try (ResultSet hasTheOrderBeenAssembledResultSet = hasTheOrderBeenAssembledStatement.executeQuery(hasTheOrderBeenAssembledQuery)) {
                                        if (hasTheOrderBeenAssembledResultSet.next()) {
                                            hasTheOrderBeenAssembled = false;
                                        }
                                    }
                                }
                                System.out.println("hasTheOrderBeenAssembled: " + hasTheOrderBeenAssembled);
                                if (!hasTheOrderBeenAssembled) {
                                    break;
                                }
                                // We need to remove the items from transit and put the order in transit
                                String removeItemsFromTransitQuery = "DELETE FROM [dbo].[Transit] WHERE OrderId = " + orderId + " AND Type = " + OrderHelper.TransitType.ITEM.getType();
                                try (Statement removeItemsFromTransitStatement = connection.createStatement()) {
                                    removeItemsFromTransitStatement.executeUpdate(removeItemsFromTransitQuery);
                                }
                                String orderPath = null;
                                String getOrderPathQuery = "SELECT Path FROM [dbo].[Order] WHERE Id = " + orderId;
                                try (Statement getOrderPathStatement = connection.createStatement()) {
                                    try (ResultSet getOrderPathResultSet = getOrderPathStatement.executeQuery(getOrderPathQuery)) {
                                        if (getOrderPathResultSet.next()) {
                                            orderPath = getOrderPathResultSet.getString(1);
                                        }
                                    }
                                }
                                System.out.println("orderPath: " + orderPath);
                                List<Integer> orderPathElements = new LinkedList<>();
                                for (String pathElement: orderPath.split(",")) {
                                    orderPathElements.add(Integer.valueOf(pathElement));
                                }
                                if (orderPathElements.size() < 2) {
                                    // Buyer city is the same as assembly city so no need for the order to go into transit
                                    System.out.println("Buyer city is the same as assembly city");
                                    String updateOrderReceivedTimeQuery = "UPDATE [dbo].[Order] SET ReceivedTime = ?, [State] = ? WHERE Id = ?";
                                    try (PreparedStatement preparedStatement = connection.prepareStatement(updateOrderReceivedTimeQuery)) {
                                        preparedStatement.setTimestamp(1, new Timestamp(currentTime.getTimeInMillis()));
                                        preparedStatement.setString(2, OrderHelper.OrderState.ARRIVED.getState());
                                        preparedStatement.setInt(3, orderId);
                                        preparedStatement.executeUpdate();
                                    }
                                    break;
                                } else {
                                    System.out.println("Need to put the order into transit");
                                    int origin = orderPathElements.get(0);
                                    int destination = orderPathElements.get(1);
                                    int currentEdge = -1, currentDistance = -1;
                                    String findCurrentEdge = "SELECT Id, Distance FROM [dbo].[ConnectedCities] WHERE CityId1 = " + origin + " AND CityId2 = " + destination;
                                    try (Statement statementFindCurrentEdge = connection.createStatement()) {
                                        try (ResultSet resultSetFindCurrentEdge = statementFindCurrentEdge.executeQuery(findCurrentEdge)) {
                                            if (resultSetFindCurrentEdge.next()) {
                                                currentEdge = resultSetFindCurrentEdge.getInt(1);
                                                currentDistance = resultSetFindCurrentEdge.getInt(2);
                                            } else {
                                                break;
                                            }
                                        }
                                    }
                                    System.out.println("currentEdge: " + currentEdge);
                                    System.out.println("currentDistance: " + currentDistance);
                                    String insertTransitQuery = "INSERT INTO Transit (OrderId, EdgeId, DaysLeft, Type) VALUES (?, ?, ?, ?)";
                                    try (PreparedStatement preparedStatement = connection.prepareStatement(insertTransitQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
                                        preparedStatement.setInt(1, orderId);
                                        preparedStatement.setInt(2, currentEdge);
                                        preparedStatement.setInt(3, currentDistance);
                                        preparedStatement.setInt(4, OrderHelper.TransitType.ORDER.getType());
                                        preparedStatement.executeUpdate();
                                        try (ResultSet resultSetInsertTransit = preparedStatement.getGeneratedKeys()) {
                                            if (resultSetInsertTransit.next()) {
                                                transitId = resultSetInsertTransit.getInt(1);
                                                isOrder = true;
                                                path = orderPath;
                                                pathElements.clear();
                                                for (Integer orderPathElement: orderPathElements) {
                                                    pathElements.add(orderPathElement);
                                                }
                                                edgeId = currentEdge;
                                                daysLeftInTransit = currentDistance;
                                            }
                                        }
                                    }
                                }
                            } else {
                                // Order
                                Calendar receivedTime = Calendar.getInstance();
                                receivedTime.clear();
                                receivedTime.setTimeInMillis(currentTime.getTimeInMillis());
                                receivedTime.add(Calendar.DATE, -daysLeftInSimulation);
                                String updateOrderReceivedTimeQuery = "UPDATE [dbo].[Order] SET ReceivedTime = ?, [State] = ? WHERE Id = ?";
                                try (PreparedStatement preparedStatement = connection.prepareStatement(updateOrderReceivedTimeQuery)) {
                                    preparedStatement.setTimestamp(1, new Timestamp(receivedTime.getTimeInMillis()));
                                    preparedStatement.setString(2, OrderHelper.OrderState.ARRIVED.getState());
                                    preparedStatement.setInt(3, orderId);
                                    preparedStatement.executeUpdate();
                                }
                                String removeOrderFromTransitQuery = "DELETE FROM Transit WHERE Id = " + transitId;
                                try (Statement removeOrderFromTransitStatement = connection.createStatement()) {
                                    removeOrderFromTransitStatement.executeUpdate(removeOrderFromTransitQuery);
                                }
                                break;
                            }
                        }
                    }
                }
            }
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return currentTime;
    }

    /*
        java.util.Calendar getCurrentTime()
        Gets current time

        Returns:
        current time
     */
    @Override
    public Calendar getCurrentTime() {
        return currentTime;
    }

    /*
        void eraseAll()
        Clears data in database.
     */
    @Override
    public void eraseAll() {
        String disableAllConstraints = "EXEC sp_MSForEachTable 'ALTER TABLE ? NOCHECK CONSTRAINT ALL'";
        String disableAllTriggers = "EXEC sp_MSForEachTable 'ALTER TABLE ? DISABLE TRIGGER ALL'";
        String deleteAllFromTables = "EXEC sp_MSForEachTable 'DELETE FROM ?'";
        String enableAllTriggers = "EXEC sp_MSForEachTable 'ALTER TABLE ? ENABLE TRIGGER ALL'";
        String enableAllConstraints = "EXEC sp_MSForEachTable 'ALTER TABLE ? WITH CHECK CHECK CONSTRAINT ALL'";
        String reseedAllIdentities = "EXEC sp_MSForEachTable \"DBCC CHECKIDENT ( '?', RESEED, 0)\"";

        Connection connection = DBUtils.getInstance().getConnection();
        try (Statement statement = connection.createStatement()) {
            connection.setAutoCommit(false);
            statement.addBatch(disableAllConstraints);
            statement.addBatch(disableAllTriggers);
            statement.addBatch(deleteAllFromTables);
            statement.addBatch(enableAllTriggers);
            statement.addBatch(enableAllConstraints);
            statement.addBatch(reseedAllIdentities);
            statement.executeBatch();
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
