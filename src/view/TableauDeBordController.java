/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
 
 package src.view;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import src.controller.StatistiquesController;
import src.model.StatutDemande;
import src.model.TypeDemande;

import java.time.Year;
import java.util.Map;

/**
 * FXML Controller class
 *
 * @author oghyd
 */
 
public class TableauDeBordController {

    @FXML private HBox cardsRow;
    @FXML private PieChart demandesParStatutChart;
    @FXML private PieChart reclamationsParStatutChart;
    @FXML private BarChart<String, Number> demandesParTypeChart;
    @FXML private BarChart<String, Number> annualChart;
    @FXML private ComboBox<Integer> yearPicker;

    private StatistiquesController statsCtrl = new StatistiquesController();

    @FXML
    private void initialize() {
        buildCards();
        buildDemandesParStatut();
        buildReclamationsParStatut();
        buildDemandesParType();
        setupYearPicker();
    }

    // Summary cards

    private void buildCards() {
        Map<String, Integer> data = statsCtrl.consulterTableauDeBord();

        cardsRow.getChildren().addAll(
            card("Demandes", data.getOrDefault("totalDemandes", 0), "#3498db"),
            card("Réclamations", data.getOrDefault("totalReclamations", 0), "#e67e22"),
            card("Étudiants", data.getOrDefault("totalEtudiants", 0), "#2ecc71"),
            card("En cours", data.getOrDefault("demandesEnCours", 0), "#9b59b6")
        );
    }

    private VBox card(String label, int value, String color) {
        Label valueLabel = new Label(String.valueOf(value));
        valueLabel.setStyle("-fx-font-size: 28; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        Label nameLabel = new Label(label);
        nameLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #7f8c8d;");

        VBox box = new VBox(4, valueLabel, nameLabel);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: white; -fx-padding: 18 24; -fx-background-radius: 10; "
                   + "-fx-border-color: " + color + "; -fx-border-width: 0 0 3 0; -fx-border-radius: 10;");
        box.setMinWidth(140);
        HBox.setHgrow(box, Priority.ALWAYS);
        return box;
    }


    private void buildDemandesParStatut() {
        Map<StatutDemande, Integer> data = statsCtrl.getNombreDemandesParStatut();
        for (Map.Entry<StatutDemande, Integer> entry : data.entrySet()) {
            if (entry.getValue() > 0) {
                demandesParStatutChart.getData().add(
                    new PieChart.Data(formatStatut(entry.getKey()) + " (" + entry.getValue() + ")", entry.getValue())
                );
            }
        }
    }


    private void buildReclamationsParStatut() {
        Map<StatutDemande, Integer> data = statsCtrl.getNombreReclamationsParStatut();
        for (Map.Entry<StatutDemande, Integer> entry : data.entrySet()) {
            if (entry.getValue() > 0) {
                reclamationsParStatutChart.getData().add(
                    new PieChart.Data(formatStatut(entry.getKey()) + " (" + entry.getValue() + ")", entry.getValue())
                );
            }
        }
    }


    private void buildDemandesParType() {
        Map<TypeDemande, Integer> data = statsCtrl.getNombreDemandesParType();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Map.Entry<TypeDemande, Integer> entry : data.entrySet()) {
            series.getData().add(new XYChart.Data<>(formatType(entry.getKey()), entry.getValue()));
        }
        demandesParTypeChart.getData().add(series);
    }



    private void setupYearPicker() {
        int now = Year.now().getValue();
        yearPicker.setItems(FXCollections.observableArrayList(now - 2, now - 1, now));
        yearPicker.setValue(now);
        refreshAnnualChart(now);
    }

    @FXML
    private void handleYearChange() {
        if (yearPicker.getValue() != null) {
            refreshAnnualChart(yearPicker.getValue());
        }
    }

    private void refreshAnnualChart(int year) {
        annualChart.getData().clear();
        annualChart.setTitle("Répartition annuelle — " + year);

        Map<StatutDemande, Integer> demandes = statsCtrl.getStatistiquesDemandesAnnuelles(year);
        Map<StatutDemande, Integer> reclamations = statsCtrl.getStatistiquesReclamationsAnnuelles(year);

        XYChart.Series<String, Number> seriesD = new XYChart.Series<>();
        seriesD.setName("Demandes");

        XYChart.Series<String, Number> seriesR = new XYChart.Series<>();
        seriesR.setName("Réclamations");

        for (StatutDemande s : StatutDemande.values()) {
            seriesD.getData().add(new XYChart.Data<>(formatStatut(s), demandes.getOrDefault(s, 0)));
            seriesR.getData().add(new XYChart.Data<>(formatStatut(s), reclamations.getOrDefault(s, 0)));
        }

        annualChart.getData().addAll(seriesD, seriesR);
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
}