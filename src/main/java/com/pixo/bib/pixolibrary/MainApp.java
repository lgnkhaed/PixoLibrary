package com.pixo.bib.pixolibrary;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Créer un bouton simple
        Button btn = new Button("Cliquez moi !");
        btn.setOnAction(e -> System.out.println("Bonjour, JavaFX!"));

        // Créer un conteneur (StackPane)
        StackPane root = new StackPane();
        root.getChildren().add(btn);

        // Créer la scène
        Scene scene = new Scene(root, 300, 250);

        // Configurer le stage (fenêtre)
        primaryStage.setTitle("Mon Application JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);  // Lancer l'application JavaFX
    }
}
