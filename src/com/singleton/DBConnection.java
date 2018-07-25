package com.singleton;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class is for create Connection with Singleton Pattern.
 *
 * @author Omar Limbert Huanca Sanchez - AT-[06].
 * @version 1.0.
 */
public class DBConnection {

    /**
     * searchConnection, Type: SearchConnection, connection to  singleton of application.
     */
    private static DBConnection searchConnection;
    private PasswordGenerator generator = PasswordGenerator.getGenerator();
    private String user;
    private String password;
    private String db;
    private String host;
    private String url;
    private Connection conn;
    private Connection conn2;
    private Statement stm;
    private Statement stm2;

    /**
     * Constructor for SearchConnection.
     */
    private DBConnection() {

        // Initialize Connection
        this.initConnection();
    }

    /**
     * This method is for Initialize connection to SQLite.
     */
    private void initConnection(){
        this.stm = null;
        this.stm2 = null;
        this.user = PropertiesManager.getInstance().getUsername();
        this.password = generator.desencriptar(PropertiesManager.getInstance().getPassword());
        this.db = PropertiesManager.getInstance().getDB();
        this.host = PropertiesManager.getInstance().getServer();
        this.url = "jdbc:mysql://" + this.host + "/" + this.db+"?zeroDateTimeBehavior=convertToNull";

        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, password);
            conn2= DriverManager.getConnection(url, user, password);
            if (conn != null) {
                System.out.println("Conexión a base de datos " + url + " … Ok");
                stm = conn.createStatement();
            }

            if (conn2 != null) {
                System.out.println("Conexión a base de datos " + url + " … Ok");
                stm2 = conn2.createStatement();
            }

        } catch (SQLException ex) {
            System.out.println("Hubo un problema al intentar conectarse con la base de datos " + url);
            System.out.println(ex);
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
        }
    }

    /**
     * This method is for return instance or create new if this not exist with Singleton Pattern.
     *
     * @return SearchConnection, this is SearchConnection class for Singleton Pattern.
     */
    public static DBConnection getInstance() throws ClassNotFoundException, SQLException {
        if (searchConnection == null) {
            searchConnection = new DBConnection();
        }
        return searchConnection;
    }

    /**
     * This method is for return Connection to singleton.
     *
     * @return Connection, this is connection to singleton.
     */
    public Connection getConnection() {
        return conn;
    }
    public Statement getStm() {
        return stm;
    }
    public Statement getStm2(){
        return stm2;
    }
}
