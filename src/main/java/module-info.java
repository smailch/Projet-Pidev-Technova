module com.example.demo {
    requires org.json;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.web;
    requires javafx.controls;
    requires jakarta.mail;
    requires spring.security.crypto;
    requires jjwt;
    requires java.desktop;

    opens com.example.demo to javafx.fxml;
    exports com.example.demo;
}