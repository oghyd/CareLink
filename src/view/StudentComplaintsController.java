package src.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import src.controller.ReclamationController;
import src.model.Reclamation;
import src.model.StatutDemande;

import java.util.Date;

public class StudentComplaintsController {

    @FXML private TableView<Reclamation> tableReclamations;
    @FXML private TableColumn<Reclamation, String> colObjet;
    @FXML private TableColumn<Reclamation, String> colStatut;
    @FXML private TableColumn<Reclamation, String> colDate;
    @FXML private TableColumn<Reclamation, Void> colActions;

    @FXML private javafx.scene.layout.VBox formPanel;
    @FXML private TextField fieldObjet;
    @FXML private TextArea fieldDescription;
    @FXML private Label labelError;
    @FXML private Button btnSubmitReclamation;

    private ReclamationController reclamationController = new ReclamationController();
    private Reclamation reclamationEnEdition = null;

    @FXML
    public void initialize() {
        colObjet.setCellValueFactory(r -> new javafx.beans.property.SimpleStringProperty(r.getValue().getObjet()));
        colStatut.setCellValueFactory(r -> new javafx.beans.property.SimpleStringProperty(r.getValue().getStatut().name()));
        colDate.setCellValueFactory(r -> new javafx.beans.property.SimpleStringProperty(r.getValue().getDateCreation().toString()));

        colActions.setCellFactory(col -> new TableCell<>() {
            Button editBtn = new Button("Modifier");

            {
                editBtn.setOnAction(e -> {
                    Reclamation r = getTableView().getItems().get(getIndex());
                    ouvrirFormulaireEdition(r);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Reclamation r = getTableView().getItems().get(getIndex());
                    StatutDemande s = r.getStatut();
                    boolean editable = s != StatutDemande.TRAITEE && s != StatutDemande.REJETE;
                    editBtn.setDisable(!editable);
                    setGraphic(editBtn);
                }
            }
        });

        refreshTable();
    }

    @FXML
    private void handleNouvelleReclamation() {
        reclamationEnEdition = null;
        fieldObjet.clear();
        fieldDescription.clear();
        labelError.setText("");
        btnSubmitReclamation.setText("Envoyer");
        formPanel.setVisible(true);
        formPanel.setManaged(true);
    }

    @FXML
    private void handleAnnuler() {
        formPanel.setVisible(false);
        formPanel.setManaged(false);
    }

    @FXML
    private void handleSubmit() {
        String objet = fieldObjet.getText().trim();
        String desc = fieldDescription.getText().trim();

        if (objet.isEmpty()) {
            labelError.setText("L'objet est obligatoire.");
            return;
        }

        if (reclamationEnEdition == null) {
            Reclamation rec = new Reclamation(0, objet, desc, new Date(), StatutDemande.CREE);
            boolean ok = reclamationController.creerReclamation(rec);
            if (ok) {
                refreshTable();
                handleAnnuler();
            } else {
                labelError.setText("Erreur lors de la création.");
            }
        } else {
            reclamationEnEdition.setObjet(objet);
            reclamationEnEdition.setDescription(desc);
            boolean ok = reclamationController.modifierReclamation(reclamationEnEdition);
            if (ok) {
                refreshTable();
                handleAnnuler();
            } else {
                labelError.setText("Erreur lors de la modification.");
            }
        }
    }

    private void ouvrirFormulaireEdition(Reclamation r) {
        reclamationEnEdition = r;
        fieldObjet.setText(r.getObjet());
        fieldDescription.setText(r.getDescription());
        labelError.setText("");
        btnSubmitReclamation.setText("Modifier");
        formPanel.setVisible(true);
        formPanel.setManaged(true);
    }

    private void refreshTable() {
        ObservableList<Reclamation> data = FXCollections.observableArrayList(reclamationController.listerMesReclamations());
        tableReclamations.setItems(data);
    }
}