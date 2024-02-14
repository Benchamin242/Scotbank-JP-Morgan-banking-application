package uk.co.asepstrath.bank;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class Account {
    private BigDecimal balance; //BigDecimal is more accurate when doing arithmetic
    private final String name;

    public Account(String n){
        name = n;
        balance= new BigDecimal("0.00");
    }
    public void deposit(double x) { // takes in double then turns it into a BigDecimal since it uses arbritary arithmetic
        BigDecimal amount = new BigDecimal(x).setScale(2, RoundingMode.HALF_DOWN);
        balance = balance.add(amount);
    }

    public BigDecimal getBalance() {

        return balance;
    }
    public String getName(){
        return name;
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
        String result = this.getName() + " " + this.getBalance();
        return result;
    }
}
