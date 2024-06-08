package com.project;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        final int windowWidth = 570;
        final int windowHeight = 350;

        UtilsViews.parentContainer.setStyle("-fx-font: 14 arial;");
        UtilsViews.addView(getClass(), "ViewLogin", "/assets/viewLogin.fxml");
        UtilsViews.addView(getClass(), "ViewConnecting", "/assets/viewConnecting.fxml");
        UtilsViews.addView(getClass(), "View0", "/assets/view0.fxml");
        UtilsViews.addView(getClass(), "ViewWinner", "/assets/viewWinner.fxml");

        Scene scene = new Scene(UtilsViews.parentContainer);
        
        stage.setScene(scene);
        stage.setTitle("Memory JavaFX");
        stage.setMinWidth(windowWidth);
        stage.setMinHeight(windowHeight);
        stage.setResizable(false);
        stage.show();

        // Add icon only if not Mac
        if (!System.getProperty("os.name").contains("Mac")) {
            Image icon = new Image("file:/icons/icon.png");
            stage.getIcons().add(icon);
        }
    }
}