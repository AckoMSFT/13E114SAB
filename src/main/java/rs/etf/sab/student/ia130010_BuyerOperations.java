package rs.etf.sab.student;

import rs.etf.sab.operations.BuyerOperations;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ia130010_BuyerOperations implements BuyerOperations {

    /*
        int createBuyer(java.lang.String name, int cityId)
        Creates new buyer with 0 credit.

        Parameters:
        name - name of the buyer
        cityId - id of the city

        Returns:
        buyer's id, or -1 if failure
     */
    @Override
    public int createBuyer(String name, int cityId) {
        Connection connection = DBUtils.getInstance().getConnection();
        // Credit is implicitly set to 0 by using default value for the column in the database model
        String query = "INSERT INTO [dbo].[Buyer] (Name, CityId) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, cityId);
            preparedStatement.executeUpdate();
            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
        return -1;
    }

    /*
        int setCity(int buyerId, int cityId)
        Changes city for buyer.

        Parameters:
        buyerId - id of the buyer
        cityId - id of the city

        Returns:
        1 if success, -1 if failure
     */
    @Override
    public int setCity(int buyerId, int cityId) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "UPDATE [dbo].[Buyer] SET CityId = ? where Id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, cityId);
            preparedStatement.setInt(2, buyerId);
            int rowCount = preparedStatement.executeUpdate();
            if (rowCount != 1) {
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
        return 1;
    }

    /*
        int getCity(int buyerId)
        Gets city for buyer.

        Parameters:
        buyerId - buyer's id

        Returns:
        city's id, -1 if failure
     */
    @Override
    public int getCity(int buyerId) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT CityId from [dbo].[Buyer] where Id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, buyerId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
        return -1;
    }

    /*
        java.math.BigDecimal increaseCredit(int buyerId, java.math.BigDecimal credit)
        Increases buyer's credit.

        Parameters:
        buyerId - id of the buyer
        credit - credit

        Returns:
        credit after addition
     */
    @Override
    public BigDecimal increaseCredit(int buyerId, BigDecimal credit) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "UPDATE Buyer SET credit = credit + ? WHERE Id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setBigDecimal(1, credit);
            preparedStatement.setInt(2, buyerId);
            int rowCount = preparedStatement.executeUpdate();
            if (rowCount != 1) {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return getCredit(buyerId);
    }

    /*
        int createOrder(int buyerId)
        Creates empty order.

        Parameters:
        buyerId - buyer id

        Returns:
        id of the order, -1 in failure
     */
    @Override
    public int createOrder(int buyerId) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "INSERT INTO [dbo].[Order] (BuyerId, State) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, buyerId);
            preparedStatement.setString(2, OrderHelper.OrderState.CREATED.getState());
            preparedStatement.executeUpdate();
            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
        return -1;
    }

    /*
        java.util.List<java.lang.Integer> getOrders(int buyerId)
        Gets all orders for buyer

        Parameters:
        buyerId - buyer id

        Returns:
        list of order's ids for buyer
     */
    @Override
    public List<Integer> getOrders(int buyerId) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT Id FROM [dbo].[Order] WHERE BuyerId = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, buyerId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<Integer> ids = new ArrayList<>();
                while (resultSet.next()) {
                    ids.add(resultSet.getInt(1));
                }
                return ids;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
        java.math.BigDecimal getCredit(int buyerId)
        Gets credit for buyer.

        Parameters:
        buyerId - buyer's id

        Returns:
        credit for buyer
     */
    @Override
    public BigDecimal getCredit(int buyerId) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT Credit FROM Buyer WHERE Id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, buyerId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBigDecimal(1).setScale(3);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
}
