package com.purchasehistorysystem;

import com.purchasehistorysystem.model.User;
import com.purchasehistorysystem.service.UserService;
import com.purchasehistorysystem.util.AuthTokenStorage;
import com.purchasehistorysystem.util.UserSessionUtils;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.sql.SQLException;

public class App extends Application {
    private static Scene scene;
    private final UserService userService = new UserService();

    @Override
    public void start(Stage stage) throws IOException {
        String authToken = AuthTokenStorage.loadToken();

        if (authToken != null) {
            try {
                User user = userService.findAuthToken(authToken);
                if (user != null) {
                    UserSessionUtils.setCurrentUser(user);
                    FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/com/purchasehistorysystem/templates/view/MainView.fxml"));
                    Parent root = fxmlLoader.load();
                    scene = new Scene(root);
                    stage.setTitle("Історія покупок");
                    stage.setScene(scene);
                    stage.setWidth(1550);
                    stage.setHeight(825);
                    stage.show();
                    return;
                }
            }

            catch (SQLException exception) {
                System.out.println("Помилка при завантаженні токену");
            }
        }

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/com/purchasehistorysystem/templates/view/RegisterView.fxml"));
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
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/com/purchasehistorysystem/templates/view/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }
}
