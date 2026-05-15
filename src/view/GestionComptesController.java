package src.view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.geometry.Insets;
import src.controller.CompteController;
import src.model.Admin;
import src.model.Etudiant;
import src.model.User;
import src.utils.SessionManager;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class GestionComptesController {

    @FXML private TableView<User> tableComptes;
    @FXML private TableColumn<User, Integer> colId;
    @FXML private TableColumn<User, String> colNom;
    @FXML private TableColumn<User, String> colPrenom;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, Boolean> colActif;

    private CompteController compteController = new CompteController();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colActif.setCellValueFactory(new PropertyValueFactory<>("actif"));

        refreshTable();
    }

    @FXML
    private void handleAjouterCompte() {
        Stage dialog = new Stage();
        dialog.setTitle("Ajouter un compte");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));
        
        TextField nomField = new TextField();
        TextField prenomField = new TextField();
        TextField emailField = new TextField();
        PasswordField mdpField = new PasswordField();
        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("Étudiant", "Administrateur");
        roleBox.setValue("Étudiant");
        
        TextField matriculeField = new TextField();
        TextField typeHandicapField = new TextField();
        TextField telephoneField = new TextField();
        TextField fonctionField = new TextField();
        
        // Champs spécifiques étudiant (visibles par défaut)
        Label matriculeLabel = new Label("Matricule :");
        Label typeHandicapLabel = new Label("Type handicap :");
        Label telephoneLabel = new Label("Téléphone :");
        Label fonctionLabel = new Label("Fonction :");
        
        // Cacher champs admin par défaut
        fonctionLabel.setVisible(false);
        fonctionField.setVisible(false);
        
        // Changer les champs selon le rôle sélectionné
        roleBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean isEtudiant = newVal.equals("Étudiant");
            matriculeLabel.setVisible(isEtudiant);
            matriculeField.setVisible(isEtudiant);
            typeHandicapLabel.setVisible(isEtudiant);
            typeHandicapField.setVisible(isEtudiant);
            telephoneLabel.setVisible(isEtudiant);
            telephoneField.setVisible(isEtudiant);
            fonctionLabel.setVisible(!isEtudiant);
            fonctionField.setVisible(!isEtudiant);
        });
        
        // Ajout des champs à la grille
        grid.add(new Label("Nom :"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Prénom :"), 0, 1);
        grid.add(prenomField, 1, 1);
        grid.add(new Label("Email :"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Mot de passe :"), 0, 3);
        grid.add(mdpField, 1, 3);
        grid.add(new Label("Rôle :"), 0, 4);
        grid.add(roleBox, 1, 4);
        
        grid.add(matriculeLabel, 0, 5);
        grid.add(matriculeField, 1, 5);
        grid.add(typeHandicapLabel, 0, 6);
        grid.add(typeHandicapField, 1, 6);
        grid.add(telephoneLabel, 0, 7);
        grid.add(telephoneField, 1, 7);
        grid.add(fonctionLabel, 0, 8);
        grid.add(fonctionField, 1, 8);
        
        Button btnSave = new Button("Créer");
        btnSave.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-cursor: hand;");
        btnSave.setOnAction(e -> {
            if (roleBox.getValue().equals("Étudiant")) {
                compteController.creerCompteEtudiant(
                    nomField.getText(),
                    prenomField.getText(),
                    emailField.getText(),
                    mdpField.getText(),
                    matriculeField.getText(),
                    typeHandicapField.getText(),
                    telephoneField.getText()
                );
            } else {
                compteController.creerCompteAdmin(
                    nomField.getText(),
                    prenomField.getText(),
                    emailField.getText(),
                    mdpField.getText(),
                    fonctionField.getText()
                );
            }
            dialog.close();
            refreshTable();
        });
        
        Button btnAnnuler = new Button("Annuler");
        btnAnnuler.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
        btnAnnuler.setOnAction(e -> dialog.close());
        
        grid.add(btnSave, 0, 9);
        grid.add(btnAnnuler, 1, 9);
        
        Scene scene = new Scene(grid, 450, 450);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    @FXML
    private void handleActiver() {
        User selected = tableComptes.getSelectionModel().getSelectedItem();
        if (selected != null) {
            compteController.activerCompte(selected.getId());
            refreshTable();
        }
    }

    @FXML
    private void handleDesactiver() {
        User selected = tableComptes.getSelectionModel().getSelectedItem();
        if (selected != null) {
            compteController.desactiverCompte(selected.getId());
            refreshTable();
        }
    }

    @FXML
private void handleSupprimer() {
    User selected = tableComptes.getSelectionModel().getSelectedItem();
    if (selected == null) {
        showAlert("Erreur", "Veuillez sélectionner un compte à supprimer.");
        return;
    }
    
    // Empêcher la suppression de son propre compte
    if (SessionManager.isCurrentUser(selected.getId())) {
        showAlert("Erreur", "Vous ne pouvez pas supprimer votre propre compte.");
        return;
    }
    
    // Empêcher la suppression d'un autre admin
    if (selected instanceof Admin) {
        showAlert("Erreur", "Vous ne pouvez pas supprimer un autre administrateur.");
        return;
    }
    
    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
    confirm.setTitle("Confirmation");
    confirm.setHeaderText("Supprimer le compte");
    confirm.setContentText("Voulez-vous vraiment supprimer le compte de " + selected.getNom() + " " + selected.getPrenom() + " ?");
    
    if (confirm.showAndWait().get() == ButtonType.OK) {
        compteController.supprimerCompte(selected.getId());
        refreshTable();
        showAlert("Succès", "Compte supprimé avec succès.");
    }
}

    private void refreshTable() {
        tableComptes.getItems().setAll(compteController.listerComptes());
    }

    @FXML
private void handleModifier() {
    User selected = tableComptes.getSelectionModel().getSelectedItem();
    if (selected == null) {
        showAlert("Erreur", "Veuillez sélectionner un compte à modifier.");
        return;
    }
    
    // Vérifier si c'est un admin et si c'est un autre admin (pas autorisé)
    if (selected instanceof Admin && !SessionManager.isCurrentUser(selected.getId())) {
        showAlert("Erreur", "Vous ne pouvez pas modifier un autre administrateur.");
        return;
    }
    
    showModifierDialog(selected);
}

private void showModifierDialog(User user) {
    Stage dialog = new Stage();
    dialog.setTitle("Modifier le compte - " + user.getNom() + " " + user.getPrenom());
    
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(15));
    
    TextField nomField = new TextField(user.getNom());
    TextField prenomField = new TextField(user.getPrenom());
    TextField emailField = new TextField(user.getEmail());
    PasswordField mdpField = new PasswordField();
    mdpField.setPromptText("Nouveau mot de passe (laisser vide pour ne pas changer)");
    
    grid.add(new Label("Nom :"), 0, 0);
    grid.add(nomField, 1, 0);
    grid.add(new Label("Prénom :"), 0, 1);
    grid.add(prenomField, 1, 1);
    grid.add(new Label("Email :"), 0, 2);
    grid.add(emailField, 1, 2);
    grid.add(new Label("Mot de passe :"), 0, 3);
    grid.add(mdpField, 1, 3);
    
    // Champs spécifiques selon le type d'utilisateur
    if (user instanceof Etudiant) {
        Etudiant etudiant = (Etudiant) user;
        TextField matriculeField = new TextField(etudiant.getMatricule());
        TextField typeHandicapField = new TextField(etudiant.getTypeHandicap());
        TextField telephoneField = new TextField(etudiant.getTelephone());
        
        grid.add(new Label("Matricule :"), 0, 4);
        grid.add(matriculeField, 1, 4);
        grid.add(new Label("Type handicap :"), 0, 5);
        grid.add(typeHandicapField, 1, 5);
        grid.add(new Label("Téléphone :"), 0, 6);
        grid.add(telephoneField, 1, 6);
        
        Button btnSave = new Button("Enregistrer");
        btnSave.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-cursor: hand;");
        btnSave.setOnAction(e -> {
            // Mettre à jour les champs
            etudiant.setNom(nomField.getText());
            etudiant.setPrenom(prenomField.getText());
            etudiant.setEmail(emailField.getText());
            if (!mdpField.getText().isEmpty()) {
                etudiant.setMotDePasse(mdpField.getText());
            }
            etudiant.setMatricule(matriculeField.getText());
            etudiant.setTypeHandicap(typeHandicapField.getText());
            etudiant.setTelephone(telephoneField.getText());
            
            compteController.modifierMonCompte(etudiant);
            dialog.close();
            refreshTable();
            showAlert("Succès", "Compte étudiant modifié avec succès.");
        });
        grid.add(btnSave, 1, 7);
        
    } else if (user instanceof Admin) {
        // Admin : seulement modifier son propre compte
        if (!SessionManager.isCurrentUser(user.getId())) {
            showAlert("Erreur", "Vous ne pouvez pas modifier un autre administrateur.");
            dialog.close();
            return;
        }
        
        TextField fonctionField = new TextField(((Admin) user).getFonction());
        grid.add(new Label("Fonction :"), 0, 4);
        grid.add(fonctionField, 1, 4);
        
        Button btnSave = new Button("Enregistrer");
        btnSave.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-cursor: hand;");
        btnSave.setOnAction(e -> {
            ((Admin) user).setNom(nomField.getText());
            ((Admin) user).setPrenom(prenomField.getText());
            ((Admin) user).setEmail(emailField.getText());
            if (!mdpField.getText().isEmpty()) {
                ((Admin) user).setMotDePasse(mdpField.getText());
            }
            ((Admin) user).setFonction(fonctionField.getText());
            
            compteController.modifierMonCompte(user);
            dialog.close();
            refreshTable();
            showAlert("Succès", "Votre compte a été modifié avec succès.");
        });
        grid.add(btnSave, 1, 5);
    }
    
    Button btnAnnuler = new Button("Annuler");
    btnAnnuler.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
    btnAnnuler.setOnAction(e -> dialog.close());
    grid.add(btnAnnuler, 1, 8);
    
    Scene scene = new Scene(grid, 450, 500);
    dialog.setScene(scene);
    dialog.showAndWait();
}

private void showAlert(String titre, String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(titre);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
}

}