package rs.etf.sab.student;

import rs.etf.sab.operations.BuyerOperations;

public class OrderHelper {

    static BuyerOperations buyerOperations = new ia130010_BuyerOperations();

    /*
        Order can have 3 possible states:
            - created
            - sent
            - arrived
     */
    public enum OrderState
    {
        CREATED("created"),
        SENT("sent"),
        ARRIVED("arrived");

        private String state;

        OrderState(String state) {
            this.state = state;
        }

        public String getState() {
            return this.state;
        }
    }

    /*
        Transaction can have 2 possible types:
            - buyer
            - shop
     */
    public enum TransactionType
    {
        BUYER(0),
        SHOP(1);
        private int type;

        TransactionType(int type) {
            this.type = type;
        }

        public int getType() {
            return this.type;
        }
    }

    /*
        Transit can have 2 different types:
            - item
            - order
     */
    public enum TransitType
    {
        ITEM(0),
        ORDER(1);
        private int type;

        TransitType(int type) {
            this.type = type;
        }

        public int getType() {
            return this.type;
        }
    }

    /*
        Checks if the order has been completed
     */
    public static boolean isOrderCompleted(String state) {
        return !state.equals(OrderState.CREATED.getState());
    }

    /*
        For a given order with given buyerId finds the destination city (city of the buyer)
     */
    public static int getOrderDestinationCity(int buyerId) {
        return buyerOperations.getCity(buyerId);
    }

    /*
        Returns city of the buyer
     */
    public static int getBuyerCity(int buyerId) {
        return buyerOperations.getCity(buyerId);
    }
}
