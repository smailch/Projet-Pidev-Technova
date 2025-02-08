package tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyConnection {
    private String url="jdbc:mysql://localhost:3306/db3a8";
    private String login="root";
    private String pwd="";
    private Connection cnx;


    private static MyConnection instance;

    private MyConnection(){
        try {
           cnx=DriverManager.getConnection(url,login,pwd);
            System.out.print("connection established 👍");
        } catch (SQLException e) {
            System.out.print("Error, connection not established! ❌ /"+e.getMessage());
        }
    }

    public static MyConnection getInstance() {
        if(instance == null){
            instance= new MyConnection();
        }
        return instance;
    }

    public Connection getCnx() {
        return cnx;
    }
}
