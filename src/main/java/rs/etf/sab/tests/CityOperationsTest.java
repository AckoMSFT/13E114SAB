package rs.etf.sab.tests;

import java.util.List;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import rs.etf.sab.operations.CityOperations;
import rs.etf.sab.operations.GeneralOperations;
import rs.etf.sab.operations.ShopOperations;

public class CityOperationsTest {
    private TestHandler testHandler;
    private GeneralOperations generalOperations;
    private CityOperations cityOperations;
    private ShopOperations shopOperations;

    public CityOperationsTest() {
    }

    @Before
    public void setUp() throws Exception {
        this.testHandler = TestHandler.getInstance();
        Assert.assertNotNull(this.testHandler);
        this.cityOperations = this.testHandler.getCityOperations();
        Assert.assertNotNull(this.cityOperations);
        this.generalOperations = this.testHandler.getGeneralOperations();
        Assert.assertNotNull(this.generalOperations);
        this.shopOperations = this.testHandler.getShopOperations();
        Assert.assertNotNull(this.shopOperations);
        this.generalOperations.eraseAll();
    }

    @After
    public void tearDown() throws Exception {
        this.generalOperations.eraseAll();
    }

    @Test
    public void createCity() {
        int cityVranje = this.cityOperations.createCity("Vranje");
        Assert.assertEquals(1L, (long)this.cityOperations.getCities().size());
        Assert.assertEquals((long)cityVranje, (long)(Integer)this.cityOperations.getCities().get(0));
    }

    @Test
    public void insertShops() {
        int cityId = this.cityOperations.createCity("Vranje");
        int shopId1 = this.shopOperations.createShop("Gigatron", "Vranje");
        int shopId2 = this.shopOperations.createShop("Teranova", "Vranje");
        List<Integer> shops = this.cityOperations.getShops(cityId);
        Assert.assertEquals(2L, (long)shops.size());
        Assert.assertTrue(shops.contains(shopId1) && shops.contains(shopId2));
    }

    @Test
    public void connectCities() {
        int cityVranje = this.cityOperations.createCity("Vranje");
        int cityLeskovac = this.cityOperations.createCity("Leskovac");
        int cityNis = this.cityOperations.createCity("Nis");
        Assert.assertNotEquals(-1L, (long)cityLeskovac);
        Assert.assertNotEquals(-1L, (long)cityVranje);
        Assert.assertNotEquals(-1L, (long)cityNis);
        this.cityOperations.connectCities(cityNis, cityVranje, 50);
        this.cityOperations.connectCities(cityVranje, cityLeskovac, 70);
        List<Integer> connectedCities = this.cityOperations.getConnectedCities(cityVranje);
        Assert.assertEquals(2L, (long)connectedCities.size());
        Assert.assertTrue(connectedCities.contains(cityLeskovac) && connectedCities.contains(cityNis));
    }
}
