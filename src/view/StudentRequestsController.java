package src.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
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
        // Copier le fichier dans un dossier local de l'application
            File dossierPJ = new File(System.getProperty("user.dir"), "uploads/pj");
            dossierPJ.mkdirs();
            File destination = new File(dossierPJ, file.getName());
            try {
                java.nio.file.Files.copy(file.toPath(), destination.toPath(),
                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                selectedFiles.add(destination); // stocker la copie locale
                labelFichier.setText(file.getName());
            } catch (Exception e) {
                selectedFiles.add(file); // fallback : stocker l'original
                labelFichier.setText(file.getName());
            }
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

        Stage dialog = new Stage();
        dialog.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialog.setTitle("Pièces justificatives");

        javafx.scene.layout.VBox box = new javafx.scene.layout.VBox(8);
        box.setPadding(new javafx.geometry.Insets(16));

        if (pieces.isEmpty()) {
            box.getChildren().add(new Label("Aucune pièce jointe."));
        } else {
            for (PieceJustificative p : pieces) {
                Button fileBtn = new Button("📄 " + p.getNomFichier());
                fileBtn.setOnAction(ev -> {
                    File source = resoudreCheminFichier(p.getChemin(), p.getNomFichier());
                    if (source == null || !source.exists()) {
                        new Alert(Alert.AlertType.WARNING,
                        "Fichier introuvable.\nChemin enregistré : " + p.getChemin() +
                        "\nVérifiez que le fichier existe encore sur ce poste.").showAndWait();
                        return;
                    }
                    FileChooser saveChooser = new FileChooser();
                    saveChooser.setTitle("Télécharger la pièce justificative");
                    saveChooser.setInitialFileName(p.getNomFichier());
                    File dest = saveChooser.showSaveDialog(((javafx.scene.Node) ev.getSource()).getScene().getWindow());
                    if (dest != null) {
                        try {
                            java.nio.file.Files.copy(source.toPath(), dest.toPath(),
                            java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                            new Alert(Alert.AlertType.INFORMATION,
                            "Fichier téléchargé avec succès.").showAndWait();
                        } catch (Exception ex) {
                            new Alert(Alert.AlertType.ERROR,
                            "Erreur lors du téléchargement : " + ex.getMessage()).showAndWait();
                        }
                    }
                });
                box.getChildren().add(fileBtn);
            }
        }

        dialog.setScene(new javafx.scene.Scene(box, 400, 300));
        dialog.show();
    }

private File resoudreCheminFichier(String chemin, String nomFichier) {
    // 1. Chemin absolu direct
    File f = new File(chemin);
    if (f.isAbsolute() && f.exists()) return f;

    // 2. Chemin relatif depuis le répertoire de travail
    f = new File(System.getProperty("user.dir"), chemin);
    if (f.exists()) return f;

    // 3. Chemin relatif depuis le dossier home de l'utilisateur
    f = new File(System.getProperty("user.home"), chemin);
    if (f.exists()) return f;

    // 4. Juste le nom du fichier dans le dossier uploads/pj local
    f = new File(System.getProperty("user.dir"), "uploads/pj/" + nomFichier);
    if (f.exists()) return f;

    return null;
}

    private void refreshTable() {
        ObservableList<Demande> data = FXCollections.observableArrayList(demandeController.listerMesDemandes());
        tableDemandes.setItems(data);
    }
}