package com.pixo.bib.pixolibrary.Controllers;

import com.pixo.bib.pixolibrary.Model.User;
import com.pixo.bib.pixolibrary.dao.UserDAO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RegisterController {

    @FXML private TextField email;
    @FXML private TextField name;
    @FXML private PasswordField password;
    @FXML private PasswordField confirmPassword;
    @FXML private TextField firstname;
    @FXML private Label messageLabel;

    private final UserDAO userDAO = new UserDAO();


    @FXML
    private void register() throws SQLException {
        String emailText = email.getText();
        String passwordText = password.getText();
        String confirmPasswordText = confirmPassword.getText();
        String nameText = name.getText();
        String firstnameText = firstname.getText();

        if (firstnameText.isEmpty() || emailText.isEmpty() || nameText.isEmpty() || passwordText.isEmpty() || confirmPasswordText.isEmpty()) {
            messageLabel.setText("Please fill all the fields");
            return;
        }

        if (!isValidEmail(emailText)) {
            messageLabel.setText("Invalid email format");
            return;
        }

        if (passwordText.length() < 8) {
            messageLabel.setText("Password must be at least 8 characters");
            return;
        }

        if (!passwordText.equals(confirmPasswordText)) {
            messageLabel.setText("Passwords do not match");
            return;
        }

        try {
            User userToRegister = new User(emailText, nameText, passwordText, firstnameText);
            boolean created = userDAO.register(userToRegister);

            if (created) {
                messageLabel.setText("User registered successfully! Redirecting to login...");

                ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
                scheduler.schedule(() -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pixo/bib/pixolibrary/fxml/LoginView.fxml"));
                        Parent root = loader.load();
                        Platform.runLater(() -> {
                            Stage stage = (Stage) password.getScene().getWindow();
                            stage.setScene(new Scene(root));
                            stage.show();
                        });
                    } catch (IOException e) {
                        Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Error", "Failed to load LoginView"));
                    } finally {
                        scheduler.shutdown();
                    }
                }, 3, TimeUnit.SECONDS);
            } else {
                messageLabel.setText("Registration failed: Email or user already exists.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML private void goLogin() throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pixo/bib/pixolibrary/fxml/LoginView.fxml"));
            Parent root = loader.load();
            LoginController controller = loader.getController();
            //controller.initialize();
            Stage stage = (Stage) password.getScene().getWindow();
            Platform.runLater(() -> {
                stage.setScene(new Scene(root));
                stage.show();
            });

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
