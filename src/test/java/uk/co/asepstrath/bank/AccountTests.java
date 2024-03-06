package uk.co.asepstrath.bank;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class AccountTests {

    @Test
    void createAccount(){
        Account a = new Account("testname","testID", new BigDecimal(10.00), false);
        assertNotNull(a);
        System.out.println(a.getBalance());
        assertEquals("testname", a.getName());
        assertEquals("testID", a.getId());
        assertEquals(a.getBalance(), new BigDecimal(10.00));
        assertFalse(a.getRe());
    }

    @Test
    void addFunds() {
        Account a = new Account("testname","testID", new BigDecimal(10.00), false);
        a.deposit(20.00);
        a.deposit(50.00);

        assertEquals(a.getBalance(), new BigDecimal("80.00"));
       //Assertions.assertEquals(expected,toBe);
        //Assertions.assertNull(valueToBeNull);
        // And their counterparts
        //Assertions.assertFalse(valueToBeFalse);
        //Assertions.assertNotEquals(expected,notToBe);
        //Assertions.assertNotNull(valueNotToBeNull);

    }

    @Test
    void withdrawFunds() {
        Account a = new Account("testname","testID", new BigDecimal(10.00), false);
        a.deposit(40);
        a.withdraw(20);

        assertEquals(a.getBalance(), new BigDecimal("30.00"));
    }

    @Test
    void withdrawOverdraft() {
        Account a = new Account("testname","testID", new BigDecimal(10.00), false);
        a.deposit(30);


        Assertions.assertThrows(ArithmeticException.class,() -> a.withdraw(100));
    }

    @Test
    void test5() {
        Account a = new Account("testname","testID", new BigDecimal(00.00), false);
        a.deposit(20.00);
        a.deposit(10.00);
        a.deposit(10.00);
        a.deposit(10.00);
        a.deposit(10.00);
        a.deposit(10.00);
        a.withdraw(20.00);
        a.withdraw(20.00);
        a.withdraw(20.00);

        assertEquals(a.getBalance(), new BigDecimal("10.00"));

    }


    @Test
    void pennies(){
        Account a = new Account("testname","testID", new BigDecimal(0.00), false);
        a.deposit(5.45);
        a.deposit(17.56);

        assertEquals(a.getBalance(), new BigDecimal("23.01"));
    }

    @Test
    void stringTest(){

        Account a = new Account("testname","testID", new BigDecimal(0.00), false);
        assertEquals("testID testname 0 false", a.toString());
    }
}
