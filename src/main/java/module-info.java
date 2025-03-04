module java {
    requires org.json;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.web;
    requires javafx.controls;
    requires jakarta.mail;
    requires spring.security.crypto;
    requires jjwt;
    requires java.desktop;
    requires kernel;
    requires layout;


    exports controllers;
    opens controllers to javafx.fxml;
}