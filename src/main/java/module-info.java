module com.purchasehistorysystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;


    opens com.purchasehistorysystem to javafx.fxml;
    exports com.purchasehistorysystem;
}