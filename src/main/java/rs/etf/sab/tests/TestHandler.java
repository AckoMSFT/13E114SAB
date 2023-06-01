package rs.etf.sab.tests;


import javax.validation.constraints.NotNull;
import rs.etf.sab.operations.ArticleOperations;
import rs.etf.sab.operations.BuyerOperations;
import rs.etf.sab.operations.CityOperations;
import rs.etf.sab.operations.GeneralOperations;
import rs.etf.sab.operations.OrderOperations;
import rs.etf.sab.operations.ShopOperations;
import rs.etf.sab.operations.TransactionOperations;

public class TestHandler {
    private static TestHandler testHandler = null;
    private ArticleOperations articleOperations;
    private BuyerOperations buyerOperations;
    private CityOperations cityOperations;
    private GeneralOperations generalOperations;
    private OrderOperations orderOperations;
    private ShopOperations shopOperations;
    private TransactionOperations transactionOperations;

    public TestHandler(@NotNull ArticleOperations articleOperations, @NotNull BuyerOperations buyerOperations, @NotNull CityOperations cityOperations, @NotNull GeneralOperations generalOperations, @NotNull OrderOperations orderOperations, @NotNull ShopOperations shopOperations, @NotNull TransactionOperations transactionOperations) {
        this.articleOperations = articleOperations;
        this.buyerOperations = buyerOperations;
        this.cityOperations = cityOperations;
        this.generalOperations = generalOperations;
        this.orderOperations = orderOperations;
        this.shopOperations = shopOperations;
        this.transactionOperations = transactionOperations;
    }

    public static void createInstance(@NotNull ArticleOperations articleOperations, @NotNull BuyerOperations buyerOperations, @NotNull CityOperations cityOperations, @NotNull GeneralOperations generalOperations, @NotNull OrderOperations orderOperations, @NotNull ShopOperations shopOperations, @NotNull TransactionOperations transactionOperations) {
        testHandler = new TestHandler(articleOperations, buyerOperations, cityOperations, generalOperations, orderOperations, shopOperations, transactionOperations);
    }

    static TestHandler getInstance() {
        return testHandler;
    }

    public ArticleOperations getArticleOperations() {
        return this.articleOperations;
    }

    public BuyerOperations getBuyerOperations() {
        return this.buyerOperations;
    }

    public CityOperations getCityOperations() {
        return this.cityOperations;
    }

    public GeneralOperations getGeneralOperations() {
        return this.generalOperations;
    }

    public OrderOperations getOrderOperations() {
        return this.orderOperations;
    }

    public ShopOperations getShopOperations() {
        return this.shopOperations;
    }

    public TransactionOperations getTransactionOperations() {
        return this.transactionOperations;
    }
}
