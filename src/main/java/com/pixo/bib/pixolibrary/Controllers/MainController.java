package com.pixo.bib.pixolibrary.Controllers;
import com.pixo.bib.pixolibrary.Model.Secuirity.HashPassword;
import com.pixo.bib.pixolibrary.Model.Secuirity.ImageEncryptor;
import com.pixo.bib.pixolibrary.Model.metaData.MetaDataManager;
import com.pixo.bib.pixolibrary.Utilis.ImageConverter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MainController{

    // password Hardcoded but it will be chnaged
    private String myPassword = "mySecret";
    // variables for security
    private HashPassword hashPassword = new HashPassword();
    private ImageEncryptor imageEncryptor = new ImageEncryptor();

    // Variable to check is connected
    private Boolean isConnected;

    // getters and setter for my security classes
    public HashPassword getHashPassword() {return hashPassword;}
    public ImageEncryptor getImageEncryptor() {return imageEncryptor;}



    // getter and setter for isConnected
    public Boolean isConnected() {return isConnected;}
    public void setIsConnected(Boolean isConnected) {this.isConnected = isConnected;}


    // Composants FXML
    @FXML private ImageView myImageView;
    @FXML private TextField tagInput;
    @FXML private ListView<String> tagsListView;
    @FXML private TextField searchTagField;
    @FXML private Button loginButton;
    @FXML private Text messageDisplay;



    // Données
    private final ArrayList<String> imagesList = new ArrayList<>();
    private int currentIndex = 0;
    private final MetaDataManager metadataManager = new MetaDataManager();

    // intialize method for Mainview{ upload Images from uploads , upload metada From json file
    @FXML
    public void initialize() {
        setIsConnected(false);
        metadataManager.loadMetadata();
        loadImagesFromUploads();

        if (!imagesList.isEmpty()) {
            showCurrentImage();
            updateTagsList();
        }
    }

    //method act the same as initialize just set the isConnected to true -- to be used in the return buttons
    public void initializedConnected(){
        setIsConnected(true);
        metadataManager.loadMetadata();
        loadImagesFromUploads();

        if (!imagesList.isEmpty()) {
            showCurrentImage();
            updateTagsList();
        }
    }

    //upload pictures from Uploads directory
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

    // methods to Display the Image
    @FXML
    private void handleNextImage() {
            if (imagesList.isEmpty()) return;
            currentIndex = (currentIndex + 1) % imagesList.size();
            showCurrentImage();
            if(!isConnected()) {
                messageDisplay.setText("You have to be logged in first");
            }
    }

    private void showCurrentImage() {
        if (imagesList.isEmpty()) return;
        try {
            String path = imagesList.get(currentIndex);
            Image image = new Image(new File(path).toURI().toString());

            if(isConnected()) {
                myImageView.setImage(image);
                updateTagsList();
            }else{
                Image encyrptedImage = getImageEncryptor().encrypt(image,getHashPassword().sha256(myPassword));
                myImageView.setImage(encyrptedImage);
            }
        } catch (Exception e) {
            showAlert("Error", "cannot load image from ");
        }
    }


    @FXML
    private void uploadPicture() {
        if(isConnected) {
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
                    showAlert("Error", "Couldn't upload picture");
                }
            }
        }else{
            messageDisplay.setText("Can't upload pictures ! you have to be logged in first");
        }
    }


    //handling tags

    // add tag to Image
    @FXML
    private void handleAddTag() {
        if(isConnected()) {
            if (imagesList.isEmpty()) return;

            String tag = tagInput.getText().trim();
            if (!tag.isEmpty()) {
                metadataManager.addTag(imagesList.get(currentIndex), tag);
                updateTagsList();
                tagInput.clear();
            }
        }else{
            messageDisplay.setText("You have to be logged in To be able to add tag");
        }
    }

    // remove Tag to image
    @FXML
    private void handleRemoveTag() {
        if(isConnected()) {
            if (imagesList.isEmpty()) return;

            String selectedTag = tagsListView.getSelectionModel().getSelectedItem();
            if (selectedTag != null) {
                metadataManager.removeTag(imagesList.get(currentIndex), selectedTag);
                updateTagsList();
            }
        }else{
            messageDisplay.setText("You have to be logged in To be able to remove tag");
        }
    }

    // update Tags {display tags // nedded when add or remove Tags}
    private void updateTagsList() {
        if (!imagesList.isEmpty()) {
            List<String> tags = metadataManager.getTagsForImage(imagesList.get(currentIndex));
            tagsListView.getItems().setAll(tags);
        }
    }

    //Searching Tags

    // search with tag
    @FXML
    private void handleSearchByTag() {
        if(isConnected()) {
            String query = searchTagField.getText().trim();
            if (!query.isEmpty()) {
                List<String> matchingImages = metadataManager.searchByTagOrTransformation(query);
                showSearchResultsWithPagination(matchingImages); // Nouvelle méthode
            }
        }else{
            messageDisplay.setText("You have to be logged in To be able to search tag");
        }

    }

    private void showSearchResultsWithPagination(List<String> imagePaths) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pixo/bib/pixolibrary/fxml/SearchResultsView.fxml"));
            Parent root = loader.load();

            SearchResultsController controller = loader.getController();
            controller.initializeData(imagePaths, metadataManager);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'afficher les résultats : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // display the results
    private void showSearchResults(List<String> imagePaths) {
        // Version simplifiée sans dépendances externes
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Searching Results");
        alert.setHeaderText("Images found (" + imagePaths.size() + "):");

        String content = String.join("\n", imagePaths);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // go to transformation panel
    @FXML
    private void openTransformationPanel() {
        if(isConnected()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pixo/bib/pixolibrary/fxml/TransformView.fxml"));
                Parent root = loader.load();

                TransformController controller = loader.getController();
                controller.setImage(myImageView.getImage());
                controller.setImagePath(imagesList.get(currentIndex));

                Stage stage = (Stage) myImageView.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException e) {
                showAlert("Eroor", "Can't open transform panel");
            }
        }else{
            messageDisplay.setText("You have to be logged in To be able to open transform panel");
        }
    }

    // method to display error message  on alert window
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    // login method
    @FXML
    private void login(){
        if(isConnected()){
            messageDisplay.setText("You are already logged in");
        }else{
           enterPasswordAlert();
        }
    }

    private boolean validatePassword(String inputPassword) {
        // here i am hardcoding the password , to be changed after
        String correctPassword = myPassword; // Replace with your logic
        return inputPassword.equals(correctPassword);
    }


    // function to popup a login page
    private void enterPasswordAlert(){        // Create a dialog window
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Access Required");
        dialog.setHeaderText("Enter your access password");

        // Set the button types
        ButtonType submitButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);

        // Create the password field
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        // Layout for the dialog content
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Password:"), 0, 0);
        grid.add(passwordField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        // Enable/Disable submit button depending on whether a password was entered
        Node submitButton = dialog.getDialogPane().lookupButton(submitButtonType);
        submitButton.setDisable(true);

        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            submitButton.setDisable(newValue.trim().isEmpty());
        });

        // Convert the result to the password string when the submit button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == submitButtonType) {
                return passwordField.getText();
            }
            return null;
        });

        // Show dialog and wait for result
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(password -> {
            // Handle password validation here
            if (validatePassword(password)) {
                //System.out.println("Password correct! Access granted.");
                setIsConnected(true);
                messageDisplay.setText("You are logged in");
                showCurrentImage();
            } else {
                System.out.println("Wrong password.");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Access Denied");
                alert.setHeaderText(null);
                alert.setContentText("The password you entered is incorrect.");
                alert.showAndWait();
            }
        });

    }


}

