package uk.co.asepstrath.bank;

import java.math.BigDecimal;

public class Transactions {

    private String type;
    private BigDecimal amount;

    private Account to;

    private Account from;

    Transactions(String type, BigDecimal amount, Account to, Account from){
        type = type;
        amount = amount;
        to = to;
        from = from;

    }
    public void payment(){
        if(type.equals("Payment")){

        }

    }
    public void withdrawal(){
        if(type.equals("Withdrawal") && from.getBalance().compareTo(amount) == 1 ){
            from.withdraw(amount.doubleValue());
        }
    }
    public void Deposit(){
        if(type.equals("Deposit")) {
            to.deposit(new BigDecimal("amount"));
        }

    }

    public void collectionRoundups(){
        if(type.equals("Collect Roundups")){

        }
    }

    public void transfer(){
        if(type.equals("Transfer")){
            to.deposit(amount);
            from.withdraw(amount.doubleValue());
        }
    }

    /*@Override
    public String toString() {
        String result = this.getId() + " " + this.getName() + " " + this.getBalance() + " " + this.getRe();
        return result;
    }*/
}
