package uk.co.asepstrath.bank;


import io.jooby.Context;
import io.jooby.ModelAndView;
import io.jooby.Session;
import io.jooby.StatusCode;
import io.jooby.exception.StatusCodeException;
import io.jooby.test.MockContext;
import io.jooby.test.MockRouter;
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
    public void AuthenticateLogin(){
        App app = new App();
        AuthController authController = app.authController;
        BankController bankController = mock(BankController.class);
        Context context = mock(Context.class);
        //Session session = context.session();
        Session session = mock(Session.class);
        session.put("id","01b02232-eeff-4294-aad0-c3cdbbbf773c");

        ModelAndView model = bankController.submit(session);
        //ModelAndView model = new ModelAndView("home.hbs");


        //when(authController.AuthenticateLogin("Miss Lavina Waelchi", "test", context)).thenReturn(model);

        ModelAndView result = authController.AuthenticateLogin("Miss Lavina Waelchi", "test", context);

        assertEquals(model, result);



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

    /*@Test
    public void loginEncryption(){
        MockRouter router = new MockRouter(new App());
    }*/

}
