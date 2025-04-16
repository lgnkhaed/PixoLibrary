package com.pixo.bib.pixolibrary.Controllers;

import com.pixo.bib.pixolibrary.dao.ImageDAO;
import com.pixo.bib.pixolibrary.dao.TagDAO;
import com.pixo.bib.pixolibrary.dao.TransformationDAO;
import com.pixo.bib.pixolibrary.Model.Filters.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class SearchResultsController {

    @FXML private ImageView currentImageView;
    @FXML private Label pageLabel;
    @FXML private Label imageInfoLabel;
    @FXML private VBox metadataContainer;

    private List<String> imagePaths;
    private int currentIndex = 0;

    // Remplacement de MetaDataManager par les DAOs
    private ImageDAO imageDAO;
    private TagDAO tagDAO;
    private TransformationDAO transformationDAO;

    public void initializeData(List<String> imagePaths, ImageDAO imageDAO,
                               TagDAO tagDAO, TransformationDAO transformationDAO) {
        this.imagePaths = imagePaths;
        this.imageDAO = imageDAO;
        this.tagDAO = tagDAO;
        this.transformationDAO = transformationDAO;

        if (imagePaths == null || imagePaths.isEmpty()) {
            showNoResults();
        } else {
            showCurrentImage();
        }
    }

    private void showCurrentImage() {
        try {
            String currentPath = imagePaths.get(currentIndex);
            Image originalImage = new Image(new File(currentPath).toURI().toString());
            currentImageView.setImage(applySavedFilters(currentPath, originalImage));
            updatePageInfo();
            displayMetadata(currentPath);
            clearError();
        } catch (Exception e) {
            showError("Erreur de chargement de l'image: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Image applySavedFilters(String imagePath, Image originalImage) {
        try {
            int imageId = imageDAO.getImageIdByPath(imagePath);
            List<String> transformations = transformationDAO.getTransformations(imageId);

            Image processedImage = originalImage;
            for (String filterName : transformations) {
                switch (filterName) {
                    case "Sepia":
                        processedImage = new SepiaFilter().apply(processedImage);
                        break;
                    // ... autres cas identiques ...
                }
            }
            return processedImage;
        } catch (SQLException e) {
            showError("Erreur de chargement des filtres");
            return originalImage;
        }
    }

    private void displayMetadata(String imagePath) {
        metadataContainer.getChildren().clear();

        try {
            int imageId = imageDAO.getImageIdByPath(imagePath);

            // Récupération des tags avec TagDAO
            List<String> tags = tagDAO.getTags(imageId);
            if (!tags.isEmpty()) {
                Label tagsLabel = new Label("Tags: " + String.join(", ", tags));
                metadataContainer.getChildren().add(tagsLabel);
            }

            // Récupération des transformations avec TransformationDAO
            List<String> transformations = transformationDAO.getTransformations(imageId);
            if (!transformations.isEmpty()) {
                Label filtersLabel = new Label("Filtres: " + String.join(", ", transformations));
                metadataContainer.getChildren().add(filtersLabel);
            }

        } catch (SQLException e) {
            showError("Erreur de chargement des métadonnées");
        }
    }

    private void updatePageInfo() {
        pageLabel.setText(String.format("Image %d/%d", currentIndex + 1, imagePaths.size()));
    }

    private void showNoResults() {
        pageLabel.setText("Aucun résultat");
        showError("Aucune image trouvée");
    }

    private void showError(String message) {
        if (imageInfoLabel != null) {
            imageInfoLabel.setText(message);
            imageInfoLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private void clearError() {
        if (imageInfoLabel != null) {
            imageInfoLabel.setText("");
        }
    }

    @FXML
    private void previousPage() {
        if (currentIndex > 0) {
            currentIndex--;
            showCurrentImage();
        }
    }

    @FXML
    private void nextPage() {
        if (currentIndex < imagePaths.size() - 1) {
            currentIndex++;
            showCurrentImage();
        }
    }
    //go back to main
    @FXML
    private  void goBackToMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pixo/bib/pixolibrary/fxml/MainView.fxml"));
            Parent root = loader.load();

            MainController mainController = loader.getController();
            mainController.initializedConnected();//to reUpload Images and Metdatas

            Stage stage = (Stage) currentImageView.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showAlert("Error", "Can't go back to the MainView");
        }
    }

    // method to display error message  on alert window
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

}