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
        a.deposit(BigDecimal.valueOf(20.00));
        a.deposit(BigDecimal.valueOf(50.00));

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
        a.deposit(BigDecimal.valueOf(40));
        a.withdraw(20);

        assertEquals(a.getBalance(), new BigDecimal("30.00"));
    }

    @Test
    void withdrawOverdraft() {
        Account a = new Account("testname","testID", new BigDecimal(10.00), false);
        a.deposit(BigDecimal.valueOf(30));


        Assertions.assertThrows(ArithmeticException.class,() -> a.withdraw(100));
    }

    @Test
    void test5() {
        Account a = new Account("testname","testID", new BigDecimal(00.00), false);
        a.deposit(BigDecimal.valueOf(20.00));
        a.deposit(BigDecimal.valueOf(10.00));
        a.deposit(BigDecimal.valueOf(10.00));
        a.deposit(BigDecimal.valueOf(10.00));
        a.deposit(BigDecimal.valueOf(10.00));
        a.deposit(BigDecimal.valueOf(10.00));
        a.withdraw(20.00);
        a.withdraw(20.00);
        a.withdraw(20.00);

        assertEquals(a.getBalance(), new BigDecimal("10.00"));

    }


    @Test
    void pennies(){
        Account a = new Account("testname","testID", new BigDecimal(0.00), false);
        a.deposit(BigDecimal.valueOf(5.45));
        a.deposit(BigDecimal.valueOf(17.56));

        assertEquals(a.getBalance(), new BigDecimal("23.01"));
    }

    @Test
    void stringTest(){

        Account a = new Account("testname","testID", new BigDecimal(0.00), false);
        assertEquals("testID testname 0 false", a.toString());
    }

    @Test
    void accountTest(){

        Account a = new Account("Pls", "Work", new BigDecimal(0.00), false);

        String name = "Pls";
        String unique = "Work";
        BigDecimal val = new BigDecimal(0.00);
        boolean re = false;

        Account Test = new Account(name, unique, val, re);

        assertEquals(name, a.getName());
        assertEquals(unique, a.getId());
        assertEquals(val, a.getBalance());
        assertEquals(re, a.getRe());
        assertEquals(Test.toString(), a.toString());
    }

    @Test
    public void depositTest(){
        Account a = new Account("Pls", "Work", new BigDecimal(0), false);

        BigDecimal initial = new BigDecimal(String.valueOf(a.getBalance()));
        BigDecimal add = new BigDecimal(100);

        a.deposit(add);

        BigDecimal expected = initial.add(new BigDecimal(String.valueOf(add)).setScale(2, BigDecimal.ROUND_HALF_DOWN));

        assertEquals(expected, a.getBalance());
    }
}
