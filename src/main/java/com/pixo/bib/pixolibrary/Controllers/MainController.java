package com.pixo.bib.pixolibrary.Controllers;
import com.pixo.bib.pixolibrary.Model.Secuirity.HashPassword;
import com.pixo.bib.pixolibrary.Model.Secuirity.ImageEncryptor;
//import com.pixo.bib.pixolibrary.Model.metaData.MetaDataManager;
import com.pixo.bib.pixolibrary.dao.ImageDAO;
import com.pixo.bib.pixolibrary.dao.TagDAO;
import com.pixo.bib.pixolibrary.dao.TransformationDAO;

import java.sql.Connection;
import java.sql.SQLException;
import com.pixo.bib.pixolibrary.Utilis.ImageConverter;
import com.pixo.bib.pixolibrary.database.DataBaseConnection;
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
import java.util.*;

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
   // private final MetaDataManager metadataManager = new MetaDataManager();
    private final ImageDAO imageDAO = new ImageDAO();
    private final TagDAO tagDAO = new TagDAO();
    private final TransformationDAO transformationDAO = new TransformationDAO();
    // intialize method for Mainview{ upload Images from uploads , upload metada From json file
    @FXML
    public void initialize() {
        try {
            DataBaseConnection.createTablesIfNotExist();
        } catch (SQLException e) {
            showAlert("Erreur DB", "Impossible de créer les tables");
        }
        setIsConnected(false);
        loadImagesFromUploads();


        if (!imagesList.isEmpty()) {
            showCurrentImage();
            updateTagsList();
        }
    }
    //method act the same as initialize just set the isConnected to true -- to be used in the return buttons
    public void initializedConnected(){
        setIsConnected(true);
        //metadataManager.loadMetadata();
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
        }catch(Exception e) {
            showAlert("Error", "cannot load image from ");
        }
    }


    @FXML
    private void uploadPicture() {
        if (!isConnected) {
            messageDisplay.setText("Veuillez vous connecter d'abord");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(myImageView.getScene().getWindow());
        if (selectedFile == null) return;

        try (Connection conn = DataBaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Début de transaction

            // copyfile
            File uploadsDir = new File("uploads");
            if (!uploadsDir.exists()) uploadsDir.mkdir();

            String extension = selectedFile.getName().substring(selectedFile.getName().lastIndexOf("."));
            String newFileName = "picture" + (uploadsDir.listFiles().length + 1) + extension;
            File destination = new File(uploadsDir, newFileName);
            Files.copy(selectedFile.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // insert to database
            String dbPath = "uploads/" + newFileName;
            int imageId = imageDAO.insertImage(dbPath); // <-- Clé ici

            // updating the interface
            imagesList.add(dbPath);
            currentIndex = imagesList.size() - 1;
            showCurrentImage();

            conn.commit(); // Validation
        } catch (IOException | SQLException e) {
            showAlert("Erreur", "Échec de l'upload : " + e.getMessage());
        }
    }


    //handling tags

    // add tag to Image

    @FXML
    private void handleAddTag() {
        if (!isConnected()) {
            messageDisplay.setText("Connectez-vous d'abord");
            return;
        }

        if (imagesList.isEmpty()) return;

        String tag = tagInput.getText().trim();
        if (tag.isEmpty()) return;

        try {
            String imagePath = imagesList.get(currentIndex);
            int imageId = imageDAO.getImageIdByPath(imagePath); // Lance une exception si non trouvé
            tagDAO.addTag(imageId, tag);
            updateTagsList();
            tagInput.clear();
        } catch (SQLException e) {
            showAlert("Erreur Base de Données", "Image non enregistrée : " + e.getMessage());
        }
    }

    // remove Tag to image
    @FXML
    private void handleRemoveTag() {
        if(isConnected()) {
            if (imagesList.isEmpty()) return;

            String selectedTag = tagsListView.getSelectionModel().getSelectedItem();
            if (selectedTag != null) {
                //metadataManager.removeTag(imagesList.get(currentIndex), selectedTag);
                int imageId = 0;
                try {
                    imageId = imageDAO.getImageIdByPath(imagesList.get(currentIndex));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                try {
                    tagDAO.removeTag(imageId, selectedTag);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                updateTagsList();
            }
        }else{
            messageDisplay.setText("You have to be logged in To be able to remove tag");
        }
    }

    // update Tags {display tags // nedded when add or remove Tags}
    private void updateTagsList() {
        if (!imagesList.isEmpty()) {
            try {
                int imageId = imageDAO.getImageIdByPath(imagesList.get(currentIndex));
                List<String> tags = tagDAO.getTags(imageId);
                tagsListView.getItems().setAll(tags);
            } catch (SQLException e) {
                // Image exists in filesystem but not in DB
                tagsListView.getItems().clear();
                if (isConnected()) {
                    // If connected, we should add it to DB
                    try {
                        String path = imagesList.get(currentIndex);
                        int imageId = imageDAO.insertImage(path);
                        tagsListView.getItems().clear();
                    } catch (SQLException ex) {
                        showAlert("Error", "Failed to register image in database");
                    }
                }
            }
        }
    }

    //Searching Tags

    // search with tag

    @FXML
    private void handleSearchByTag() {
        if(isConnected()) {
            String query = searchTagField.getText().trim();
            if (!query.isEmpty()) {
                try {
                    // Recherche dans les tags ET transformations
                    List<String> tagResults = tagDAO.searchImagesByTag(query);
                    List<String> transformationResults = transformationDAO.searchImagesByTransformation(query);

                    // Fusion des résultats sans doublons
                    Set<String> combinedResults = new LinkedHashSet<>();
                    combinedResults.addAll(tagResults);
                    combinedResults.addAll(transformationResults);

                    showSearchResultsWithPagination(new ArrayList<>(combinedResults));

                } catch (SQLException e) {
                    showAlert("Erreur", "Échec de la recherche : " + e.getMessage());
                }
            }
        } else {
            messageDisplay.setText("Connectez-vous pour rechercher");
        }
    }

    private void showSearchResultsWithPagination(List<String> imagePaths) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pixo/bib/pixolibrary/fxml/SearchResultsView.fxml"));
            Parent root = loader.load();

            SearchResultsController controller = loader.getController();
            //controller.initializeData(imagePaths, metadataManager);
            controller.initializeData(imagePaths, imageDAO, tagDAO, transformationDAO);

            Stage stage = (Stage) myImageView.getScene().getWindow();
            stage.setScene(new Scene(root));

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

