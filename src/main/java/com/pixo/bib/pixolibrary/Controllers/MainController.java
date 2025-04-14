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
import java.util.Objects;

public class MainController {

    @FXML private ImageView myImageView;

    //the list of the Images to display and
    private final ArrayList<String> imagesList = new ArrayList<>();
    private int currentIndex=0;
    private int numberOfImages=2;

    // getter and setter for Number of images
    public int getNumberOfImages(){
        return numberOfImages;
    }
    public void setNumberOfImages(int numberOfImages){this.numberOfImages=numberOfImages;}

    //the first setup of the MainView
    @FXML
    public void initialize() {
        imagesList.clear(); // make sure it's clean

        // Path to Uploads
        File uploadsPic = new File("uploads");

        if (uploadsPic.exists()) {
            File[] files = uploadsPic.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().matches("picture\\d+\\..+")) {

                        String uploadsPath = "uploads/" + file.getName();
                        imagesList.add(uploadsPath);
                    }
                }
                //set The NumberOfPictures
                setNumberOfImages(imagesList.size());
                //System.out.println("Loaded " + getNumberOfImages() + " images from uploads.");
            }
        } else {
            System.out.println("Resources image directory doesn't exist.");
        }

        //display the firstImage
        if (!imagesList.isEmpty()) {
            showCurrentImage();
        }
    }

    // upload picture {it uploads the picture into /uploads so it will be easy to use in the initialise method}
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
                // Create or use "uploads" directory relative to the working directory
                File uploadsDir = new File("uploads");
                if (!uploadsDir.exists()) {
                    System.out.println("UploadsDirectory doesn't exist.");
                    uploadsDir.mkdirs();
                }

                // numberOfPictures
                int count = (int) java.util.Arrays.stream(Objects.requireNonNull(uploadsDir.listFiles()))
                        .filter(file -> file.getName().matches("picture\\d+\\..+"))
                        .count();
                setNumberOfImages(count + 1);

                //file extension
                String extension = selectedFile.getName().substring(
                        selectedFile.getName().lastIndexOf(".")
                        );

                //create destination file name: picture{number}.{ext}
                File destination = new File(uploadsDir, "picture" + getNumberOfImages() + extension);

                //copy the file
                java.nio.file.Files.copy(
                        selectedFile.toPath(),
                        destination.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );

                //display the pic
                Image image = new Image(destination.toURI().toString());
                myImageView.setImage(image);

                //add image's path to imageList
                imagesList.add("uploads/" + destination.getName());
                currentIndex = imagesList.size() - 1;

                //System.out.println("Image uploaded to: " + destination.getAbsolutePath());

            } catch (Exception e) {
                System.out.println("Error while uploading: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // To display The image with the index currentIndex
    private void showCurrentImage() {
        try {
            String path = imagesList.get(currentIndex);
            //System.out.println("Current image path: " + path+"\n");

            File imageFile = new File(path);
            if (!imageFile.exists()) {
                //System.out.println("Image file does not exist: " + path);
                return;
            }

            Image image = new Image(imageFile.toURI().toString());
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
            e.printStackTrace();
        }
    }


}
