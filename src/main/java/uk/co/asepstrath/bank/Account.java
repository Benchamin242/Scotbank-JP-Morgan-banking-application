package uk.co.asepstrath.bank;

public class Account {
    private int balance;

    public Account(){
        balance=0;
    }
    public void deposit(int amount) {
        balance += amount;
    }

    public int getBalance() {

        return balance;
    }

    public void withdraw(int amount){
        balance -=amount;
    }

}
