package uk.co.asepstrath.bank;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.jooby.Context;
import io.jooby.Route;


public class Account {

    private  String id;
    private final String name;
    private BigDecimal balance; //BigDecimal is more accurate when doing arithmetic
    private boolean roundupEnabled;

    private boolean managerOrNot;

    private static final Logger log = LoggerFactory.getLogger(Account.class);



    public Account(String fullName, String uniqueid, BigDecimal startingBal, boolean re){
        id = uniqueid;
        name = fullName;
        balance= startingBal;
        roundupEnabled = re;
        managerOrNot = false;
    }

    public void deposit(BigDecimal x) { // takes in do  uble then turns it into a BigDecimal since it uses arbritary arithmetic
        BigDecimal amount = new BigDecimal(String.valueOf(x)).setScale(2, RoundingMode.HALF_DOWN);
        balance = balance.add(amount);
    }

    public BigDecimal getBalance() {

        return balance;
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

    public void makeManager() {
        managerOrNot = true;
    }





    public void withdraw(double x) throws ArithmeticException{
        BigDecimal amount = new BigDecimal(x);
        if(amount.compareTo(balance) > 0){ //uses BigDecimal compareTo method ->  amount > balance
            throw new ArithmeticException("\n"+"Insufficient funds");
        } else {
            balance = balance.subtract(amount); //you cannot use normal arithmetic with BigDecimal, so we use the subtract method
        }
    }

    @Override
    public String toString() {
        String result = this.getId() + " " + this.getName() + " " + this.getBalance() + " " + this.getRe();
        return result;
    }
}
