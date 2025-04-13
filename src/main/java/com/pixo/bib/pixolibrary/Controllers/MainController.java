package com.pixo.bib.pixolibrary.Controllers;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainController {

    @FXML private ImageView myImageView;
    @FXML private Button changeImageButton;
    @FXML private Button myTransformButton;
    @FXML private Button myUploadButton;


    //the list of the Images to display and
    private final ArrayList<String> imagesList = new ArrayList<>();
    private int currentIndex=0;

    //to switch to scenes
    private Stage stage;
    private Scene scene;
    private Parent root;

    //the first setup of the Mainview
    @FXML
    public void initialize() {
        // Upload the pictures
        imagesList.add("res:/com/pixo/bib/pixolibrary/Images/picture1.jpg");
        imagesList.add("res:/com/pixo/bib/pixolibrary/Images/picture2.jpg");
        //imagesList.add("/resources/com/pixo/bib/pixolibrary/Images/picture2.jpg");

        //dislay the first image in the start
        showCurrentImage();

    }
    //to upload the image
    private void showCurrentImage() {
        try {
            String path = imagesList.get(currentIndex);
            Image image;

            if (path.startsWith("res:")) {
                //load from resources
                String resourcePath = path.substring(4); // remove "res:"
                image = new Image(getClass().getResourceAsStream(resourcePath));
            } else {
                //load from file
                File file = new File(path);
                image = new Image(file.toURI().toString());
            }

            myImageView.setImage(image);
        } catch (Exception e) {
            System.out.println("Error while accesing the image : " + e.getMessage());
        }
    }
    // show the next image
    @FXML
    private void showNextImage() {
        currentIndex = (currentIndex + 1) % imagesList.size(); // boucle
        showCurrentImage();
    }
    // upload picture
    @FXML
    private void uploadPicture() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a Picture");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(myImageView.getScene().getWindow());

        if (selectedFile != null) {
            try {
                //gettin the uploads repo
                File uploadDir = new File("uploads");
                //checking
                if (!uploadDir.exists()) {
                    System.out.println("⚠️uploads doesn't exist  !");
                    return;
                }

                //saving
                File destination = new File(uploadDir, selectedFile.getName());

                java.nio.file.Files.copy(
                        selectedFile.toPath(),
                        destination.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );

                //display the image
                Image image = new Image(destination.toURI().toString());
                myImageView.setImage(image);

                //add uploaded image to the liste
                imagesList.add(destination.getAbsolutePath());

                //i don't know but it's working like this
                currentIndex = imagesList.size() - 1;

            } catch (Exception e) {
                System.out.println("Error while uploading : " + e.getMessage());
            }
        }
    }
    // go to transformation
    @FXML
    private void transformationPanel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pixo/bib/pixolibrary/fxml/TransformView.fxml"));
            Parent root = loader.load();

            TransformController transformController = loader.getController();
            transformController.setImage(myImageView.getImage());

            Stage stage = (Stage) myImageView.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // Logs more detailed info
        }
    }


}
