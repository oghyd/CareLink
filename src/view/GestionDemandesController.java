package src.view;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import src.controller.DemandeController;
import src.model.Demande;
import src.model.StatutDemande;

public class GestionDemandesController {

    @FXML private TableView<Demande> tableDemandes;
    @FXML private TableColumn<Demande, Integer> colId;
    @FXML private TableColumn<Demande, String> colTitre;
    @FXML private TableColumn<Demande, String> colDescription;
    @FXML private TableColumn<Demande, String> colType;
    @FXML private TableColumn<Demande, StatutDemande> colStatut;
    @FXML private TableColumn<Demande, String> colDateCreation;

    private DemandeController demandeController = new DemandeController();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colDateCreation.setCellValueFactory(new PropertyValueFactory<>("dateCreation"));

        refreshTable();
    }

    @FXML
    private void handleTraiter() {
        Demande selected = tableDemandes.getSelectionModel().getSelectedItem();
        if (selected != null) {
            demandeController.traiterDemande(selected.getId(), StatutDemande.TRAITEE);
            refreshTable();
        }
    }

    @FXML
    private void handleSupprimer() {
        Demande selected = tableDemandes.getSelectionModel().getSelectedItem();
        if (selected != null) {
            demandeController.supprimerDemande(selected.getId());
            refreshTable();
        }
    }

    private void refreshTable() {
        tableDemandes.getItems().setAll(demandeController.listerDemandes());
    }
}