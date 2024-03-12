package uk.co.asepstrath.bank;

import java.math.BigDecimal;

public class Transactions {


    private String type;
    private BigDecimal amount;
    private String to;
    private String from;



    Transactions(BigDecimal amount, String from, String to, String type){
        this.type = type;
        this.amount = amount;
        this.to = to;
        this.from = from;

    }
    public void payment(){
        if(type.equals("PAYMENT")){

        }

    }
    public void withdrawal(){
        /*if(type.equals("WITHDRAWAL") && from.getBalance().compareTo(amount) == 1 ){
            from.withdraw(amount.doubleValue());
        }*/
    }
    public void Deposit(){
        /*if(type.equals("DEPOSIT")) {
            to.deposit(new BigDecimal("AMOUNT"));
        }*/

    }

    public void collectionRoundups(){
        if(type.equals("COLLECT ROUNDUPS")){

        }
    }

    public void transfer(){
        /*if(type.equals("TRANSFER")){
            to.deposit(amount);
            from.withdraw(amount.doubleValue());
        }*/
    }

    public String getType(){
        return type;
    }
    public BigDecimal getAmount(){
        return amount;
    }

    public String getTo(){
        return to;
    }

    public String getFrom(){
        return from;
    }

    @Override
    public String toString() {
        String result = this.getType() + " " + this.getAmount() + " " + this.getTo() + " " + this.getFrom();
        return result;
    }

}
