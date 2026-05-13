/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
 
package src.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import src.controller.CompteController;
import src.model.Etudiant;

/**
 * FXML Controller class
 *
 * @author oghyd
 */
 
public class RegisterController {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private TextField matriculeField;
    @FXML private TextField telephoneField;
    @FXML private TextField typeHandicapField;
    @FXML private PasswordField mdpField;
    @FXML private Label statusLabel;

    @FXML
    private void handleRegister() {
        Etudiant etudiant = new Etudiant(
            0,
            nomField.getText().trim(),
            prenomField.getText().trim(),
            emailField.getText().trim(),
            mdpField.getText(),
            false,
            matriculeField.getText().trim(),
            typeHandicapField.getText().trim(),
            telephoneField.getText().trim()
        );

        CompteController compteCtrl = new CompteController();
        if (compteCtrl.sInscrire(etudiant)) {
            statusLabel.setStyle("-fx-text-fill: green;");
            statusLabel.setText("Compte créé. En attente d'activation.");
        } else {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Erreur lors de l'inscription.");
        }
    }

    @FXML
    private void handleBack() {
        MainApp.loadScreen("login.fxml");
    }
}