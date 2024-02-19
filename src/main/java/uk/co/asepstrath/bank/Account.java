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
    private BigDecimal balance; //BigDecimal is more accurate when doing arithmetic
    private final String name;
    private static final Logger log = LoggerFactory.getLogger(Account.class);



    public Account(String n){
        name = n;
        balance= new BigDecimal("0.00");
    }
    public void deposit(double x) { // takes in do  uble then turns it into a BigDecimal since it uses arbritary arithmetic
        BigDecimal amount = new BigDecimal(x).setScale(2, RoundingMode.HALF_DOWN);
        balance = balance.add(amount);
    }

    public BigDecimal getBalance() {

        return balance;
    }
    public String getName(){
        return name;
    }


    public void displaySummaryOfSpending(DataSource ds) {




        try (Connection connection = ds.getConnection()) {
            Statement stmt = connection.createStatement();


            ResultSet resultSet = stmt.executeQuery("SELECT businessName, withdrawn FROM transactionsTable");


            Map<String, Double> spendingSummary = new HashMap<>();
            while (resultSet.next()) {
                String businessCategory = resultSet.getString("businessName");
                double amountWithdrawn = resultSet.getDouble("withdrawn");


                spendingSummary.put(businessCategory, spendingSummary.getOrDefault(businessCategory, 0.0) + amountWithdrawn);
            }


            System.out.println("Overall Spending Summary:");
            for (Map.Entry<String, Double> entry : spendingSummary.entrySet()) {
                System.out.println("Business Category: " + entry.getKey() + ", Total Spent: " + entry.getValue());
            }

        } catch (SQLException e) {
            log.error("Error fetching spending data", e);
        }
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
