package uk.co.asepstrath.bank;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Account {

    private  String id;
    private final String name;
    private BigDecimal startingBalance; //BigDecimal is more accurate when doing arithmetic
    private boolean roundupEnabled;

    private String postcode;
    private int cardDetails;

    private BigDecimal roundUpBalance = new BigDecimal("0.00");


    public Account(String fullName, String uniqueid,  BigDecimal startingBal, boolean re){
        id = uniqueid;
        name = fullName;
        startingBalance = startingBal;
        roundupEnabled = re;
    }
    public Account(String fullName, String uniqueid,  BigDecimal startingBal, boolean re, String postcode, int cardDetails){
        id = uniqueid;
        name = fullName;
        startingBalance = startingBal;
        roundupEnabled = re;
        this.postcode = postcode;
        this.cardDetails = cardDetails;
    }
    public void deposit(BigDecimal amount) { // takes in do  uble then turns it into a BigDecimal since it uses arbritary arithmetic
        //BigDecimal amount = x.setScale(3, RoundingMode.HALF_DOWN);
        startingBalance = startingBalance.add(amount);
        startingBalance = startingBalance.setScale(2,RoundingMode.HALF_DOWN);
    }

    public BigDecimal getStartingBalance() {

        return startingBalance;
    }
    public BigDecimal getRoundupBalance() {

        return roundUpBalance;
    }

    public String getName(){
        return name;
    }

    public String getId(){
        return id;
    }

    public boolean getRe(){
        return roundupEnabled;
    }

    public String getPostcode(){
        return postcode;
    }
    public int getCardDetails(){
        return cardDetails;
    }




    public void setRoundUp(boolean x){
        roundupEnabled = x;
    }
    public void withdraw(BigDecimal amount) throws ArithmeticException{
        //BigDecimal amount = new BigDecimal(x);
        //BigDecimal amount = x.setScale(3, RoundingMode.HALF_DOWN);

        if(amount.compareTo(startingBalance) > 0){ //uses BigDecimal compareTo method ->  amount > balance
            throw new ArithmeticException("\n"+"Insufficient funds");
        } else {
            startingBalance = startingBalance.subtract(amount); //you cannot use normal arithmetic with BigDecimal, so we use the subtract method
            startingBalance = startingBalance.setScale(2,RoundingMode.HALF_DOWN);

        }
    }
    public void withdrawWithRoundup(BigDecimal amount) throws ArithmeticException{

        if(roundupEnabled) {
            if (amount.compareTo(startingBalance) > 0) { //uses BigDecimal compareTo method ->  amount > balance
                throw new ArithmeticException("\n" + "Insufficient funds");
            } else {
                BigDecimal temp = amount; // £1.10
                amount = amount.setScale(0, RoundingMode.UP); //£2
                temp = amount.subtract(temp); //£0.90
                startingBalance = startingBalance.subtract(amount); //you cannot use normal arithmetic with BigDecimal, so we use the subtract method
                startingBalance = startingBalance.setScale(2, RoundingMode.HALF_DOWN);
                roundUpBalance = roundUpBalance.add(temp);
                roundUpBalance = roundUpBalance.setScale(2, RoundingMode.HALF_DOWN);
            }
        }else{
            this.withdraw(amount);
        }
    }

    @Override
    public String toString() {
        String result = this.getId() + " " + this.getName() + " " + this.getStartingBalance() + " " + this.getRe() + " " + this.getPostcode() + " " + this.getCardDetails() ;
        return result;
    }
}
