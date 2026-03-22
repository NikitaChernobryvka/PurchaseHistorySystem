package com.purchasehistorysystem;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

public class App extends Application {
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/com/purchasehistorysystem/templates/MainView.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Історія покупок");
        stage.setScene(scene);
        stage.setWidth(1550);
        stage.setHeight(825);
        stage.show();
    }
}
