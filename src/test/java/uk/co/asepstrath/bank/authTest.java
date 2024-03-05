package uk.co.asepstrath.bank;


import io.jooby.StatusCode;
import io.jooby.exception.StatusCodeException;
import io.jooby.test.MockRouter;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.bank.AuthController;
import uk.co.asepstrath.bank.bank.BankController;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class authTest {

    @Test
    public void encryption() throws SQLException {

        //case: should work
        String inputPassword = "couch123";
        String userPassword = "couch123";
        StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
        String encryptedPassword = passwordEncryptor.encryptPassword(userPassword);

        assertTrue(passwordEncryptor.checkPassword(inputPassword, encryptedPassword));


        //case: shouldn't work
        userPassword = "couch1233";
        encryptedPassword = passwordEncryptor.encryptPassword(userPassword);

        assertFalse(passwordEncryptor.checkPassword(inputPassword, encryptedPassword));

        userPassword = "couch1233";
        encryptedPassword = passwordEncryptor.encryptPassword("couch123");
        System.out.println(encryptedPassword);
        String temp = "Sxh3upR4bTtynk57LcTs2gCjXPeRNb01YPeQGnqt5DSuJ6rtAHxnxrVynB7hIFpi";

        try {
            assertTrue(passwordEncryptor.checkPassword(inputPassword, userPassword));
        }catch(EncryptionOperationNotPossibleException e){
            assertFalse(false);
        }




    }

    /*@Test
    public void loginEncryption(){
        MockRouter router = new MockRouter(new App());
    }*/

}
