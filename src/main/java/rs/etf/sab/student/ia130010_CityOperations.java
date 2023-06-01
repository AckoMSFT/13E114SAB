package rs.etf.sab.student;

import rs.etf.sab.operations.CityOperations;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ia130010_CityOperations implements CityOperations {
    @Override
    public int createCity(String name) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "INSERT INTO City (Name) VALUES (?) ";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, name);
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
    public List<Integer> getCities() {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT Id FROM [dbo].[City]";
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
    public int connectCities(int cityId1, int cityId2, int distance) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "INSERT INTO [dbo].[ConnectedCities] (CityId1, CityId2, Distance) VALUES (?, ?, ?)";
        int id = -1;
        try (PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, cityId1);
            preparedStatement.setInt(2, cityId2);
            preparedStatement.setInt(3, distance);
            preparedStatement.addBatch();
            preparedStatement.executeUpdate();
            try (ResultSet resultSet = preparedStatement.getGeneratedKeys() ){
                if (resultSet.next()) {
                    id = resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
        // TODO (acko): Make this a transaction, JDBC has an issue with using batched prepared statements where you can't get the generated keys
        // so basically do a batch insert and then query for the id...
        try (PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, cityId2);
            preparedStatement.setInt(2, cityId1);
            preparedStatement.setInt(3, distance);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }

        return id;
    }

    @Override
    public List<Integer> getConnectedCities(int cityId) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT CityId2 FROM [dbo].[ConnectedCities] WHERE CityId1 = " + cityId;
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
    public List<Integer> getShops(int cityId) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT Id FROM [dbo].[Shop] WHERE CityId = " + cityId;
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
}
