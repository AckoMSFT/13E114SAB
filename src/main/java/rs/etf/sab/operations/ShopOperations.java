package rs.etf.sab.operations;

import java.util.List;

public interface ShopOperations {
    int createShop(String name, String cityName);

    int setCity(int shopId, String cityName);

    int getCity(int shopId);

    int setDiscount(int shopId, int discountPercentage);

    int increaseArticleCount(int articleId, int increment);

    int getArticleCount(int articleId);

    List<Integer> getArticles(int shopId);

    int getDiscount(int shopId);
}
