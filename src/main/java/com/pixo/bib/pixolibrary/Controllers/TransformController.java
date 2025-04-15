package com.pixo.bib.pixolibrary.Controllers;
import com.pixo.bib.pixolibrary.Model.Filters.*;
import com.pixo.bib.pixolibrary.Model.metaData.MetaDataManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class TransformController {
    private Image originalImage;
    private String currentImagePath;
    private final MetaDataManager metadataManager = new MetaDataManager();
    private String currentActiveFilter;
    @FXML private ImageView myImageView;

    // Initialisation , used in MainController
    public void setImage(Image image) {
        this.originalImage = image; // save the origin image when opening , to be able to reset after applying filters
        myImageView.setImage(image);
        currentActiveFilter = null; //take off the current Active filters
    }
    // method to save transformations
    @FXML
    private void saveTransformations(){metadataManager.saveMetadata();}

    // used in MainController , to set the apth of the originalImage while opening
    public void setImagePath(String path) {
        this.currentImagePath = path;
    }

    // Navigation
    @FXML
    private void goBackMainView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pixo/bib/pixolibrary/fxml/MainView.fxml"));
            Parent root = loader.load();

            MainController mainController = loader.getController();
            mainController.initialize(); //to reUpload Images and Metdatas

            Stage stage = (Stage) myImageView.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showAlert("Error", "Can't go back to the MainView");
        }
    }

    //Filters classes Created and use applyFilter method
    @FXML
    private void onMirrorClicked() {
        applyFilter(new FlipHorizontalFilter(), "Mirror");
    }
    @FXML
    private void onGrayscaleClicked() {
        applyFilter(new GrayscaleFilter(), "Grayscale");
    }
    @FXML
    private void onRGBSwapClicked() {
        applyFilter(new RGBSwapFilter(), "RGBSwap");
    }
    @FXML
    private void onSepiaClicked() {
        applyFilter(new SepiaFilter(), "Sepia");
    }
    @FXML
    private void onSobelClicked() {
        applyFilter(new SobelFilter(), "Sobel");
    }

    // method used in the FilterButton{to ensure No duplication in the Filters before applying}
    private void applyFilter(ImageFilter filter, String filterName) {
        // verify if the image is set
        if (myImageView.getImage() == null || originalImage == null) return;

        // verify if the filter has been applied before {avoids having the same tags many times} {use the hasTransformation method in metaDataManager}
        metadataManager.loadMetadata();
        boolean isAlreadyApplied = metadataManager.hasTransformation(currentImagePath, filterName);

        // If the Filter is already applied , we take off the filter
        /*
        if (isAlreadyApplied) {
            //myImageView.setImage(originalImage);
            //metadataManager.getTransformationsForImage(currentImagePath).remove(filterName);
        }
        // else we apply it
        else {
            Image result = filter.apply(originalImage);
            myImageView.setImage(result);
            metadataManager.addTransformation(currentImagePath, filterName);
        }*/
        Image result = filter.apply(originalImage);
        myImageView.setImage(result);
        if (!isAlreadyApplied) {metadataManager.addTransformation(currentImagePath, filterName);}
    }



    // method to display error message  on alert window
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}