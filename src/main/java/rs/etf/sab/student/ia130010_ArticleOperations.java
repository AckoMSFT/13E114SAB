package rs.etf.sab.student;

import rs.etf.sab.operations.ArticleOperations;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ia130010_ArticleOperations implements ArticleOperations {
    @Override
    public int createArticle(int shopId, String articleName, int articlePrice) {
        Connection connection = DBUtils.getInstance().getConnection();
        String insertQuery = "INSERT INTO [dbo].[Article] (Name, Price, ShopId) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
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
