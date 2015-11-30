/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.io;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author swalter
 */
public class DB_Connector {
    private final String userName;    
    private final String password;
    private final String dbms;
    private final String serverName;
    private final String portNumber;
    private final String database;
    private Connection conn;
    
    public DB_Connector(Config config){
        this.userName = config.getUserName();
        this.password = config.getPassword();
        this.dbms = config.getDbms();
        this.serverName = config.getServerName();
        this.portNumber = config.getPortNumber();
        this.database = config.getDatabase();
    }
    
    public Connection connect() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        this.conn = null;
        Properties connectionProps = new Properties();
        connectionProps.put("user", this.userName);
        connectionProps.put("password", this.password);

        Class.forName("com.mysql.jdbc.Driver").newInstance();

        this.conn = DriverManager.getConnection(
                   "jdbc:" + this.dbms + "://" +
                   this.serverName +
                   ":" + this.portNumber + "/" + this.database,
                   connectionProps);

//        System.out.println("Connected to database");
        return conn;
    }

    public Connection getConn() {
        return conn;
    }

    public void closeConnection() throws SQLException {
        this.conn.close();
    }
}
