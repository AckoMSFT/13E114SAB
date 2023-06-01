package rs.etf.sab.tests;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import rs.etf.sab.operations.*;

public class AckoTest {
    private TestHandler testHandler;

    private BuyerOperations buyerOperations;
    private GeneralOperations generalOperations;
    private ShopOperations shopOperations;
    private CityOperations cityOperations;
    private ArticleOperations articleOperations;

    private OrderOperations orderOperations;

    public AckoTest() {
    }

    @Before
    public void setUp() throws Exception {
        this.testHandler = TestHandler.getInstance();
        Assert.assertNotNull(this.testHandler);
        this.buyerOperations = this.testHandler.getBuyerOperations();
        Assert.assertNotNull(this.buyerOperations);
        this.shopOperations = this.testHandler.getShopOperations();
        Assert.assertNotNull(this.shopOperations);
        this.cityOperations = this.testHandler.getCityOperations();
        Assert.assertNotNull(this.cityOperations);
        this.articleOperations = this.testHandler.getArticleOperations();
        Assert.assertNotNull(this.articleOperations);
        this.generalOperations = this.testHandler.getGeneralOperations();
        Assert.assertNotNull(this.generalOperations);
        this.orderOperations = this.testHandler.getOrderOperations();
        Assert.assertNotNull(this.orderOperations);
        this.generalOperations.eraseAll();
    }

    @After
    public void tearDown() throws Exception {
        this.generalOperations.eraseAll();
    }

    @Test
    public void findShortestPath() {
        int cityKragujevac = this.cityOperations.createCity("Kragujevac");
        int cityBeograd = this.cityOperations.createCity("Beograd");
        int cityLeskovac = this.cityOperations.createCity("Leskovac");
        int cityVranje = this.cityOperations.createCity("Vranje");
        int citySubotica = this.cityOperations.createCity("Subotica");
        Assert.assertNotEquals(-1L, cityKragujevac);
        Assert.assertNotEquals(-1L, cityBeograd);
        Assert.assertNotEquals(-1L, cityLeskovac);
        Assert.assertNotEquals(-1L, cityVranje);
        Assert.assertNotEquals(-1L, citySubotica);
        this.cityOperations.connectCities(cityKragujevac, cityBeograd, 1);
        this.cityOperations.connectCities(cityBeograd, cityLeskovac, 1);
        this.cityOperations.connectCities(cityLeskovac, cityVranje, 1);
        this.cityOperations.connectCities(cityVranje, citySubotica, 1);
        int shopGigatronBeograd = this.shopOperations.createShop("Gigatron", "Beograd");
        Assert.assertNotEquals(-1L, shopGigatronBeograd);
        int buyerAckoSubotica = this.buyerOperations.createBuyer("Acko", citySubotica);
        Assert.assertNotEquals(-1L, buyerAckoSubotica);
        int orderAcko = this.buyerOperations.createOrder(buyerAckoSubotica);
        Assert.assertNotEquals(-1, orderAcko);
        this.orderOperations.findShortestPath(orderAcko, cityBeograd, citySubotica);
        List<Integer> path = this.orderOperations.getPath(orderAcko);
        List<Integer> expectedPath = Arrays.asList(cityBeograd, cityLeskovac, cityVranje, citySubotica);
        Assert.assertEquals(path, expectedPath);
        // TODO (acko): Move me out
        int articlePS5 = this.articleOperations.createArticle(shopGigatronBeograd, "PS5", 10);
        Assert.assertNotEquals(-1, articlePS5);
        int increasedPS5Count = this.shopOperations.increaseArticleCount(articlePS5, 15);
        Assert.assertNotEquals(-1, increasedPS5Count);
        int itemPS5 = this.orderOperations.addArticle(orderAcko, articlePS5, 5);
        Assert.assertNotEquals(-1, itemPS5);
        int articlePS5Count = this.orderOperations.getArticleCount(orderAcko, articlePS5);
        Assert.assertEquals(articlePS5Count, 5);
        int sameItemPS5 = this.orderOperations.addArticle(orderAcko, articlePS5, 10);
        Assert.assertEquals(itemPS5, sameItemPS5);
        articlePS5Count = this.orderOperations.getArticleCount(orderAcko, articlePS5);
        Assert.assertEquals(articlePS5Count, 15);
        int removePS5Success = this.orderOperations.removeArticle(orderAcko, articlePS5);
        Assert.assertEquals(removePS5Success, 1);
        articlePS5Count = this.orderOperations.getArticleCount(orderAcko, articlePS5);
        Assert.assertEquals(articlePS5Count, 0);
    }

    @Test
    public void testUniqueCityName() {
        int cityKragujevac = this.cityOperations.createCity("Kragujevac");
        int cityKragujevacAgain = this.cityOperations.createCity("Kragujevac");
        Assert.assertNotEquals(-1L, cityKragujevac);
        Assert.assertEquals(-1L, cityKragujevacAgain);
        /*
            com.microsoft.sqlserver.jdbc.SQLServerException: Violation of UNIQUE KEY constraint 'XAK1CityUniqueName'.
            Cannot insert duplicate key in object 'dbo.City'. The duplicate key value is (Kragujevac).
         */
    }

    @Test
    public void testUniqueEdges() {
        int cityKragujevac = this.cityOperations.createCity("Kragujevac");
        int cityBeograd = this.cityOperations.createCity("Beograd");
        Assert.assertNotEquals(-1L, cityKragujevac);
        Assert.assertNotEquals(-1L, cityBeograd);
        int edgeId = this.cityOperations.connectCities(cityBeograd, cityKragujevac, 5);
        Assert.assertNotEquals(-1L, edgeId);
        int invalidEdgeId = this.cityOperations.connectCities(cityBeograd, cityKragujevac, 5);
        Assert.assertEquals(-1L, invalidEdgeId);
        invalidEdgeId = this.cityOperations.connectCities(cityBeograd, cityKragujevac, 10);
        Assert.assertEquals(-1L, invalidEdgeId);
        invalidEdgeId = this.cityOperations.connectCities(cityKragujevac, cityBeograd, 5);
        Assert.assertEquals(-1L, invalidEdgeId);
        // Violation of UNIQUE KEY constraint 'XAK1ConnectedCitiesUniqueEdge'. Cannot insert duplicate key in object 'dbo.ConnectedCities'. The duplicate key value is (1, 2).
    }

    @Test
    public void testUniqueShopName() {
        int cityKragujevac = this.cityOperations.createCity("Kragujevac");
        Assert.assertNotEquals(-1L, cityKragujevac);
        int shopGigatron = this.shopOperations.createShop("Gigatron", "Kragujevac");
        Assert.assertNotEquals(-1L, shopGigatron);
        shopGigatron = this.shopOperations.createShop("Gigatron", "Kragujevac");
        Assert.assertEquals(-1L, shopGigatron);
        int cityBeograd = this.cityOperations.createCity("Beograd");
        Assert.assertNotEquals(-1L, cityBeograd);
        shopGigatron = this.shopOperations.createShop("Gigatron", "Beograd");
        Assert.assertEquals(-1L, shopGigatron);
        // Violation of UNIQUE KEY constraint 'XAK1ShopUniqueName'. Cannot insert duplicate key in object 'dbo.Shop'. The duplicate key value is 'Gigatron'.
    }
}
