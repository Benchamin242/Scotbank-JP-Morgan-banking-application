package uk.co.asepstrath.bank;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class AccountTests {

    @Test
    public void createAccount(){
        Account a = new Account("");
        Assertions.assertTrue(a != null    );
        System.out.println(a.getBalance());
        Assertions.assertTrue(a.getBalance().equals(new BigDecimal("0.00")));
    }

    @Test
    public void addFunds() {
        Account a = new Account("");
        a.deposit(20.00);
        a.deposit(50.00);

        Assertions.assertTrue(a.getBalance().equals(new BigDecimal("70.00")));
       /* Assertions.assertEquals(expected,toBe);
        Assertions.assertNull(valueToBeNull);
// And their counterparts
        Assertions.assertFalse(valueToBeFalse);
        Assertions.assertNotEquals(expected,notToBe);
        Assertions.assertNotNull(valueNotToBeNull);
        */
    }

    @Test
    public void withdrawFunds() {
        Account a = new Account("");
        a.deposit(40);
        a.withdraw(20);

        assertTrue(a.getBalance().equals(new BigDecimal("20.00")));
    }

    @Test
    public void withdrawOverdraft() {
        Account a = new Account("");
        a.deposit(30);


        Assertions.assertThrows(ArithmeticException.class,() -> a.withdraw(100));
    }

    @Test
    public void test5() {
        Account a = new Account("");
        a.deposit(20.00);
        a.deposit(10.00);
        a.deposit(10.00);
        a.deposit(10.00);
        a.deposit(10.00);
        a.deposit(10.00);
        a.withdraw(20.00);
        a.withdraw(20.00);
        a.withdraw(20.00);

        assertTrue(a.getBalance().equals(new BigDecimal("10.00")));

    }


    @Test
    public void pennies(){
        Account a = new Account("");
        a.deposit(5.45);
        a.deposit(17.56);

        assertTrue(a.getBalance().equals(new BigDecimal("23.01")));
    }
}
