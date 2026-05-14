/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
 
package src.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import src.controller.ArchivageController;
import src.model.Demande;
import src.model.Reclamation;
import src.model.StatutDemande;
import src.model.TypeDemande;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * FXML Controller class
 *
 * @author oghyd
 */
 
public class HistoriqueController {

    @FXML private ToggleButton tabDemandes;
    @FXML private ToggleButton tabReclamations;
    @FXML private TextField keywordField;
    @FXML private ComboBox<String> statutCombo;
    @FXML private ComboBox<String> typeCombo;
    @FXML private DatePicker dateDebut;
    @FXML private DatePicker dateFin;
    @FXML private TableView resultTable;

    private ArchivageController archivageCtrl = new ArchivageController();
    private SimpleDateFormat dateFmt = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private boolean showingDemandes = true;

    @FXML
    private void initialize() {
        // Link toggle buttons so only one is selected at a time
        ToggleGroup group = new ToggleGroup();
        tabDemandes.setToggleGroup(group);
        tabReclamations.setToggleGroup(group);

        // Populate combo boxes
        statutCombo.setItems(FXCollections.observableArrayList("Tous", "Créée", "En cours", "Traitée", "Rejetée"));
        statutCombo.setValue("Tous");

        typeCombo.setItems(FXCollections.observableArrayList("Tous", "Aménagement d'examen", "Accompagnement", "Accessibilité", "Autre"));
        typeCombo.setValue("Tous");

        // Initial load
        buildDemandeColumns();
        doSearch();
    }


    @FXML
    private void handleTabDemandes() {
        showingDemandes = true;
        typeCombo.setDisable(false);
        buildDemandeColumns();
        doSearch();
    }

    @FXML
    private void handleTabReclamations() {
        showingDemandes = false;
        typeCombo.setDisable(true);
        buildReclamationColumns();
        doSearch();
    }


    @FXML
    private void handleSearch() {
        doSearch();
    }

    @FXML
    private void handleReset() {
        keywordField.clear();
        statutCombo.setValue("Tous");
        typeCombo.setValue("Tous");
        dateDebut.setValue(null);
        dateFin.setValue(null);
        doSearch();
    }

    @SuppressWarnings("unchecked")
    private void doSearch() {
        String keyword = keywordField.getText().trim();
        StatutDemande statut = parseStatut(statutCombo.getValue());
        Date debut = toDate(dateDebut.getValue());
        Date fin = toDate(dateFin.getValue());

        if (showingDemandes) {
            List<Demande> results;
            if (!keyword.isEmpty()) {
                results = archivageCtrl.rechercherDemandesParMotCle(keyword);
            } else {
                TypeDemande type = parseType(typeCombo.getValue());
                results = archivageCtrl.rechercherDemandes(statut, type, debut, fin);
            }
            resultTable.setItems(FXCollections.observableArrayList(results));
        } else {
            List<Reclamation> results;
            if (!keyword.isEmpty()) {
                results = archivageCtrl.rechercherReclamationsParMotCle(keyword);
            } else {
                results = archivageCtrl.rechercherReclamations(statut, debut, fin);
            }
            resultTable.setItems(FXCollections.observableArrayList(results));
        }
    }


    @SuppressWarnings("unchecked")
    private void buildDemandeColumns() {
        resultTable.getColumns().clear();

        TableColumn<Demande, String> id = new TableColumn<>("ID");
        id.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));

        TableColumn<Demande, String> titre = new TableColumn<>("Titre");
        titre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitre()));

        TableColumn<Demande, String> type = new TableColumn<>("Type");
        type.setCellValueFactory(c -> new SimpleStringProperty(formatType(c.getValue().getType())));

        TableColumn<Demande, String> statut = new TableColumn<>("Statut");
        statut.setCellValueFactory(c -> new SimpleStringProperty(formatStatut(c.getValue().getStatut())));

        TableColumn<Demande, String> date = new TableColumn<>("Date");
        date.setCellValueFactory(c -> {
            Date d = c.getValue().getDateCreation();
            return new SimpleStringProperty(d != null ? dateFmt.format(d) : "");
        });

        resultTable.getColumns().addAll(id, titre, type, statut, date);
    }

    @SuppressWarnings("unchecked")
    private void buildReclamationColumns() {
        resultTable.getColumns().clear();

        TableColumn<Reclamation, String> id = new TableColumn<>("ID");
        id.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));

        TableColumn<Reclamation, String> objet = new TableColumn<>("Objet");
        objet.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getObjet()));

        TableColumn<Reclamation, String> statut = new TableColumn<>("Statut");
        statut.setCellValueFactory(c -> new SimpleStringProperty(formatStatut(c.getValue().getStatut())));

        TableColumn<Reclamation, String> date = new TableColumn<>("Date");
        date.setCellValueFactory(c -> {
            Date d = c.getValue().getDateCreation();
            return new SimpleStringProperty(d != null ? dateFmt.format(d) : "");
        });

        resultTable.getColumns().addAll(id, objet, statut, date);
    }


    private StatutDemande parseStatut(String label) {
        if (label == null || label.equals("Tous")) return null;
        switch (label) {
            case "Créée":    return StatutDemande.CREE;
            case "En cours": return StatutDemande.EN_COURS_DE_TRAITEMENT;
            case "Traitée":  return StatutDemande.TRAITEE;
            case "Rejetée":  return StatutDemande.REJETE;
            default:         return null;
        }
    }

    private TypeDemande parseType(String label) {
        if (label == null || label.equals("Tous")) return null;
        switch (label) {
            case "Aménagement d'examen": return TypeDemande.AMENAGEMENT_EXAMEN; 
            case "Accompagnement": return TypeDemande.ACCOMPAGNEMENT;
            case "Accessibilité": return TypeDemande.ACCESSIBILITE;
            case "Autre":         return TypeDemande.AUTRE;
            default:              return null;
        }
    }

    private String formatStatut(StatutDemande s) {
        switch (s) {
            case CREE:                   return "Créée";
            case EN_COURS_DE_TRAITEMENT: return "En cours";
            case TRAITEE:                return "Traitée";
            case REJETE:                 return "Rejetée";
            default:                     return s.name();
        }
    }

    private String formatType(TypeDemande t) {
        switch (t) {
            case AMENAGEMENT_EXAMEN: return "Aménagement d'examen"; 
            case ACCOMPAGNEMENT: return "Accompagnement";
            case ACCESSIBILITE: return "Accessibilité";
            case AUTRE:         return "Autre";
            default:            return t.name();
        }
    }

    private Date toDate(LocalDate ld) {
        if (ld == null) return null;
        return Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}