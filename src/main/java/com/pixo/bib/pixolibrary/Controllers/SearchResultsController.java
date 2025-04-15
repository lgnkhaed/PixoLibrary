package com.pixo.bib.pixolibrary.Controllers;

import com.pixo.bib.pixolibrary.Model.Filters.*;
import com.pixo.bib.pixolibrary.Model.metaData.MetaDataManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import java.io.File;
import java.util.List;

public class SearchResultsController {

    @FXML private ImageView currentImageView;
    @FXML private Label pageLabel;
    @FXML private Label imageInfoLabel;
    @FXML private VBox metadataContainer;

    private List<String> imagePaths;
    private int currentIndex = 0;
    private MetaDataManager metadataManager;

    public void initialize() {
        // Initialisation optionnelle si nécessaire
    }

    public void initializeData(List<String> imagePaths, MetaDataManager metadataManager) {
        this.imagePaths = imagePaths;
        this.metadataManager = metadataManager;

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
            List<String> transformations = metadataManager.getTransformationsForImage(imagePath);
            Image processedImage = originalImage;

            for (String filterName : transformations) {
                switch (filterName) {
                    case "Sepia":
                        processedImage = new SepiaFilter().apply(processedImage);
                        break;
                    case "BlackWhite":
                    case "Grayscale":
                        processedImage = new GrayscaleFilter().apply(processedImage);
                        break;
                    case "Mirror":
                        processedImage = new FlipHorizontalFilter().apply(processedImage);
                        break;
                    case "RGBSwap":
                        processedImage = new RGBSwapFilter().apply(processedImage);
                        break;
                    case "Sobel":
                        processedImage = new SobelFilter().apply(processedImage);
                        break;
                }
            }
            return processedImage;
        } catch (Exception e) {
            showError("Erreur d'application des filtres");
            return originalImage;
        }
    }

    private void displayMetadata(String imagePath) {
        metadataContainer.getChildren().clear();

        try {
            List<String> tags = metadataManager.getTagsForImage(imagePath);
            if (!tags.isEmpty()) {
                Label tagsLabel = new Label("Tags: " + String.join(", ", tags));
                metadataContainer.getChildren().add(tagsLabel);
            }

            List<String> transformations = metadataManager.getTransformationsForImage(imagePath);
            if (!transformations.isEmpty()) {
                Label filtersLabel = new Label("Filtres: " + String.join(", ", transformations));
                metadataContainer.getChildren().add(filtersLabel);
            }
        } catch (Exception e) {
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
}