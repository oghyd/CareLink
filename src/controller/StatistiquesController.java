package src.controller;

import src.dao.DemandeDAO;
import src.dao.EtudiantDAO;
import src.dao.ReclamationDAO;
import src.model.Demande;
import src.model.Reclamation;
import src.model.StatutDemande;
import src.model.TypeDemande;
import src.utils.CsvExporter;
import src.utils.SessionManager;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fournit les chiffres du tableau de bord et les statistiques.
 *
 * Toutes les méthodes sont admin only.
 */
public class StatistiquesController {

    private DemandeDAO demandeDAO;
    private ReclamationDAO reclamationDAO;
    private EtudiantDAO etudiantDAO;

    public StatistiquesController() {
        this.demandeDAO = new DemandeDAO();
        this.reclamationDAO = new ReclamationDAO();
        this.etudiantDAO = new EtudiantDAO();
    }

    public Map<StatutDemande, Integer> getNombreDemandesParStatut() {
        Map<StatutDemande, Integer> stats = new EnumMap<>(StatutDemande.class);
        if (!SessionManager.isAdmin()) return stats;
        for (StatutDemande s : StatutDemande.values()) {
            stats.put(s, demandeDAO.countByStatut(s));
        }
        return stats;
    }

    public Map<StatutDemande, Integer> getNombreReclamationsParStatut() {
        Map<StatutDemande, Integer> stats = new EnumMap<>(StatutDemande.class);
        if (!SessionManager.isAdmin()) return stats;
        for (StatutDemande s : StatutDemande.values()) {
            stats.put(s, reclamationDAO.countByStatut(s));
        }
        return stats;
    }

    public Map<TypeDemande, Integer> getNombreDemandesParType() {
        Map<TypeDemande, Integer> stats = new EnumMap<>(TypeDemande.class);
        if (!SessionManager.isAdmin()) return stats;
        for (TypeDemande t : TypeDemande.values()) {
            stats.put(t, demandeDAO.findByType(t).size());
        }
        return stats;
    }

    public int getTotalDemandes() {
        if (!SessionManager.isAdmin()) return 0;
        return demandeDAO.findAll().size();
    }

    public int getTotalReclamations() {
        if (!SessionManager.isAdmin()) return 0;
        return reclamationDAO.findAll().size();
    }

    public int getTotalEtudiants() {
        if (!SessionManager.isAdmin()) return 0;
        return etudiantDAO.findAll().size();
    }

    public Map<StatutDemande, Integer> getStatistiquesDemandesAnnuelles(int annee) {
        Map<StatutDemande, Integer> stats = new EnumMap<>(StatutDemande.class);
        if (!SessionManager.isAdmin()) return stats;
        for (StatutDemande s : StatutDemande.values()) stats.put(s, 0);

        List<Demande> demandes = demandeDAO.findByAnnee(annee);
        for (Demande d : demandes) {
            stats.merge(d.getStatut(), 1, Integer::sum);
        }
        return stats;
    }

    public Map<StatutDemande, Integer> getStatistiquesReclamationsAnnuelles(int annee) {
        Map<StatutDemande, Integer> stats = new EnumMap<>(StatutDemande.class);
        if (!SessionManager.isAdmin()) return stats;
        for (StatutDemande s : StatutDemande.values()) stats.put(s, 0);

        List<Reclamation> reclamations = reclamationDAO.findByAnnee(annee);
        for (Reclamation r : reclamations) {
            stats.merge(r.getStatut(), 1, Integer::sum);
        }
        return stats;
    }

    public Map<String, Integer> consulterTableauDeBord() {
        Map<String, Integer> dashboard = new HashMap<>();
        if (!SessionManager.isAdmin()) return dashboard;

        dashboard.put("totalDemandes", getTotalDemandes());
        dashboard.put("totalReclamations", getTotalReclamations());
        dashboard.put("totalEtudiants", getTotalEtudiants());
        dashboard.put("demandesEnCours", demandeDAO.countByStatut(StatutDemande.EN_COURS_DE_TRAITEMENT));
        dashboard.put("demandesTraitees", demandeDAO.countByStatut(StatutDemande.TRAITEE));
        dashboard.put("demandesCrees", demandeDAO.countByStatut(StatutDemande.CREE));
        dashboard.put("demandesRejetees", demandeDAO.countByStatut(StatutDemande.REJETE));
        return dashboard;
    }

    // ===========================
    //          EXPORT
    // ===========================

    public boolean exporterTableauDeBordCsv(String filePath) {
        if (!SessionManager.isAdmin()) return false;
        Map<String, Integer> dashboard = consulterTableauDeBord();
        String[] headers = { "Indicateur", "Valeur" };
        List<String[]> rows = new ArrayList<>();
        for (Map.Entry<String, Integer> e : dashboard.entrySet()) {
            rows.add(new String[] { e.getKey(), String.valueOf(e.getValue()) });
        }
        return CsvExporter.export(filePath, headers, rows);
    }

    public boolean exporterStatistiquesParStatutCsv(String filePath) {
        if (!SessionManager.isAdmin()) return false;
        Map<StatutDemande, Integer> demandes = getNombreDemandesParStatut();
        Map<StatutDemande, Integer> reclamations = getNombreReclamationsParStatut();

        String[] headers = { "Statut", "Nombre demandes", "Nombre réclamations" };
        List<String[]> rows = new ArrayList<>();
        for (StatutDemande s : StatutDemande.values()) {
            rows.add(new String[] {
                s.name(),
                String.valueOf(demandes.getOrDefault(s, 0)),
                String.valueOf(reclamations.getOrDefault(s, 0))
            });
        }
        return CsvExporter.export(filePath, headers, rows);
    }

    public boolean exporterStatistiquesAnnuellesCsv(String filePath, int annee) {
        if (!SessionManager.isAdmin()) return false;
        Map<StatutDemande, Integer> demandes = getStatistiquesDemandesAnnuelles(annee);
        Map<StatutDemande, Integer> reclamations = getStatistiquesReclamationsAnnuelles(annee);

        String[] headers = { "Année", "Statut", "Nombre demandes", "Nombre réclamations" };
        List<String[]> rows = new ArrayList<>();
        for (StatutDemande s : StatutDemande.values()) {
            rows.add(new String[] {
                String.valueOf(annee),
                s.name(),
                String.valueOf(demandes.getOrDefault(s, 0)),
                String.valueOf(reclamations.getOrDefault(s, 0))
            });
        }
        return CsvExporter.export(filePath, headers, rows);
    }
}