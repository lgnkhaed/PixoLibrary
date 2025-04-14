package com.pixo.bib.pixolibrary.Controllers;


import com.pixo.bib.pixolibrary.Model.Filters.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class TransformController {
     // attributes to manipulate the transformations
     private Image originalImage;
     private boolean grayscaleApplied = false;
     private boolean rgbSwapApplied = false;
     private boolean sepiaApplied = false;
        private boolean sobelApplied = false;


    @FXML private ImageView myImageView;

    //to use it in the MainController pour afficher l'image choisie
    @FXML
    public void setImage(Image image) {
        myImageView.setImage(image);
        originalImage = image;
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


    // transformation Functions
    @FXML
    private void onMirrorClicked() {
        if (myImageView.getImage() != null) {
            ImageFilter filter = new flipHorizontalFilter();
            Image result = filter.apply(myImageView.getImage());
            myImageView.setImage(result);
        }
    }


    @FXML
    private void onGrayscaleClicked() {
        if (myImageView.getImage() == null) return;

        if (!grayscaleApplied) {
            ImageFilter filter = new GrayscaleFilter();
            Image result = filter.apply(myImageView.getImage());
            myImageView.setImage(result);
            grayscaleApplied = true;
        } else {
            myImageView.setImage(originalImage);
            grayscaleApplied = false;
        }
    }


    @FXML
    private void onRGBSwapClicked() {
        if (myImageView.getImage() == null) return;

        if (!rgbSwapApplied) {
            ImageFilter filter = new RGBSwapFilter();
            Image result = filter.apply(myImageView.getImage());
            myImageView.setImage(result);
            rgbSwapApplied = true;
        } else {
            myImageView.setImage(originalImage);
            rgbSwapApplied = false;
        }
    }

    @FXML
    private void onSepiaClicked() {
        if (myImageView.getImage() == null) return;

        if (!sepiaApplied) {
            ImageFilter filter = new SepiaFilter();
            Image result = filter.apply(myImageView.getImage());
            myImageView.setImage(result);
            sepiaApplied = true;
        } else {
            myImageView.setImage(originalImage);
            sepiaApplied = false;
        }
    }

    @FXML
    private void onSobelClicked() {
        if (myImageView.getImage() == null) return;

        if (!sobelApplied) {
            ImageFilter filter = new SobelFilter();
            Image result = filter.apply(myImageView.getImage());
            myImageView.setImage(result);
            sobelApplied = true;
        } else {
            myImageView.setImage(originalImage);
            sobelApplied = false;
        }
    }

}
