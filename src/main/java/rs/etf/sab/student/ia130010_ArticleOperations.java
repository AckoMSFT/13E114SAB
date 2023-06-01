package rs.etf.sab.student;

import rs.etf.sab.operations.ArticleOperations;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ia130010_ArticleOperations implements ArticleOperations {

    /*
        int createArticle(int shopId, java.lang.String articleName, int articlePrice)
        Creates new article in shop with count 0.

        Parameters:
        shopId - shop id
        articleName - article name
        articlePrice - price of the article

        Returns:
        id of the article, -1 in failure
     */
    @Override
    public int createArticle(int shopId, String articleName, int articlePrice) {
        Connection connection = DBUtils.getInstance().getConnection();
        // Count is implicitly set to 0 by using default value for the column in the database model
        String query = "INSERT INTO [dbo].[Article] (Name, Price, ShopId) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, articleName);
            preparedStatement.setBigDecimal(2, new BigDecimal(articlePrice));
            preparedStatement.setInt(3, shopId);
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
}
