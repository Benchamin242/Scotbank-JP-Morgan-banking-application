package uk.co.asepstrath.bank;

import java.math.BigDecimal;

public class Account {
    private BigDecimal balance; //BigDecimal is more accurate when doing arithmetic

    public Account(){
        balance= new BigDecimal("0.00");
    }
    public void deposit(double x) { // takes in double then turns it into a BigDecimal since it uses arbritary arithmetic
        BigDecimal amount = new BigDecimal(x);
        balance = balance.add(amount);
    }

    public BigDecimal getBalance() {

        return balance;
    }

    public void withdraw(double x) throws ArithmeticException{
        BigDecimal amount = new BigDecimal(x);
        if(amount.compareTo(balance) > 0){ //uses BigDecimal compareTo method ->  amount > balance
            throw new ArithmeticException("\n"+"Insufficient funds");
        } else {
            balance = balance.subtract(amount); //you cannot use normal arithmetic with BigDecimal, so we use the subtract method
        }
        }

}
