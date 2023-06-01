package rs.etf.sab.tests;

import java.util.List;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import rs.etf.sab.operations.ArticleOperations;
import rs.etf.sab.operations.CityOperations;
import rs.etf.sab.operations.GeneralOperations;
import rs.etf.sab.operations.ShopOperations;

public class ShopOperationsTest {
    private TestHandler testHandler;
    private GeneralOperations generalOperations;
    private ShopOperations shopOperations;
    private CityOperations cityOperations;
    private ArticleOperations articleOperations;

    public ShopOperationsTest() {
    }

    @Before
    public void setUp() throws Exception {
        this.testHandler = TestHandler.getInstance();
        Assert.assertNotNull(this.testHandler);
        this.shopOperations = this.testHandler.getShopOperations();
        Assert.assertNotNull(this.shopOperations);
        this.cityOperations = this.testHandler.getCityOperations();
        Assert.assertNotNull(this.cityOperations);
        this.articleOperations = this.testHandler.getArticleOperations();
        Assert.assertNotNull(this.articleOperations);
        this.generalOperations = this.testHandler.getGeneralOperations();
        Assert.assertNotNull(this.generalOperations);
        this.generalOperations.eraseAll();
    }

    @After
    public void tearDown() throws Exception {
        this.generalOperations.eraseAll();
    }

    @Test
    public void createShop() {
        int cityId = this.cityOperations.createCity("Kragujevac");
        Assert.assertNotEquals(-1L, (long)cityId);
        int shopId = this.shopOperations.createShop("Gigatron", "Kragujevac");
        Assert.assertEquals((long)shopId, (long)(Integer)this.cityOperations.getShops(cityId).get(0));
    }

    @Test
    public void setCity() {
        this.cityOperations.createCity("Kragujevac");
        int shopId = this.shopOperations.createShop("Gigatron", "Kragujevac");
        int cityId2 = this.cityOperations.createCity("Subotica");
        this.shopOperations.setCity(shopId, "Subotica");
        Assert.assertEquals((long)shopId, (long)(Integer)this.cityOperations.getShops(cityId2).get(0));
    }

    @Test
    public void discount() {
        this.cityOperations.createCity("Kragujevac");
        int shopId = this.shopOperations.createShop("Gigatron", "Kragujevac");
        this.shopOperations.setDiscount(shopId, 20);
        Assert.assertEquals(20L, (long)this.shopOperations.getDiscount(shopId));
    }

    @Test
    public void articles() {
        this.cityOperations.createCity("Kragujevac");
        int shopId = this.shopOperations.createShop("Gigatron", "Kragujevac");
        int articleId = this.articleOperations.createArticle(shopId, "Olovka", 10);
        Assert.assertNotEquals(-1L, (long)articleId);
        int articleId2 = this.articleOperations.createArticle(shopId, "Gumica", 5);
        Assert.assertNotEquals(-1L, (long)articleId2);
        this.shopOperations.increaseArticleCount(articleId, 5);
        this.shopOperations.increaseArticleCount(articleId, 2);
        int articleCount = this.shopOperations.getArticleCount(articleId);
        Assert.assertEquals(7L, (long)articleCount);
        List<Integer> articles = this.shopOperations.getArticles(shopId);
        Assert.assertEquals(2L, (long)articles.size());
        Assert.assertTrue(articles.contains(articleId) && articles.contains(articleId2));
    }
}
