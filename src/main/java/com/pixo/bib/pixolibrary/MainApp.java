package com.pixo.bib.pixolibrary;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Load the FXML file to get the root element (which should be a Parent, like a Pane)
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/pixo/bib/pixolibrary/fxml/MainView.fxml")));
        // Create a Scene with the root element
        Scene scene = new Scene(root);
        stage.setTitle("Pixo Library");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
