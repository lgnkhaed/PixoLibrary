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

    // Initialisation
    public void setImage(Image image) {
        this.originalImage = image; // Sauvegarde l'original
        myImageView.setImage(image);
        currentActiveFilter = null; // Réinitialise le filtre actif
    }

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
            mainController.initialize(); // Recharge les images et métadonnées

            Stage stage = (Stage) myImageView.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de retourner à l'accueil");
        }
    }

    // Transformations
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

    private void applyFilter(ImageFilter filter, String filterName) {
        if (myImageView.getImage() == null || originalImage == null) return;

        metadataManager.loadMetadata();
        boolean isAlreadyApplied = metadataManager.hasTransformation(currentImagePath, filterName);

        // Si le filtre est déjà appliqué (dans metadata.json), on le désactive
        if (isAlreadyApplied) {
            myImageView.setImage(originalImage);
            metadataManager.getTransformationsForImage(currentImagePath).remove(filterName);
        }
        // Sinon, on l'applique
        else {
            Image result = filter.apply(originalImage);
            myImageView.setImage(result);
            metadataManager.addTransformation(currentImagePath, filterName);
        }

        metadataManager.saveMetadata();
    }

    // Utilitaires
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}