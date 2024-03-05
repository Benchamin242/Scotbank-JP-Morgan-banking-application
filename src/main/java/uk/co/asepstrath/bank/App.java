package uk.co.asepstrath.bank;

import io.jooby.Jooby;
import io.jooby.handlebars.HandlebarsModule;
import io.jooby.helper.UniRestExtension;
import io.jooby.hikari.HikariModule;
import kong.unirest.core.HttpResponse;
import org.jasypt.iv.RandomIvGenerator;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.bank.AuthController;
import uk.co.asepstrath.bank.bank.BankController;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import kong.unirest.core.Unirest;
import io.jooby.jasypt.JasyptModule;

import static java.lang.String.valueOf;

public class App extends Jooby {
    ArrayList<Account> accounts = new ArrayList<Account>();
    AuthController authController;
    BankController bankController;
    {

        /*
        This section is used for setting up the Jooby Framework modules
         */
        //install(new JasyptModule());
        install(new UniRestExtension());
        install(new HandlebarsModule());
        install(new HikariModule("mem"));

        /*
        This will host any files in src/main/resources/assets on <host>/assets
        For example in the dice template (dice.hbs) it references "assets/dice.png" which is in resources/assets folder
         */
        assets("/assets/*", "/assets");
        assets("/service_worker.js","/service_worker.js");

        /*
        Now we set up our controllers and their dependencies
         */
        DataSource ds = require(DataSource.class);
        Logger log = getLog();

        bankController = new BankController(ds,log);
        authController = new AuthController(bankController);

        mvc(bankController);
        mvc(authController);

        /*
        Finally we register our application lifecycle methods
         */
        onStarted(() -> onStart());
        onStop(() -> onStop());

        post("/submitForm", req -> {

            String name = req.form(String.class);
            Account account = new Account(name, "ppp", new BigDecimal("0.00"), false);
            // ...

            return "Welcome " + account.getName();
        });

    }

    public static void main(final String[] args) {
        runApp(args, App::new);
    }

    /*
    This function will be called when the application starts up,
    it should be used to ensure that the DB is properly setup
     */

    public void onStart() {
        Logger log = getLog();
        log.info("Starting Up...");


        // Fetch DB Source
        DataSource ds = require(DataSource.class);
        // Open Connection to DB
        try (Connection connection = ds.getConnection()) {
            //
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("CREATE TABLE `Example` (`Key` varchar(255),`Value` varchar(255))");
            stmt.executeUpdate("INSERT INTO Example " + "VALUES ('WelcomeMessage', 'Welcome to A Bank')");
        } catch (SQLException e) {
            log.error("Database Creation Error",e);
        }

        try (Connection connection = ds.getConnection()) {

            //this line connects us to the api, uses a get statement to place all the information from the api into an
            //array of objects of type Account
            HttpResponse<Account[]> help = Unirest.get("https://api.asep-strath.co.uk/api/accounts").asObject(Account[].class);


            //beginning of our sql adventures, the stmt variable is what we call sql commands on like create table and stuff
            Statement stmt = connection.createStatement();

            //creating our table of accounts, will hold an id, a name, a balance, and a boolean called "roundup enabled"
            stmt.executeUpdate("CREATE TABLE `accountsTable` (`accountNum` int not null primary key , `id` varchar(255), `Name` varchar(255),`Balance` double, `roundupEnabled` boolean)");

            //this splits up our accounts into individual objects of type Account, placing them all in an array called "please"
            Account[] please = help.getBody();

            //this was just so i could test that it actually is seperating the accounts properly, it just prints out the details of each account
            for(Account account : please){
                //System.out.println(account.toString());
            }


            //now we are moving all the details into our accounts table, using a preparedstatement
            //prepared statement basically just means we have a statement already ready that we will be calling multiple times
            //we need it so that accounts with ` in the name or other tokenisers will not mess up the insert
            PreparedStatement pstmt = connection.prepareStatement("INSERT INTO accountsTable (accountNum, id, Name, Balance, roundupEnabled) VALUES (?, ?, ?, ?, ?)");

            //loops through our array of accounts, calling the preparedstatement on each iteration
            //(the "count" variable i added to basically act as an account number)
            int count = 1;
            for(Account account : please){

                //declaring all our variables to plug into the prepared statement
                String num = String.valueOf(count);
                String currId = account.getId();
                String currName = account.getName();
                double startingBal;
                if(account.getBalance() == null){
                    startingBal = 0.00;
                }
                else{
                    startingBal = account.getBalance().doubleValue();
                }
                boolean roundE = account.getRe();


                //plugs our variables into the prepared statement, then executes the statement
                pstmt.setString(1, num);
                pstmt.setString(2, currId);
                pstmt.setString(3, currName);
                pstmt.setDouble(4, startingBal);
                pstmt.setBoolean(5, roundE);

                pstmt.executeUpdate();
                System.out.println(num + " " + currId + " " + currName);
                count += 1;
            }
            stmt.executeUpdate("CREATE TABLE `transactionsTable` (`id` int, `businessName` varchar(255),`withdrawn` double)" );
            stmt.executeUpdate("INSERT INTO transactionsTable " + "VALUES (1,'The COOP', 50.00 )");
            stmt.executeUpdate("INSERT INTO transactionsTable " + "VALUES (1,'Morrison', 25.00 )");
            stmt.executeUpdate("INSERT INTO transactionsTable " + "VALUES (1,'Tesco', 25.00 )");
            String testPassword = authController.getPasswordEncryptor().encryptPassword("test");
            String test2 = "temp";

            stmt.executeUpdate("CREATE TABLE `accountsPassword` (`accountNum` int not null primary key, `password` varchar(255), foreign key (`accountNum`) references `accountsTable`(`accountNum`) )");
            stmt.executeUpdate("INSERT INTO accountsPassword " + "VALUES (1,'"+ testPassword +"')");
            stmt.executeUpdate("INSERT INTO accountsPassword " + "VALUES (2, '"+ authController.getPasswordEncryptor().encryptPassword("couch123") +"')");
            stmt.executeUpdate("INSERT INTO accountsPassword " + "VALUES (3,'"+ authController.getPasswordEncryptor().encryptPassword("456") +"')");
            stmt.executeUpdate("INSERT INTO accountsPassword " + "VALUES (4,'"+ authController.getPasswordEncryptor().encryptPassword("testing") +"')");
            stmt.executeUpdate("INSERT INTO accountsPassword " + "VALUES (5,'123apple')");
            stmt.executeUpdate("INSERT INTO accountsPassword " + "VALUES (6,'bank')");

            stmt.executeUpdate("CREATE TABLE `transactionHistory` (`id` int, `paidTo` varchar(255), `amount` double)");
            stmt.executeUpdate("INSERT INTO transactionHistory" + "VALUES (4, 'Rachel', 6.10)");
            stmt.executeUpdate("INSERT INTO transactionHistory" + "VALUES (4, 'Ross', 10)");

        } catch (SQLException e) {
            log.error("Database Creation Error" + e.getMessage());
        }




    }

    /*
    This function will be called when the application shuts down
     */
    public void onStop() {
        System.out.println("Shutting Down...");
    }

}
