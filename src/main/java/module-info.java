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

    opens RolDePagos to javafx.fxml;
    exports RolDePagos;
}