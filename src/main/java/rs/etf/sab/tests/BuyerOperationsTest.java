package rs.etf.sab.tests;

import java.math.BigDecimal;
import java.util.List;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import rs.etf.sab.operations.BuyerOperations;
import rs.etf.sab.operations.CityOperations;
import rs.etf.sab.operations.GeneralOperations;

public class BuyerOperationsTest {
    private TestHandler testHandler;
    private GeneralOperations generalOperations;
    private CityOperations cityOperations;
    private BuyerOperations buyerOperations;

    public BuyerOperationsTest() {
    }

    @Before
    public void setUp() throws Exception {
        this.testHandler = TestHandler.getInstance();
        Assert.assertNotNull(this.testHandler);
        this.cityOperations = this.testHandler.getCityOperations();
        Assert.assertNotNull(this.cityOperations);
        this.generalOperations = this.testHandler.getGeneralOperations();
        Assert.assertNotNull(this.generalOperations);
        this.buyerOperations = this.testHandler.getBuyerOperations();
        Assert.assertNotNull(this.buyerOperations);
        this.generalOperations.eraseAll();
    }

    @After
    public void tearDown() throws Exception {
        this.generalOperations.eraseAll();
    }

    @Test
    public void insertBuyer() {
        int cityId = this.cityOperations.createCity("Kragujevac");
        Assert.assertNotEquals(-1L, (long)cityId);
        int buyerId = this.buyerOperations.createBuyer("Pera", cityId);
        Assert.assertNotEquals(-1L, (long)buyerId);
    }

    @Test
    public void changeCity() {
        int cityId1 = this.cityOperations.createCity("Kragujevac");
        int cityId2 = this.cityOperations.createCity("Beograd");
        int buyerId = this.buyerOperations.createBuyer("Lazar", cityId1);
        this.buyerOperations.setCity(buyerId, cityId2);
        int cityId = this.buyerOperations.getCity(buyerId);
        Assert.assertEquals((long)cityId2, (long)cityId);
    }

    @Test
    public void credit() {
        int cityId = this.cityOperations.createCity("Kragujevac");
        int buyerId = this.buyerOperations.createBuyer("Pera", cityId);
        BigDecimal credit1 = (new BigDecimal("1000.000")).setScale(3);
        BigDecimal creditReturned = this.buyerOperations.increaseCredit(buyerId, credit1);
        Assert.assertEquals(credit1, creditReturned);
        BigDecimal credit2 = new BigDecimal("500");
        this.buyerOperations.increaseCredit(buyerId, credit2).setScale(3);
        creditReturned = this.buyerOperations.getCredit(buyerId);
        Assert.assertEquals(credit1.add(credit2).setScale(3), creditReturned);
    }

    @Test
    public void orders() {
        int cityId = this.cityOperations.createCity("Kragujevac");
        int buyerId = this.buyerOperations.createBuyer("Pera", cityId);
        int orderId1 = this.buyerOperations.createOrder(buyerId);
        int orderId2 = this.buyerOperations.createOrder(buyerId);
        Assert.assertNotEquals(-1L, (long)orderId1);
        Assert.assertNotEquals(-1L, (long)orderId2);
        List<Integer> orders = this.buyerOperations.getOrders(buyerId);
        Assert.assertEquals(2L, (long)orders.size());
        Assert.assertTrue(orders.contains(orderId1) && orders.contains(orderId2));
    }
}
