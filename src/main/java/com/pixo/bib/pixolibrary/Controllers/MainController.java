package com.pixo.bib.pixolibrary.Controllers;
import com.pixo.bib.pixolibrary.Model.metaData.MetaDataManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class MainController{

    // Composants FXML
    @FXML private ImageView myImageView;
    @FXML private TextField tagInput;
    @FXML private ListView<String> tagsListView;
    @FXML private TextField searchTagField;

    // Données
    private final ArrayList<String> imagesList = new ArrayList<>();
    private int currentIndex = 0;
    private final MetaDataManager metadataManager = new MetaDataManager();

    @FXML
    public void initialize() {
        metadataManager.loadMetadata();
        loadImagesFromUploads();

        if (!imagesList.isEmpty()) {
            showCurrentImage();
            updateTagsList();
        }
    }

    // Méthodes de base ---------------------------------------------------------------

    private void loadImagesFromUploads() {
        imagesList.clear();
        File uploadsDir = new File("uploads");

        if (uploadsDir.exists()) {
            File[] files = uploadsDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().matches("picture\\d+\\..+")) {
                        imagesList.add("uploads/" + file.getName());
                    }
                }
            }
        }
    }

    private void showCurrentImage() {
        if (imagesList.isEmpty()) return;

        try {
            String path = imagesList.get(currentIndex);
            Image image = new Image(new File(path).toURI().toString());
            myImageView.setImage(image);
            updateTagsList();
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger l'image");
        }
    }

    @FXML
    private void handleNextImage() {
        if (imagesList.isEmpty()) return;
        currentIndex = (currentIndex + 1) % imagesList.size();
        showCurrentImage();
    }

    @FXML
    private void uploadPicture() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(myImageView.getScene().getWindow());
        if (selectedFile != null) {
            try {
                File uploadsDir = new File("uploads");
                if (!uploadsDir.exists()) uploadsDir.mkdir();

                String extension = selectedFile.getName().substring(selectedFile.getName().lastIndexOf("."));
                String newFileName = "picture" + (uploadsDir.listFiles().length + 1) + extension;
                File destination = new File(uploadsDir, newFileName);

                Files.copy(selectedFile.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
                imagesList.add("uploads/" + newFileName);
                currentIndex = imagesList.size() - 1;
                showCurrentImage();
            } catch (IOException e) {
                showAlert("Erreur", "Échec de l'upload");
            }
        }
    }

    // Gestion des tags ----------------------------------------------------------------

    @FXML
    private void handleAddTag() {
        if (imagesList.isEmpty()) return;

        String tag = tagInput.getText().trim();
        if (!tag.isEmpty()) {
            metadataManager.addTag(imagesList.get(currentIndex), tag);
            updateTagsList();
            tagInput.clear();
        }
    }

    @FXML
    private void handleRemoveTag() {
        if (imagesList.isEmpty()) return;

        String selectedTag = tagsListView.getSelectionModel().getSelectedItem();
        if (selectedTag != null) {
            metadataManager.removeTag(imagesList.get(currentIndex), selectedTag);
            updateTagsList();
        }
    }

    private void updateTagsList() {
        if (!imagesList.isEmpty()) {
            List<String> tags = metadataManager.getTagsForImage(imagesList.get(currentIndex));
            tagsListView.getItems().setAll(tags);
        }
    }

    // Recherche par tags --------------------------------------------------------------

    @FXML
    private void handleSearchByTag() {
        String tag = searchTagField.getText().trim();
        if (!tag.isEmpty()) {
            List<String> matchingImages = metadataManager.getImagesByTag(tag);

            if (!matchingImages.isEmpty()) {
                showSearchResults(matchingImages);
            } else {
                showAlert("Aucun résultat", "Aucune image trouvée avec le tag: " + tag);
            }
        }
    }

    private void showSearchResults(List<String> imagePaths) {
        // Version simplifiée sans dépendances externes
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Résultats de recherche");
        alert.setHeaderText("Images trouvées (" + imagePaths.size() + "):");

        String content = String.join("\n", imagePaths);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Navigation ----------------------------------------------------------------------

    @FXML
    private void openTransformationPanel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pixo/bib/pixolibrary/fxml/TransformView.fxml"));
            Parent root = loader.load();

            TransformController controller = loader.getController();
            controller.setImage(myImageView.getImage());
            controller.setImagePath(imagesList.get(currentIndex));

            Stage stage = (Stage) myImageView.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showAlert("Erreur", "Échec de l'ouverture des transformations");
        }
    }

    // Utilitaires ---------------------------------------------------------------------

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}