package src.view;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import src.controller.CompteController;
import src.model.User;

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
        // Lier les colonnes aux attributs de User
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colActif.setCellValueFactory(new PropertyValueFactory<>("actif"));

        // Charger les données
        refreshTable();
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
        if (selected != null) {
            compteController.supprimerCompte(selected.getId());
            refreshTable();
        }
    }

    private void refreshTable() {
        tableComptes.getItems().setAll(compteController.listerComptes());
    }
}