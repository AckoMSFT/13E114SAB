package rs.etf.sab.student;

import rs.etf.sab.operations.CityOperations;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ia130010_CityOperations implements CityOperations {

    /*
        int createCity(java.lang.String name)
        Creates new city.

        Parameters:
        name - the name of the city. Name of the cities must be unique.

        Returns:
        city id, or -1 on failure
     */
    @Override
    public int createCity(String name) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "INSERT INTO [dbo].[City] (Name) VALUES (?) ";
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

    /*
        java.util.List<java.lang.Integer> getCities()
        Gets all cities

        Returns:
        ids of cities, null if failure
     */
    @Override
    public List<Integer> getCities() {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT Id FROM [dbo].[City]";
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
        int connectCities(int cityId1, int cityId2, int distance)
        Connects two cities. There can be max one line between cities.

        Parameters:
        cityId1 - id of the first city
        cityId2 - id of the second city
        distance - distance between cities (distance is measured in days)

        Returns:
        line id, or -1 on failure
     */
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
        // is this level of detail even needed?
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

    /*
        java.util.List<java.lang.Integer> getConnectedCities(int cityId)
        Get connected cities.

        Parameters:
        cityId - id of the city that connections are asked for

        Returns:
        list of connected cities ids
     */
    @Override
    public List<Integer> getConnectedCities(int cityId) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT CityId2 FROM [dbo].[ConnectedCities] WHERE CityId1 = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, cityId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                ArrayList<Integer> ids = new ArrayList<>();
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
        java.util.List<java.lang.Integer> getShops(int cityId)
        Get shops in the city.

        Parameters:
        cityId - id of the city

        Returns:
        list of ids of shops, null if failure
     */
    @Override
    public List<Integer> getShops(int cityId) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT Id FROM [dbo].[Shop] WHERE CityId = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, cityId);
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
}
