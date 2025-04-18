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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchResultsController {

    @FXML private ImageView currentImageView;
    @FXML private Label pageLabel;
    @FXML private Label imageInfoLabel;

    private List<String> imagePaths;
    private int currentIndex = 0;
    private List<Image> images;

    //data Access class
    private ImageDAO imageDAO;
    private TagDAO tagDAO;
    private TransformationDAO transformationDAO;

    public void initializeData(List<String> imagePaths, ImageDAO imageDAO, TagDAO tagDAO, TransformationDAO transformationDAO) {
        this.imagePaths = imagePaths;
        this.imageDAO = imageDAO;
        this.tagDAO = tagDAO;
        this.transformationDAO = transformationDAO;
        this.images = uploadingImagesToBeDisplayed(imagePaths);

        if (imagePaths == null || imagePaths.isEmpty()) {
            showNoResults();
        } else {
            showCurrentImage();
        }
    }

    private List<Image> uploadingImagesToBeDisplayed(List<String> imagesPaths){
           List<Image> allImagesToBeDisplayed = new ArrayList<>();
           for(String path : imagesPaths){
               Image img = new Image(new File(path).toURI().toString());
               List<Image> imgForOnePath = applySavedFilters(path,img);
               allImagesToBeDisplayed.addAll(imgForOnePath);
           }
           return allImagesToBeDisplayed;
    }

    private List<Image> applySavedFilters(String imagePath, Image originalImage) {

        List<Image> imagesToBeRetuned = new ArrayList<Image>();
        imagesToBeRetuned.add(originalImage);

        try {
            int imageId = imageDAO.getImageIdByPath(imagePath);
            List<String> transformations = transformationDAO.getTransformations(imageId);

            for (String filterName : transformations) {
                switch (filterName) {
                    case "Sepia":
                        imagesToBeRetuned.add(new SepiaFilter().apply(originalImage));
                        break;
                    case "Sobel":
                        imagesToBeRetuned.add(new SobelFilter().apply(originalImage));
                        break;
                    case "RGBSwap":
                        imagesToBeRetuned.add(new RGBSwapFilter().apply(originalImage));
                        break;
                    case "BlackWhite":
                        imagesToBeRetuned.add(new GrayscaleFilter().apply(originalImage));
                        break;
                    case "Mirror":
                        imagesToBeRetuned.add(new FlipHorizontalFilter().apply(originalImage));
                        break;
                    case "RotateRight":
                        imagesToBeRetuned.add(new RotateRightFilter().apply(originalImage));
                        break;
                    case "RotateLeft":
                        imagesToBeRetuned.add(new RotateLeftFilter().apply(originalImage));
                        break;
                }
            }
        } catch (SQLException e) {
            showError("Erreur de chargement des filtres");
        }
        return imagesToBeRetuned;
    }

    private void showCurrentImage() {
        try {
            currentImageView.setImage(this.images.get(currentIndex));
            updatePageInfo();
            clearError();
        } catch (Exception e) {
            showError("Erreur de chargement de l'image: " + e.getMessage());
        }
    }


    private void updatePageInfo() {
        pageLabel.setText(String.format("Image %d/%d", currentIndex + 1, images.size()));
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
        if (currentIndex < images.size() - 1) {
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
            //stage.setMaximized(true);
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