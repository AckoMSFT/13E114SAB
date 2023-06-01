package rs.etf.sab.student;

import rs.etf.sab.operations.ShopOperations;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ia130010_ShopOperations implements ShopOperations {

    private int getCityId(String cityName) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT Id FROM City WHERE Name = '" + cityName + "'";
        try (Statement statement = connection.createStatement()) {
             ResultSet resultSet = statement.executeQuery(query);
            if(resultSet.next()) {
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
    public int createShop(String name, String cityName) {
        int cityId = getCityId(cityName);
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "INSERT INTO [dbo].[Shop] (Name, CityId) VALUES (?, ?)";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, cityName);
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
        return 1;
    }

    @Override
    public int setCity(int shopId, String cityName) {
        int cityId = getCityId(cityName);
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "UPDATE [dbo].[Shop] SET CityId = ? WHERE Id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, cityId);
            ps.setInt(2, shopId);
            int updated = ps.executeUpdate();
            if (updated != 1) {
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
        return 1;
    }

    @Override
    public int getCity(int shopId) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT CityId FROM [dbo].[Shop] WHERE Id = '" + shopId + "'";
        try (Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query)) {
            if(resultSet.next()) {
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
    public int setDiscount(int shopId, int discountPercentage) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "UPDATE [dbo].[Shop] SET Discount = ? WHERE Id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, discountPercentage);
            ps.setInt(2, shopId);
            int updated = ps.executeUpdate();
            if (updated != 1) {
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
        return 1;
    }

    @Override
    public int increaseArticleCount(int articleId, int increment) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "UPDATE [dbo].[Article] SET Count = Count + ? WHERE Id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, increment);
            ps.setInt(2, articleId);
            int updated = ps.executeUpdate();
            if (updated != 1) {
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
        return getArticleCount(articleId);
    }

    @Override
    public int getArticleCount(int articleId) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT Count FROM Article WHERE Id = " + articleId;
        try (Statement statement = connection.createStatement();
             ResultSet count = statement.executeQuery(query)) {
            if (count.next()) {
                return count.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
        return -1;
    }

    @Override
    public List<Integer> getArticles(int shopId) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT Id FROM [dbo].[Article] WHERE ShopId = " + shopId;
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
    public int getDiscount(int shopId) {
        Connection connection = DBUtils.getInstance().getConnection();
        String query = "SELECT Discount FROM [dbo].[Shop] WHERE Id = '" + shopId + "'";
        ResultSet resultSet = null;
        try (Statement statement = connection.createStatement()) {
            resultSet = statement.executeQuery(query);
            if(resultSet.next()) {
                return resultSet.getInt(1);
            }
            else {
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
        finally {
            if (resultSet != null) try {
                resultSet.close();
            } catch (SQLException ignored) {
            }
        }
    }
}
