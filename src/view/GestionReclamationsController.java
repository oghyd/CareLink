package src.view;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import src.controller.ReclamationController;
import src.model.Reclamation;
import src.model.StatutDemande;

public class GestionReclamationsController {

    @FXML private TableView<Reclamation> tableReclamations;
    @FXML private TableColumn<Reclamation, Integer> colId;
    @FXML private TableColumn<Reclamation, String> colObjet;
    @FXML private TableColumn<Reclamation, String> colDescription;
    @FXML private TableColumn<Reclamation, StatutDemande> colStatut;
    @FXML private TableColumn<Reclamation, String> colDateCreation;

    private ReclamationController reclamationController = new ReclamationController();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colObjet.setCellValueFactory(new PropertyValueFactory<>("objet"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colDateCreation.setCellValueFactory(new PropertyValueFactory<>("dateCreation"));

        refreshTable();
    }

    @FXML
    private void handleTraiter() {
        Reclamation selected = tableReclamations.getSelectionModel().getSelectedItem();
        if (selected != null) {
            reclamationController.traiterReclamation(selected.getId(), StatutDemande.TRAITEE);
            refreshTable();
        }
    }

    @FXML
    private void handleSupprimer() {
        Reclamation selected = tableReclamations.getSelectionModel().getSelectedItem();
        if (selected != null) {
            reclamationController.supprimerReclamation(selected.getId());
            refreshTable();
        }
    }

    private void refreshTable() {
        tableReclamations.getItems().setAll(reclamationController.listerReclamations());
    }
}