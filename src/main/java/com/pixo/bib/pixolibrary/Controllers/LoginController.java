package com.pixo.bib.pixolibrary.Controllers;

import com.pixo.bib.pixolibrary.Model.User;
import com.pixo.bib.pixolibrary.dao.UserDAO;
import com.pixo.bib.pixolibrary.database.DataBaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {
        @FXML private TextField email;
        @FXML private PasswordField password;

        private final UserDAO userDAO = new UserDAO();

        @FXML public void initialize() {
            try {
                DataBaseConnection.createTablesIfNotExist();
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "ERROR","table not creted");
            }
        }

        @FXML public void login() {
            String emailInput = email.getText().trim();
            String passwordInput = password.getText();

            if (emailInput.isEmpty() || passwordInput.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "EmptyField", "fill all the fields");
                return;
            }

            try {
                User user = userDAO.login(emailInput, passwordInput);
                if (user != null) {
                    String fullName = userDAO.getFullNameByEmail(user.getEmail());
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pixo/bib/pixolibrary/fxml/MainView.fxml"));
                    Parent root = loader.load();

                    MainController mainController = loader.getController();
                    mainController.initializedConnected();//to reUpload Images and Metdatas
                    mainController.setIsConnected(true);
                    mainController.setMessageDisplay("Welcome " + fullName);
                    Stage stage = (Stage) password.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.show();
                } else {
                    showAlert(Alert.AlertType.ERROR, "error", "email or password incorrect.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Errotr", "login failed.");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @FXML public void register() throws IOException {
           try {
               FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pixo/bib/pixolibrary/fxml/RegisterView.fxml"));
               Parent root = loader.load();
               Stage stage = (Stage) password.getScene().getWindow();
               stage.setScene(new Scene(root));
               stage.show();
           } catch (IOException e) {
               throw new RuntimeException(e);
           }
        }

        private void showAlert(Alert.AlertType type, String title, String message) {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }
}


