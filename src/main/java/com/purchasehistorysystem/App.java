package com.purchasehistorysystem;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

public class App extends Application {
    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/com/purchasehistorysystem/templates/RegisterView.fxml"));
        Parent root = fxmlLoader.load();
        scene = new Scene(root);
        stage.setTitle("Історія покупок");
        stage.setScene(scene);
        stage.setWidth(1550);
        stage.setHeight(825);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFxml(fxml));
    }

    public static Parent loadFxml(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/com/purchasehistorysystem/templates/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }
}
