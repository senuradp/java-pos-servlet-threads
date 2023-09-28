/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.syos.pos.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

/**
 *
 * @author senu2k
 */

public class DBConnection {
    
    private static final String URL = "jdbc:mysql://localhost:3306/syos";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    private static final int MAX_CONNECTIONS = 10;

    private Vector<Connection> connectionPool = new Vector<Connection>();

    // Private static instance variable
    private static DBConnection instance;

    // Private constructor to prevent direct instantiation
    private DBConnection() {
        initializeConnectionPool();
    }

    // Synchronized method to get an instance
    public synchronized static DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    private void initializeConnectionPool() {
        while (!checkIfConnectionPoolIsFull()) {
            connectionPool.addElement(createNewConnectionForPool());
        }
    }

    private synchronized boolean checkIfConnectionPoolIsFull() {
        final int MAX_POOL_SIZE = MAX_CONNECTIONS;
        return connectionPool.size() >= MAX_POOL_SIZE;
    }

    // Create a new database connection
    private Connection createNewConnectionForPool() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
//1
//    public synchronized Connection getConnectionFromPool() {
//        Connection connection = null;
//        if (connectionPool.size() > 0) {
//            connection = connectionPool.firstElement();
//            connectionPool.removeElementAt(0);
//        }
//        return connection;
//    }

    private boolean isValid(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeQuery("SELECT 1");
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }
// 3 - handle stale connections
    public synchronized Connection getConnectionFromPool() {
        Connection connection = null;
        while (connectionPool.size() > 0) {
            connection = connectionPool.firstElement();
            connectionPool.removeElementAt(0);
            try {
                if (isValid(connection)) {
                    return connection;
                } else {
                    connection.close();  // Close the stale connection
                }
            } catch (SQLException ex) {
                // Handle the exception or log it
                System.err.println("Error while validating or closing connection: " + ex.getMessage());
                ex.printStackTrace();
            } finally {
                connection = null;  // reset for the next loop iteration
            }
        }
        // If no valid connection is found in the pool, create a new one
        return createNewConnectionForPool();
    }

//2
//    public synchronized Connection getConnectionFromPool() {
//        Connection connection = null;
//        if (connectionPool.size() > 0) {
//            connection = connectionPool.firstElement();
//            connectionPool.removeElementAt(0);
//        } else {
//            connection = createNewConnectionForPool();
//        }
//        return connection;
//    }

    public synchronized void returnConnectionToPool(Connection connection) {
        connectionPool.addElement(connection);
    }
}
