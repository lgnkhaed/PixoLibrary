package com.pixo.bib.pixolibrary.Controllers;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.IOException;

public class TransformController {

     @FXML private ImageView myImageView;

    //to use it in the MainController pour afficher l'image choisie
    @FXML
    public void setImage(Image image) {
          myImageView.setImage(image);
    }

    //btn to go back home
    @FXML
    private void goBackMainView() throws IOException {
        try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pixo/bib/pixolibrary/fxml/MainView.fxml"));
                Parent root = loader.load();
                //to execute initialize()
                MainController mainController = loader.getController();
                mainController.initialize();

                //
                Stage stage = (Stage) myImageView.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
        } catch (IOException e) {
                e.printStackTrace();
            }
    }


}
