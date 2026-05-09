module com.purchasehistorysystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires bcrypt;
    requires java.keyring;


    opens com.purchasehistorysystem to javafx.fxml;
    exports com.purchasehistorysystem;

    opens com.purchasehistorysystem.controller to javafx.fxml;
    exports com.purchasehistorysystem.controller;
}