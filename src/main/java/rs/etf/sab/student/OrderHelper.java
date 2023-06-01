package rs.etf.sab.student;

import rs.etf.sab.operations.BuyerOperations;

public class OrderHelper {

    static BuyerOperations buyerOperations = new ia130010_BuyerOperations();
    public static enum OrderState
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

    public static enum TransactionType
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

    public static enum TransitType
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


    public static boolean isOrderCompleted(String state) {
        return !state.equals(OrderState.CREATED.getState());
    }

    public static int getOrderDestinationCity(int buyerId) {
        return buyerOperations.getCity(buyerId);
    }

    public static int getBuyerCity(int buyerId) {
        return buyerOperations.getCity(buyerId);
    }
}
