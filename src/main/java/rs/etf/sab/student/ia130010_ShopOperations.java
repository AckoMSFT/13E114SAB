package rs.etf.sab.student;

import rs.etf.sab.operations.ShopOperations;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ia130010_ShopOperations implements ShopOperations {

    /*
        Helper class to get the city id from the city name.
        Returns -1 on failure.
     */
    private int getCityId(String cityName) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT Id FROM [dbo].[City] WHERE Name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, cityName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                } else {
                    return -1;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /*
        int createShop(java.lang.String name, java.lang.String cityName)
        Creates new shop with 0% discount. Shops must have unique name.

        Parameters:
        name - name of the shop
        cityName - name of the city

        Returns:
        id of the shop, or -1 in failure
     */
    @Override
    public int createShop(String name, String cityName) {
        int cityId = getCityId(cityName);
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "INSERT INTO [dbo].[Shop] (Name, CityId) VALUES (?, ?)";
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
        int setCity(int shopId, java.lang.String cityName)
        Changes city for shop.

        Parameters:
        shopId - id of the shop
        cityName - name of the city

        Returns:
        1 on success, -1 on failure
     */
    @Override
    public int setCity(int shopId, String cityName) {
        int cityId = getCityId(cityName);
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "UPDATE [dbo].[Shop] SET CityId = ? WHERE Id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, cityId);
            ps.setInt(2, shopId);
            int rowCount = ps.executeUpdate();
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
        int getCity(int shopId)
        Gets city's id

        Parameters:
        shopId - city for shop

        Returns:
        city's id
     */
    @Override
    public int getCity(int shopId) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT CityId FROM [dbo].[Shop] WHERE Id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, shopId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                } else {
                    return -1;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /*
        int setDiscount(int shopId, int discountPercentage)
        Sets discount for shop.

        Parameters:
        shopId - id of the shop
        discountPercentage - discount in percentage

        Returns:
        1 on success, -1 on failure
     */
    @Override
    public int setDiscount(int shopId, int discountPercentage) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "UPDATE [dbo].[Shop] SET Discount = ? WHERE Id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, discountPercentage);
            preparedStatement.setInt(2, shopId);
            int updated = preparedStatement.executeUpdate();
            if (updated != 1) {
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
        return 1;
    }

    /*
        int increaseArticleCount(int articleId, int increment)
        Increases number of articles in the shop.

        Parameters:
        articleId - id of the article
        increment - number of articles to be stored in shop

        Returns:
        number of articles after storing, -1 in failure
     */
    @Override
    public int increaseArticleCount(int articleId, int increment) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "UPDATE [dbo].[Article] SET Count = Count + ? WHERE Id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, increment);
            preparedStatement.setInt(2, articleId);
            int rowCount = preparedStatement.executeUpdate();
            if (rowCount != 1) {
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
        return getArticleCount(articleId);
    }

    /*
        int getArticleCount​(int articleId)
        Gets count of articles in shop.

        Parameters:
        articleId - id of the article

        Returns:
        number of articles in shop
     */
    @Override
    public int getArticleCount(int articleId) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT Count FROM Article WHERE Id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, articleId);
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
        java.util.List<java.lang.Integer> getArticles(int shopId)
        Gets all articles.

        Parameters:
        shopId - shop's id

        Returns:
        gets all article's ids in shop
     */
    @Override
    public List<Integer> getArticles(int shopId) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT Id FROM [dbo].[Article] WHERE ShopId = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, shopId);
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
        int getDiscount(int shopId)
        Get discount for shop.

        Parameters:
        shopId - shop's id

        Returns:
        discount percentage
     */
    @Override
    public int getDiscount(int shopId) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT Discount FROM [dbo].[Shop] WHERE Id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, shopId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                } else {
                    return -1;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
