package src.view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import src.controller.CompteController;
import src.model.Etudiant;
import src.utils.SessionManager;

public class StudentProfileController {

    @FXML private TextField fieldNom;
    @FXML private TextField fieldPrenom;
    @FXML private TextField fieldEmail;
    @FXML private TextField fieldTelephone;
    @FXML private TextField fieldHandicap;
    @FXML private TextField fieldMatricule;
    @FXML private Label labelStatus;

    private CompteController compteController = new CompteController();

    @FXML
    public void initialize() {
        Etudiant etudiant = (Etudiant) SessionManager.getCurrentUser();
        if (etudiant == null) return;

        fieldNom.setText(etudiant.getNom());
        fieldPrenom.setText(etudiant.getPrenom());
        fieldEmail.setText(etudiant.getEmail());
        fieldTelephone.setText(etudiant.getTelephone());
        fieldHandicap.setText(etudiant.getTypeHandicap());
        fieldMatricule.setText(etudiant.getMatricule());
    }

    @FXML
    private void handleSave() {
        Etudiant etudiant = (Etudiant) SessionManager.getCurrentUser();
        if (etudiant == null) return;

        etudiant.setNom(fieldNom.getText().trim());
        etudiant.setPrenom(fieldPrenom.getText().trim());
        etudiant.setEmail(fieldEmail.getText().trim());
        etudiant.setTelephone(fieldTelephone.getText().trim());
        etudiant.setTypeHandicap(fieldHandicap.getText().trim());

        boolean ok = compteController.modifierMonCompte(etudiant);

        if (ok) {
            labelStatus.setText("✔ Profil mis à jour");
            labelStatus.setStyle("-fx-text-fill: #4CAF50;");
        } else {
            labelStatus.setText("✘ Erreur lors de la mise à jour");
            labelStatus.setStyle("-fx-text-fill: #C62828;");
        }
    }
}