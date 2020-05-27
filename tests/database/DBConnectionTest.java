package database;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

class DBConnectionTest {
    DBConnection dbConn = null;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
        try {
            dbConn.closeConnection();
        } catch (DataAccessException e) {
            Assertions.fail("Exception thrown");
        }
        dbConn = null;
    }

    @Test
    void getInstance() {
        //Arrange + Act
        try {
            dbConn = DBConnection.getInstance();
        } catch (DataAccessException e) {
            Assertions.fail("Exception thrown");
        }

        //Assert
        Assertions.assertNotNull(dbConn, "The instance cannot be null using the getInstance() method.");
    }

    @Test
    void getDBConn() {
        //Arrange
        try {
            dbConn = DBConnection.getInstance();
        } catch (DataAccessException e) {
            Assertions.fail("Exception thrown");
        }
        String expected = "Microsoft SQL Server";

        //Act
        Connection connection = dbConn.getDBConn();
        String actual = null;
        try {
            actual = connection.getMetaData().getDatabaseProductName();
        } catch (SQLException e) {
            Assertions.fail("Exception thrown");
        }

        //Assert
        Assertions.assertNotNull(actual, "Connection URL cannot be null.");
        Assertions.assertEquals(expected, actual, "Connection URL has to be equal to: " + expected);
    }

    @Test
    void closeConnection() {
        //Arrange
        try {
            dbConn = DBConnection.getInstance();
        } catch (DataAccessException e) {
            Assertions.fail("Exception thrown");
        }
        boolean result = dbConn.instanceIsNull();
        Assertions.assertFalse(result, "Before closing the connection, the DBConnection instance should be not null");

        //Act
        try {
            dbConn.closeConnection();
        } catch (DataAccessException e) {
            Assertions.fail("Exception thrown");
        }

        //Assert
        result = dbConn.instanceIsNull();
        Assertions.assertTrue(result, "After closing the connection, the DBConnection instance should be null");
    }
}