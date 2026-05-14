package src.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import src.controller.DemandeController;
import src.controller.PieceJustificativeController;
import src.model.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StudentRequestsController {

    @FXML private TableView<Demande> tableDemandes;
    @FXML private TableColumn<Demande, String> colTitre;
    @FXML private TableColumn<Demande, String> colType;
    @FXML private TableColumn<Demande, String> colStatut;
    @FXML private TableColumn<Demande, String> colDate;
    @FXML private TableColumn<Demande, Void> colActions;

    @FXML private javafx.scene.layout.VBox formPanel;
    @FXML private TextField fieldTitre;
    @FXML private TextArea fieldDescription;
    @FXML private ComboBox<TypeDemande> comboType;
    @FXML private Label labelFichier;
    @FXML private Label labelError;
    @FXML private Button btnSubmitDemande;

    private DemandeController demandeController = new DemandeController();
    private PieceJustificativeController pieceController = new PieceJustificativeController();
    private List<File> selectedFiles = new ArrayList<>();
    private Demande demandeEnEdition = null;

    @FXML
    public void initialize() {
        // Setup columns
        colTitre.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getTitre()));
        colType.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getType().name()));
        colStatut.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getStatut().name()));
        colDate.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getDateCreation().toString()));

        // Setup ComboBox
        comboType.getItems().addAll(TypeDemande.values());

        // Setup actions column
        colActions.setCellFactory(col -> new TableCell<>() {
            Button editBtn = new Button("Modifier");
            Button piecesBtn = new Button("Pièces");

            {
                editBtn.setOnAction(e -> {
                    Demande d = getTableView().getItems().get(getIndex());
                    ouvrirFormulaireEdition(d);
                });
                piecesBtn.setOnAction(e -> {
                    Demande d = getTableView().getItems().get(getIndex());
                    afficherPieces(d.getId());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Demande d = getTableView().getItems().get(getIndex());
                    StatutDemande s = d.getStatut();
                    boolean editable = s != StatutDemande.TRAITEE && s != StatutDemande.REJETE;
                    editBtn.setDisable(!editable);
                    javafx.scene.layout.HBox box = new javafx.scene.layout.HBox(5, editBtn, piecesBtn);
                    setGraphic(box);
                }
            }
        });

        // Load data
        refreshTable();
    }

    @FXML
    private void handleNouvelleDemande() {
        demandeEnEdition = null;
        fieldTitre.clear();
        fieldDescription.clear();
        comboType.setValue(null);
        selectedFiles.clear();
        labelFichier.setText("Aucun fichier");
        labelError.setText("");
        btnSubmitDemande.setText("Envoyer");
        formPanel.setVisible(true);
        formPanel.setManaged(true);
    }

    @FXML
    private void handleAnnuler() {
        formPanel.setVisible(false);
        formPanel.setManaged(false);
    }

    @FXML
    private void handleChoisirFichier() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Sélectionner une pièce justificative");
        File file = chooser.showOpenDialog(formPanel.getScene().getWindow());
        if (file != null) {
            selectedFiles.add(file);
            labelFichier.setText(file.getName());
        }
    }

    @FXML
    private void handleSubmit() {
        String titre = fieldTitre.getText().trim();
        String desc = fieldDescription.getText().trim();
        TypeDemande type = comboType.getValue();

        if (titre.isEmpty() || type == null) {
            labelError.setText("Veuillez remplir tous les champs obligatoires.");
            return;
        }

        if (demandeEnEdition == null) {
            // Create new
            Demande demande = new Demande(0, titre, desc, type, new Date(), StatutDemande.CREE);
            List<PieceJustificative> pieces = new ArrayList<>();
            for (File f : selectedFiles) {
                pieces.add(new PieceJustificative(0, f.getName(), f.getAbsolutePath(), new Date()));
            }
            boolean ok = demandeController.creerDemande(demande, pieces);
            if (ok) {
                refreshTable();
                handleAnnuler();
            } else {
                labelError.setText("Erreur lors de la création.");
            }
        } else {
            // Edit existing
            demandeEnEdition.setTitre(titre);
            demandeEnEdition.setDescription(desc);
            demandeEnEdition.setType(type);
            boolean ok = demandeController.modifierDemande(demandeEnEdition);
            if (ok) {
                refreshTable();
                handleAnnuler();
            } else {
                labelError.setText("Erreur lors de la modification.");
            }
        }
    }

    private void ouvrirFormulaireEdition(Demande d) {
        demandeEnEdition = d;
        fieldTitre.setText(d.getTitre());
        fieldDescription.setText(d.getDescription());
        comboType.setValue(d.getType());
        labelError.setText("");
        btnSubmitDemande.setText("Modifier");
        formPanel.setVisible(true);
        formPanel.setManaged(true);
    }

    private void afficherPieces(int demandeId) {
        List<PieceJustificative> pieces = pieceController.listerPieces(demandeId);
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Pièces justificatives");
        alert.setHeaderText("Fichiers attachés à cette demande");
        if (pieces.isEmpty()) {
            alert.setContentText("Aucune pièce jointe.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (PieceJustificative p : pieces) {
                sb.append("• ").append(p.getNomFichier()).append("\n");
            }
            alert.setContentText(sb.toString());
        }
        alert.showAndWait();
    }

    private void refreshTable() {
        ObservableList<Demande> data = FXCollections.observableArrayList(demandeController.listerMesDemandes());
        tableDemandes.setItems(data);
    }
}