/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author fabiankaupmann
 */
public class Connector {

    private String userName;    
    private String password;
    private String dbms;
    private String serverName;
    private String portNumber;
    private String database;
    private Connection conn;
    
    public Connector(){
        this.userName = "";
        this.password = "";
        this.dbms = "mysql";
        this.serverName = "localhost";
        this.portNumber = "";
        this.database = "";
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

        System.out.println("Connected to database");
        return conn;
    }

    public Connection getConn() {
        return conn;
    }

    
}
