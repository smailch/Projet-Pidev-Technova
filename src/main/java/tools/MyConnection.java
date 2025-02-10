package tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyConnection {
    private String url="jdbc:mysql://localhost:3306/db3a8";
    private String login="root";
    private String pwd="";
    private Connection cnx;


    String green = "\u001B[32m";
    String reset = "\u001B[0m";
    String red ="\u001B[31m";


    private static MyConnection instance;

    private MyConnection(){
        try {
            cnx=DriverManager.getConnection(url,login,pwd);
            System.out.print(green+"connection established 👍\n"+reset);
        } catch (SQLException e) {
            System.out.print(red+"Error, connection not established! ❌ /"+reset+e.getMessage());
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
