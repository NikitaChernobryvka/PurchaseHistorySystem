module com.purchasehistorysystem {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.purchasehistorysystem to javafx.fxml;
    exports com.purchasehistorysystem;
}