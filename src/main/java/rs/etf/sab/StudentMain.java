package rs.etf.sab;

import rs.etf.sab.operations.*;
import rs.etf.sab.student.*;
import rs.etf.sab.tests.TestHandler;
import rs.etf.sab.tests.TestRunner;

public class StudentMain {

    public static void main(String[] args) {

        ArticleOperations articleOperations = new ia130010_ArticleOperations();
        BuyerOperations buyerOperations = new ia130010_BuyerOperations();
        CityOperations cityOperations = new ia130010_CityOperations();
        GeneralOperations generalOperations = new ia130010_GeneralOperations();
        OrderOperations orderOperations = new ia130010_OrderOperations(generalOperations);
        ShopOperations shopOperations = new ia130010_ShopOperations();
        TransactionOperations transactionOperations = new ia130010_TransactionOperations();

        TestHandler.createInstance(
                articleOperations,
                buyerOperations,
                cityOperations,
                generalOperations,
                orderOperations,
                shopOperations,
                transactionOperations
        );

        TestRunner.runTests();
    }
}
