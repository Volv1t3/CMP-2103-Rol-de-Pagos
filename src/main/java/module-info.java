module RolDePagosMain {
    requires java.naming;
    requires java.xml.bind;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.media;
    requires javafx.web;
    requires java.sql;
    requires json.simple;
    requires java.desktop;
    requires com.sun.xml.bind;

    opens RolDePagos to javafx.fxml, java.xml.bind;
    opens EmployeeAbstraction to java.xml.bind;
    exports RolDePagos;
}