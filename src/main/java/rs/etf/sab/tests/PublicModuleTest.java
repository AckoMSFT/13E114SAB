package rs.etf.sab.tests;

import java.math.BigDecimal;
import java.util.Calendar;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import rs.etf.sab.operations.ArticleOperations;
import rs.etf.sab.operations.BuyerOperations;
import rs.etf.sab.operations.CityOperations;
import rs.etf.sab.operations.GeneralOperations;
import rs.etf.sab.operations.OrderOperations;
import rs.etf.sab.operations.ShopOperations;
import rs.etf.sab.operations.TransactionOperations;

public class PublicModuleTest {
    private TestHandler testHandler;
    private GeneralOperations generalOperations;
    private ShopOperations shopOperations;
    private CityOperations cityOperations;
    private ArticleOperations articleOperations;
    private BuyerOperations buyerOperations;
    private OrderOperations orderOperations;
    private TransactionOperations transactionOperations;

    public PublicModuleTest() {
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
        this.buyerOperations = this.testHandler.getBuyerOperations();
        Assert.assertNotNull(this.buyerOperations);
        this.orderOperations = this.testHandler.getOrderOperations();
        Assert.assertNotNull(this.orderOperations);
        this.transactionOperations = this.testHandler.getTransactionOperations();
        Assert.assertNotNull(this.transactionOperations);
        this.generalOperations = this.testHandler.getGeneralOperations();
        Assert.assertNotNull(this.generalOperations);
        this.generalOperations.eraseAll();
    }

    @After
    public void tearDown() throws Exception {
        this.generalOperations.eraseAll();
    }

