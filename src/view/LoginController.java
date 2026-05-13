/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
 
package src.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import src.controller.AuthController;
import src.model.User;

/**
 * FXML Controller class
 *
 * @author oghyd
 */
 
public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private AuthController authController = new AuthController();

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        User user = authController.login(email, password);
        if (user != null) {
            MainApp.loadScreen("shell.fxml");
        } else {
            errorLabel.setText("Email ou mot de passe incorrect.");
        }
    }

    @FXML
    private void handleRegisterLink() {
        MainApp.loadScreen("register.fxml");
    }
}