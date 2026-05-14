/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */

package src.view;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import src.controller.AuthController;
import src.model.User;
import src.utils.SessionManager;

/**
 * FXML Controller class
 *
 * @author oghyd
 */
 
public class ShellController {

    @FXML private Label nameLabel;
    @FXML private Label roleLabel;
    @FXML private StackPane contentArea;

    // Student buttons
    @FXML private Button btnMesDemandes;
    @FXML private Button btnMesReclamations;
    @FXML private Button btnMonProfil;

    // Admin buttons
    @FXML private Button btnTableauDeBord;
    @FXML private Button btnComptes;
    @FXML private Button btnDemandes;
    @FXML private Button btnReclamations;
    @FXML private Button btnHistorique;

    @FXML
    private void initialize() {
        User user = SessionManager.getCurrentUser();
        nameLabel.setText(user.getPrenom() + " " + user.getNom());

        if (SessionManager.isAdmin()) {
            roleLabel.setText("Administrateur");
            hide(btnMesDemandes, btnMesReclamations, btnMonProfil);
            loadContent("tableau_de_bord.fxml");
        } else {
            roleLabel.setText("Étudiant");
            hide(btnTableauDeBord, btnComptes, btnDemandes, btnReclamations, btnHistorique);
            loadContent("StudentRequests.fxml");
        }
    }

    // Student screens (Ines)

    @FXML private void showMesDemandes()     { loadContent("StudentRequests.fxml"); }
    @FXML private void showMesReclamations() { loadContent("StudentComplaints.fxml"); }
    @FXML private void showMonProfil()       { loadContent("StudentProfile.fxml"); }

    // Admin screens (Omar + Idriss)

    @FXML private void showTableauDeBord()   { loadContent("tableau_de_bord.fxml"); }
@FXML 
private void showComptes() { 
    loadContent("gestion_comptes.fxml"); 
}
    @FXML 
private void showDemandes() { 
    loadContent("gestion_demandes.fxml"); 
}

@FXML 
private void showReclamations() { 
    loadContent("gestion_reclamations.fxml"); 
}

@FXML private void showHistorique()      { loadContent("historique.fxml"); }


    @FXML
    private void handleLogout() {
        new AuthController().logout();
        MainApp.loadScreen("login.fxml");
    }

    // ── Content loading ──
    // When a real screen FXML is ready, replace showPlaceholder() with loadContent().
    // Example: private void showMesDemandes() { loadContent("mes_demandes.fxml"); }

    /**
     * Loads an FXML file into the content area.
     * This is how Ines and Idriss plug their screens in.
     */
    public void loadContent(String fxmlFile) {
        try {
            Node content = FXMLLoader.load(getClass().getResource(fxmlFile));
            contentArea.getChildren().setAll(content);
        } catch (Exception e) {
            e.printStackTrace();
            showPlaceholder("Erreur: " + fxmlFile + " introuvable");
        }
    }

    private void showPlaceholder(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 18; -fx-text-fill: #999;");
        contentArea.getChildren().setAll(label);
    }

    private void hide(Button... buttons) {
        for (Button btn : buttons) {
            btn.setVisible(false);
            btn.setManaged(false); // removes from layout entirely
        }
    }

}