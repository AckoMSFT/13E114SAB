package rs.etf.sab.student;

import rs.etf.sab.operations.BuyerOperations;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ia130010_BuyerOperations implements BuyerOperations {
    @Override
    public int createBuyer(String name, int cityId) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "INSERT INTO Buyer (Name, CityId) VALUES (?, ?)";
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

    @Override
    public int setCity(int buyerId, int cityId) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "UPDATE Buyer SET CityId = ? where Id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, cityId);
            preparedStatement.setInt(2, buyerId);
            int updated = preparedStatement.executeUpdate();
            if(updated != 1) {
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
        return 1;
    }

    @Override
    public int getCity(int buyerId) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT CityId from Buyer where Id = ?";
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

    @Override
    public BigDecimal increaseCredit(int buyerId, BigDecimal credit) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "UPDATE Buyer SET credit = credit + ? WHERE Id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setBigDecimal(1, credit);
            ps.setInt(2, buyerId);
            int updated = ps.executeUpdate();
            if (updated != 1) {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return getCredit(buyerId);
    }

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

    @Override
    public List<Integer> getOrders(int buyerId) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT Id FROM [dbo].[Order] WHERE BuyerId = " + buyerId + " ";
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
