package tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Myconnection {
    private String url = "jdbc:mysql://localhost:3306/db3a8";
    private String login = "root";
    private String pwd = "";
    private Connection cnx;
    //Singleton
    private static Myconnection instance;

    //Singleton(private)
    private Myconnection() {
        try {
            cnx=DriverManager.getConnection(url, login, pwd);
            System.out.println("si  Smail , connection  /");
        } catch (SQLException e) {
            System.out.println("si Smail Error, connection failed ! /" + e.getMessage());
        }
    }

    public Connection getCnx() {
        return cnx;
    }

    //Singleton
    public static Myconnection getInstance() {
        if (instance == null) {
            instance = new Myconnection();
        }
        return instance;
    }
}

