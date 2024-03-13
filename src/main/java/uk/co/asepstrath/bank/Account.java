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

    private boolean manager;

    private static final Logger log = LoggerFactory.getLogger(Account.class);



    public Account(String fullName, String uniqueid, BigDecimal startingBal, boolean re){
        id = uniqueid;
        name = fullName;
        startingBalance = startingBal;
        roundupEnabled = re;
    }
    public void deposit(BigDecimal x) { // takes in do  uble then turns it into a BigDecimal since it uses arbritary arithmetic
        BigDecimal amount = x.setScale(2, RoundingMode.HALF_DOWN);
        startingBalance = startingBalance.add(amount);
    }

    public BigDecimal getStartingBalance() {

        return startingBalance;
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





    public void withdraw(double x) throws ArithmeticException{
        BigDecimal amount = new BigDecimal(x);
        if(amount.compareTo(startingBalance) > 0){ //uses BigDecimal compareTo method ->  amount > balance
            throw new ArithmeticException("\n"+"Insufficient funds");
        } else {
            startingBalance = startingBalance.subtract(amount); //you cannot use normal arithmetic with BigDecimal, so we use the subtract method
        }
    }

    @Override
    public String toString() {
        String result = this.getId() + " " + this.getName() + " " + this.getStartingBalance() + " " + this.getRe();
        return result;
    }
}
