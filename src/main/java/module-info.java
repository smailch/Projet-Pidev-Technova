module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.sql;
    requires org.apache.pdfbox; // Add this line for PDFBox
    requires java.desktop;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires jdk.jsobject;
    requires com.dlsc.gmapsfx;
    requires javafx.swing;
    requires jxmapviewer2;
    requires com.sothawo.mapjfx;
    requires org.json;

    opens com.example.demo to javafx.fxml;
    opens com.example.demo.controllers to javafx.fxml;
    opens com.example.demo.models to javafx.base;

    exports com.example.demo;
    exports com.example.demo.controllers to javafx.fxml;
    exports com.example.demo.models to javafx.base;

}