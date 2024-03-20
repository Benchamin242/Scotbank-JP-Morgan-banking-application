package uk.co.asepstrath.bank;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;


public class Transactions {

    private Date timestamp;
    private String type;
    private BigDecimal amount;
    private String to;
    private String from;



    public Transactions(Date timestamp, BigDecimal amount, String from, String to, String type){
        this.timestamp = timestamp;
        this.type = type;
        this.amount = amount;
        this.to = to;
        this.from = from;

    }
    public Transactions(BigDecimal amount, String from, String to, String type){
        this.type = type;
        this.amount = amount;
        this.to = to;
        this.from = from;

    }
    public void processTransaction(Account to, Account from){
        switch (type){
            case ("PAYMENT"):

                try { //if insufficient balance then exception is thrown and payment was un-successful and not money was withdrawn or deposited
                    if(from != null) {
                        from.withdrawWithRoundup(amount);
                        if(to != null) { //change so that it does it for business
                            System.out.println("PAYMENT");
                            to.deposit(amount);
                        }
                    }
                }catch(Exception ignored){

                }
                break;
            case ("WITHDRAWAL"):
                try {
                    from.withdraw(amount);
                }catch(Exception ignored){
                }
                break;
            case ("DEPOSIT"):
                to.deposit(amount);

                break;
            case ("COLLECT ROUNDUPS"):
                to.deposit(amount);

                break;
            case ("TRANSFER"):
                try { //if insufficient balance then exception is thrown and payment was un-successful and not money was withdrawn or deposited
                    if(from != null) { // if from is null this suggests that the transfer is from another bank
                        from.withdraw(amount);
                    }
                    to.deposit(amount);


                }catch(Exception ignored){
                }
                break;
            default:
                break;

        }
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
    public Date getTimestamp(){
        return timestamp;
    }



    @Override
    public String toString() {
        String result = this.getType() + " " + this.getAmount() + " " + this.getTo() + " " + this.getFrom() + " " + this.getTimestamp();
        return result;
    }

}
