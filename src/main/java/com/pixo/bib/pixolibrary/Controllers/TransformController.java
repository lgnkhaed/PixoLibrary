package com.pixo.bib.pixolibrary.Controllers;
import com.pixo.bib.pixolibrary.Model.Filters.*;
import com.pixo.bib.pixolibrary.Model.Secuirity.HashPassword;
import com.pixo.bib.pixolibrary.Model.Secuirity.ImageEncryptor;
import com.pixo.bib.pixolibrary.Model.metaData.MetaDataManager;
import com.pixo.bib.pixolibrary.dao.ImageDAO;
import com.pixo.bib.pixolibrary.dao.TransformationDAO;
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
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class TransformController {

    private Image originalImage;
    private String currentImagePath;
    // private final MetaDataManager metadataManager = new MetaDataManager();
    private final ImageDAO imageDAO = new ImageDAO();
    private final TransformationDAO transformationDAO = new TransformationDAO();
    private String currentActiveFilter;
    private String seed_StringEncrypt = null;
    private String seed_StringDecrypt=null;


    @FXML private ImageView myImageView;

    // Initialisation , used in MainController
    public void setImage(Image image) {
        this.originalImage = image; // save the origin image when opening , to be able to reset after applying filters
        myImageView.setImage(image);
        currentActiveFilter = null; //take off the current Active filters
    }
    // method to save transformations
    @FXML
    private void saveTransformations(){}

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
            mainController.initializedConnected();//to reUpload Images and Metdatas

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
        applyFilter(new GrayscaleFilter(), "BlackWhite");
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
    @FXML
    private void rotateRight(){
        applyFilter(new RotateRightFilter(), "RotateRight");
    }

    @FXML
    private void rotateLeft(){
        applyFilter(new RotateLeftFilter(), "RotateLeft");
    }

    // method used in the FilterButton{to ensure No duplication in the Filters before applying}
    private void applyFilter(ImageFilter filter, String filterName) {
        // verify if the image is set
        if (myImageView.getImage() == null || originalImage == null) return;

        // verify if the filter has been applied before {avoids having the same tags many times} {use the hasTransformation method in metaDataManager}
        // metadataManager.loadMetadata();
        // boolean isAlreadyApplied = metadataManager.hasTransformation(currentImagePath, filterName);
        int imageId = 0;
        try {
            imageId = imageDAO.getImageIdByPath(currentImagePath);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        boolean isAlreadyApplied = false;
        try {
            isAlreadyApplied = transformationDAO.getTransformations(imageId).contains(filterName);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Nouvelle implémentation corrigée
        if(filterName.equals("RotateRight") || filterName.equals("RotateLeft")){
            // Pour les rotations : appliquer sur l'image actuelle (permet des rotations multiples)
            Image result_img = filter.apply(myImageView.getImage());
            myImageView.setImage(result_img);

            if (!isAlreadyApplied) {
                try {
                    transformationDAO.addTransformation(imageId, filterName);
                } catch (SQLException e) {
                    showAlert("Erreur", "Échec de l'enregistrement de la transformation");
                }
            }
        } else {
            // Pour les autres filtres : toujours utiliser l'original
            Image result_img = filter.apply(originalImage);
            myImageView.setImage(result_img);

            if (!isAlreadyApplied) {
                try {
                    transformationDAO.addTransformation(imageId, filterName);
                } catch (SQLException e) {
                    showAlert("Erreur", "Échec de l'enregistrement de la transformation");
                }
            }
        }
    }



    // method to display error message  on alert window
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FXML
    private void encryptImage(){
        try{
            // verifying if the Image has "ENCRYPT" as Transformation or Not
            int idImageToEncrypt = imageDAO.getImageIdByPath(currentImagePath);
            if ((!transformationDAO.getTransformations(idImageToEncrypt).contains("ENCRYPT"))){
                // if the Image is not Encrypted
                enterPasswordEncryptAlert();
                if(seed_StringEncrypt != null){
                    //System.out.println("Here i am in the main , this the password to encrypt : " + seed_StringEncrypt);
                    // encrypting the image and displaying it
                    byte[] seed = HashPassword.sha256(seed_StringEncrypt);
                    //System.out.println("Here is the encrypted seed : " + java.util.Arrays.toString(seed));
                    Image imageEncrypted = ImageEncryptor.encrypt(originalImage, seed);
                    myImageView.setImage(imageEncrypted);

                    // setting seed_String to null after finishing
                    seed_StringEncrypt = null;
                    //Adding "ENCRYPT" transformation
                    transformationDAO.addTransformation(idImageToEncrypt, "ENCRYPT");
                }else{
                    showAlert("Error","Encryption didn't worked , Password not entered");
                }
            }else{
                showAlert("Error","Image already encrypted");
            }
        } catch(SQLException eSql){
            showAlert("ERROR","SQL Error while encrypting");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @FXML
    private void decryptImage(){
        try{
            int idImageToDecrypt = imageDAO.getImageIdByPath(currentImagePath);
            if(transformationDAO.getTransformations(idImageToDecrypt).contains("ENCRYPT")){
                   enterPasswordDecryptAlert();
                   if(seed_StringDecrypt != null){
                       //System.out.println("Here i am in the main , this the password to decrypt : " + seed_StringDecrypt);
                       byte[] seed = HashPassword.sha256(seed_StringDecrypt);
                       //System.out.println("Here is the decrypted seed : " + java.util.Arrays.toString(seed));
                       Image imageDecrypted = ImageEncryptor.decrypt(myImageView.getImage(), seed);
                       myImageView.setImage(imageDecrypted);
                       //removing "ENCRYPT" transformation
                       transformationDAO.deleteTransformation(idImageToDecrypt, "ENCRYPT");
                       seed_StringDecrypt = null;
                   }else{
                       showAlert("Error","Decryption didn't worked , Password not entered");
                   }
            }else{
                showAlert("Error","Image already Decrypted");
            }

        }catch (SQLException eSql){
            showAlert("Error", "SQL Error while decrypting");
        } catch (Exception e) {
            showAlert(e.getMessage(), "Can't decrypt image");
        }


    }



    // function to popup when encrypting picture
    private void enterPasswordEncryptAlert(){        // Create a dialog window
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("ENCRYPTION ");
        dialog.setHeaderText("Encryption Action needs a Password");

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
               seed_StringEncrypt = password ;
            }
        );

    }

    // method to popup when decrypting picture
    private void enterPasswordDecryptAlert(){
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("DERYPTION ");
        dialog.setHeaderText("enter the password");

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
                    seed_StringDecrypt = password ;
                }
        );
    }
}