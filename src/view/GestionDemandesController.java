package src.view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.geometry.Insets;
import src.controller.DemandeController;
import src.controller.PieceJustificativeController;
import src.model.Demande;
import src.model.StatutDemande;
import src.model.PieceJustificative;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class GestionDemandesController {

    @FXML private TableView<Demande> tableDemandes;
    @FXML private TableColumn<Demande, Integer> colId;
    @FXML private TableColumn<Demande, String> colTitre;
    @FXML private TableColumn<Demande, String> colDescription;
    @FXML private TableColumn<Demande, String> colType;
    @FXML private TableColumn<Demande, StatutDemande> colStatut;
    @FXML private TableColumn<Demande, String> colDateCreation;

    private DemandeController demandeController = new DemandeController();
    private PieceJustificativeController pieceController = new PieceJustificativeController();

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
    private void handleTableClick(MouseEvent event) {
        if (event.getClickCount() == 2) { // Double-clic
            Demande selected = tableDemandes.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showDemandeDetails(selected);
            }
        }
    }

    private void showDemandeDetails(Demande demande) {
        Stage dialog = new Stage();
        dialog.setTitle("Détail de la demande #" + demande.getId());
        
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));
        
        // Labels des détails
        vbox.getChildren().addAll(
            new Label("ID : " + demande.getId()),
            new Label("Titre : " + demande.getTitre()),
            new Label("Description : " + demande.getDescription()),
            new Label("Type : " + demande.getType()),
            new Label("Statut : " + demande.getStatut()),
            new Label("Date de création : " + demande.getDateCreation())
        );
        
        // Pièce jointe (s'il y en a une)
        PieceJustificative piece = pieceController.getPieceByDemande(demande.getId());
        if (piece != null) {
            Button btnPhoto = new Button("📎 Voir la pièce jointe");
            btnPhoto.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");
            btnPhoto.setOnAction(e -> openImage(piece.getChemin()));
            vbox.getChildren().add(btnPhoto);
        }
        
        Button btnFermer = new Button("Fermer");
        btnFermer.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-cursor: hand;");
        btnFermer.setOnAction(e -> dialog.close());
        vbox.getChildren().add(btnFermer);
        
        Scene scene = new Scene(vbox, 450, 350);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

   private void openImage(String chemin) {
    try {
        // Si le chemin commence par /uploads, le convertir en chemin absolu
        if (chemin.startsWith("/uploads")) {
            chemin = "C:/Users/idber/CareLink/CareLink" + chemin;
        }
        
        File file = new File(chemin);
        if (file.exists()) {
            Desktop.getDesktop().open(file);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Fichier introuvable");
            alert.setHeaderText("Impossible d'ouvrir la pièce jointe");
            alert.setContentText("Le fichier n'existe pas à l'emplacement : " + chemin);
            alert.showAndWait();
        }
    } catch (IOException e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText("Impossible d'ouvrir le fichier");
        alert.setContentText("Erreur : " + e.getMessage());
        alert.showAndWait();
    }
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