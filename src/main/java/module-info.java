module com.purchasehistorysystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires bcrypt;


    opens com.purchasehistorysystem to javafx.fxml;
    exports com.purchasehistorysystem;

    opens com.purchasehistorysystem.controller to javafx.fxml;
    exports com.purchasehistorysystem.controller;
}