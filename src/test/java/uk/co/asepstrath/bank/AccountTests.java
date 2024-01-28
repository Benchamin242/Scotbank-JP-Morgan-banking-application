package uk.co.asepstrath.bank;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AccountTests {


    @Test
    public void createAccount(){
        Account a = new Account();
        Assertions.assertTrue(a != null    );
        Assertions.assertTrue(a.getBalance()==0    );
    }

    @Test
    public void addFunds() {
        Account a = new Account();
        a.deposit(20);
        a.deposit(50);

        Assertions.assertTrue(a.getBalance()==70);
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
        Account a = new Account();
        a.deposit(40);
        a.withdraw(20);

        assertTrue(a.getBalance()==20);
    }

    @Test
    public void withdrawOverdraft() {
        Account a = new Account();
        a.deposit(30);


        Assertions.assertThrows(ArithmeticException.class,() -> a.withdraw(100));
    }

    @Test
    public void test5() {
        Account a = new Account();
        a.deposit(20);
        a.deposit(10);
        a.deposit(10);
        a.deposit(10);
        a.deposit(10);
        a.deposit(10);
        a.withdraw(20);
        a.withdraw(20);
        a.withdraw(20);

        assertTrue(a.getBalance()==10);

    }
}
