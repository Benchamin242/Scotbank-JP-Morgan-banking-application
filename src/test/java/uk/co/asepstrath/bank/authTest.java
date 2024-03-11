package uk.co.asepstrath.bank;


import io.jooby.Context;
import io.jooby.ModelAndView;
import io.jooby.Session;
import io.jooby.StatusCode;
import io.jooby.exception.StatusCodeException;
import io.jooby.test.MockContext;
import io.jooby.test.MockRouter;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.bank.AuthController;
import uk.co.asepstrath.bank.bank.BankController;

import javax.sql.DataSource;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
public class authTest {


    @Test
    public void AuthenticateCorrectLogin(){

        App app = new App();
        app.onStart();
        AuthController authController = app.authController;
        //AuthController authController = mock(AuthController.class);
        BankController bankController = app.bankController;
        MockContext context = new MockContext();

        Session session = context.session();
        session.put("id","01b02232-eeff-4294-aad0-c3cdbbbf773c");

        ModelAndView model = bankController.submit(session);

        ModelAndView result = authController.AuthenticateLogin("Miss Lavina Waelchi", "test", context);

        assertEquals(model.getView(), result.getView());

    }
    @Test
    public void AuthenticateIncorrectLogin(){ //Right username, wrong password
        App app = new App();
        app.onStart();
        AuthController authController = app.authController;
        BankController bankController = app.bankController;
        MockContext context = new MockContext();

        ModelAndView model = bankController.login();

        ModelAndView result = authController.AuthenticateLogin("Miss Lavina Waelchi", "wrongpassword", context);

        assertEquals(model.getView(), result.getView());

    }

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

}
