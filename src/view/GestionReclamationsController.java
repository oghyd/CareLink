package src.view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.geometry.Insets;
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
    private void handleTableClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Reclamation selected = tableReclamations.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showReclamationDetails(selected);
            }
        }
    }

    private void showReclamationDetails(Reclamation reclamation) {
        Stage dialog = new Stage();
        dialog.setTitle("Détail de la réclamation #" + reclamation.getId());
        
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));
        
        vbox.getChildren().addAll(
            new Label("ID : " + reclamation.getId()),
            new Label("Objet : " + reclamation.getObjet()),
            new Label("Description : " + reclamation.getDescription()),
            new Label("Statut : " + reclamation.getStatut()),
            new Label("Date de création : " + reclamation.getDateCreation())
        );
        
        Button btnFermer = new Button("Fermer");
        btnFermer.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-cursor: hand;");
        btnFermer.setOnAction(e -> dialog.close());
        vbox.getChildren().add(btnFermer);
        
        Scene scene = new Scene(vbox, 450, 300);
        dialog.setScene(scene);
        dialog.showAndWait();
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