    @Test
    public void test() {
        Calendar initialTime = Calendar.getInstance();
        initialTime.clear();
        initialTime.set(2018, 0, 1);
        this.generalOperations.setInitialTime(initialTime);
        Calendar receivedTime = Calendar.getInstance();
        receivedTime.clear();
        receivedTime.set(2018, 0, 22);
        int cityB = this.cityOperations.createCity("B");
        int cityC1 = this.cityOperations.createCity("C1");
        int cityA = this.cityOperations.createCity("A");
        int cityC2 = this.cityOperations.createCity("C2");
        int cityC3 = this.cityOperations.createCity("C3");
        int cityC4 = this.cityOperations.createCity("C4");
        int cityC5 = this.cityOperations.createCity("C5");
        this.cityOperations.connectCities(cityB, cityC1, 8);
        this.cityOperations.connectCities(cityC1, cityA, 10);
        this.cityOperations.connectCities(cityA, cityC2, 3);
        this.cityOperations.connectCities(cityC2, cityC3, 2);
        this.cityOperations.connectCities(cityC3, cityC4, 1);
        this.cityOperations.connectCities(cityC4, cityA, 3);
        this.cityOperations.connectCities(cityA, cityC5, 15);
        this.cityOperations.connectCities(cityC5, cityB, 2);
        int shopA = this.shopOperations.createShop("shopA", "A");
        int shopC2 = this.shopOperations.createShop("shopC2", "C2");
        int shopC3 = this.shopOperations.createShop("shopC3", "C3");
        this.shopOperations.setDiscount(shopA, 20);
        this.shopOperations.setDiscount(shopC2, 50);
        int laptop = this.articleOperations.createArticle(shopA, "laptop", 1000);
        int monitor = this.articleOperations.createArticle(shopC2, "monitor", 200);
        int stolica = this.articleOperations.createArticle(shopC3, "stolica", 100);
        int sto = this.articleOperations.createArticle(shopC3, "sto", 200);
        this.shopOperations.increaseArticleCount(laptop, 10);
        this.shopOperations.increaseArticleCount(monitor, 10);
        this.shopOperations.increaseArticleCount(stolica, 10);
        this.shopOperations.increaseArticleCount(sto, 10);
        int buyer = this.buyerOperations.createBuyer("kupac", cityB);
        this.buyerOperations.increaseCredit(buyer, new BigDecimal("20000"));
        int order = this.buyerOperations.createOrder(buyer);
        this.orderOperations.addArticle(order, laptop, 5);
        this.orderOperations.addArticle(order, monitor, 4);
        this.orderOperations.addArticle(order, stolica, 10);
        this.orderOperations.addArticle(order, sto, 4);
        Assert.assertNull(this.orderOperations.getSentTime(order));
        Assert.assertTrue("created".equals(this.orderOperations.getState(order)));
        this.orderOperations.completeOrder(order);
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAA");
        Assert.assertTrue("sent".equals(this.orderOperations.getState(order)));
        BigDecimal acko = this.transactionOperations.getAmmountThatBuyerPayedForOrder(order);
        System.out.println("ACKO: " + acko);
        int buyerTransactionId = (Integer)this.transactionOperations.getTransationsForBuyer(buyer).get(0);
        Assert.assertEquals(initialTime, this.transactionOperations.getTimeOfExecution(buyerTransactionId));
        Assert.assertNull(this.transactionOperations.getTransationsForShop(shopA));
        System.out.println("buyerTransactionId: " + buyerTransactionId);
        BigDecimal shopAAmount = (new BigDecimal("5")).multiply(new BigDecimal("1000")).setScale(3);
        BigDecimal shopAAmountWithDiscount = (new BigDecimal("0.8")).multiply(shopAAmount).setScale(3);
        BigDecimal shopC2Amount = (new BigDecimal("4")).multiply(new BigDecimal("200")).setScale(3);
        BigDecimal shopC2AmountWithDiscount = (new BigDecimal("0.5")).multiply(shopC2Amount).setScale(3);
        BigDecimal shopC3Amount = (new BigDecimal("10")).multiply(new BigDecimal("100")).add((new BigDecimal("4")).multiply(new BigDecimal("200"))).setScale(3);
        BigDecimal amountWithoutDiscounts = shopAAmount.add(shopC2Amount).add(shopC3Amount).setScale(3);
        BigDecimal amountWithDiscounts = shopAAmountWithDiscount.add(shopC2AmountWithDiscount).add(shopC3Amount).setScale(3);
        BigDecimal systemProfit = amountWithDiscounts.multiply(new BigDecimal("0.05")).setScale(3);
        BigDecimal shopAAmountReal = shopAAmountWithDiscount.multiply(new BigDecimal("0.95")).setScale(3);
        BigDecimal shopC2AmountReal = shopC2AmountWithDiscount.multiply(new BigDecimal("0.95")).setScale(3);
        BigDecimal shopC3AmountReal = shopC3Amount.multiply(new BigDecimal("0.95")).setScale(3);
        BigDecimal finalPrice = this.orderOperations.getFinalPrice(order);
        System.out.println("finalPrice: " + finalPrice);
        System.out.println("amountWithDiscounts: " + amountWithDiscounts);
        BigDecimal discountSum = this.orderOperations.getDiscountSum(order);
        System.out.println("discountSum: " + discountSum);
        Assert.assertEquals(amountWithDiscounts, this.orderOperations.getFinalPrice(order));
        Assert.assertEquals(amountWithoutDiscounts.subtract(amountWithDiscounts), this.orderOperations.getDiscountSum(order));
        Assert.assertEquals(amountWithDiscounts, this.transactionOperations.getBuyerTransactionsAmmount(buyer));
        System.out.println("PROFIT...");
        Assert.assertEquals(this.transactionOperations.getShopTransactionsAmmount(shopA), (new BigDecimal("0")).setScale(3));
        Assert.assertEquals(this.transactionOperations.getShopTransactionsAmmount(shopC2), (new BigDecimal("0")).setScale(3));
        Assert.assertEquals(this.transactionOperations.getShopTransactionsAmmount(shopC3), (new BigDecimal("0")).setScale(3));
        Assert.assertEquals((new BigDecimal("0")).setScale(3), this.transactionOperations.getSystemProfit());
        this.generalOperations.time(2);
        Assert.assertEquals(initialTime, this.orderOperations.getSentTime(order));
        Assert.assertNull(this.orderOperations.getRecievedTime(order));
        long orderLocation = this.orderOperations.getLocation(order);
        System.out.println("orderLocation: " + orderLocation + " cityA: " + cityA);
        Assert.assertEquals((long)this.orderOperations.getLocation(order), (long)cityA);
        this.generalOperations.time(9);
        Assert.assertEquals((long)this.orderOperations.getLocation(order), (long)cityA);
        this.generalOperations.time(8);
        orderLocation = this.orderOperations.getLocation(order);
        System.out.println("orderLocation: " + orderLocation + " cityC5: " + cityC5);
        Assert.assertEquals((long)this.orderOperations.getLocation(order), (long)cityC5);
        this.generalOperations.time(5);
        Assert.assertEquals((long)this.orderOperations.getLocation(order), (long)cityB);
        Assert.assertEquals(receivedTime, this.orderOperations.getRecievedTime(order));
        Assert.assertEquals(shopAAmountReal, this.transactionOperations.getShopTransactionsAmmount(shopA));
        Assert.assertEquals(shopC2AmountReal, this.transactionOperations.getShopTransactionsAmmount(shopC2));
        Assert.assertEquals(shopC3AmountReal, this.transactionOperations.getShopTransactionsAmmount(shopC3));
        BigDecimal systemProfitActual = this.transactionOperations.getSystemProfit();
        System.out.println("systemProfitActual: " + systemProfitActual);
        Assert.assertEquals(systemProfit, this.transactionOperations.getSystemProfit());
        int shopATransactionId = this.transactionOperations.getTransactionForShopAndOrder(order, shopA);
        Assert.assertNotEquals(-1L, (long)shopATransactionId);
        Assert.assertEquals(receivedTime, this.transactionOperations.getTimeOfExecution(shopATransactionId));
    }
}
