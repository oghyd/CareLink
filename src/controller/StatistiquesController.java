package src.controller;

import src.dao.DemandeDAO;
import src.dao.EtudiantDAO;
import src.dao.ReclamationDAO;
import src.model.Demande;
import src.model.Reclamation;
import src.model.StatutDemande;
import src.model.TypeDemande;
import src.utils.SessionManager;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fournit les chiffres du tableau de bord et les statistiques.
 *
 * Toutes les méthodes sont admin only : les stats servent au pilotage de la
 * politique d'inclusion.
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

    // Compte toutes les demandes, groupées par statut
    public Map<StatutDemande, Integer> getNombreDemandesParStatut() {
        Map<StatutDemande, Integer> stats = new EnumMap<>(StatutDemande.class);
        if (!SessionManager.isAdmin()) return stats;
        for (StatutDemande s : StatutDemande.values()) {
            stats.put(s, demandeDAO.countByStatut(s));
        }
        return stats;
    }

    // Compte toutes les réclamations, groupées par statut
    public Map<StatutDemande, Integer> getNombreReclamationsParStatut() {
        Map<StatutDemande, Integer> stats = new EnumMap<>(StatutDemande.class);
        if (!SessionManager.isAdmin()) return stats;
        for (StatutDemande s : StatutDemande.values()) {
            stats.put(s, reclamationDAO.countByStatut(s));
        }
        return stats;
    }

    // Compte les demandes par type
    public Map<TypeDemande, Integer> getNombreDemandesParType() {
        Map<TypeDemande, Integer> stats = new EnumMap<>(TypeDemande.class);
        if (!SessionManager.isAdmin()) return stats;
        for (TypeDemande t : TypeDemande.values()) {
            stats.put(t, demandeDAO.findByType(t).size());
        }
        return stats;
    }

    // Total de demandes
    public int getTotalDemandes() {
        if (!SessionManager.isAdmin()) return 0;
        return demandeDAO.findAll().size();
    }

    // Total de réclamations
    public int getTotalReclamations() {
        if (!SessionManager.isAdmin()) return 0;
        return reclamationDAO.findAll().size();
    }

    // Total d'étudiants inscrits
    public int getTotalEtudiants() {
        if (!SessionManager.isAdmin()) return 0;
        return etudiantDAO.findAll().size();
    }

    // Statistiques annuelles : demandes par statut pour une année
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

    // Statistiques annuelles : réclamations par statut pour une année
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

    // Résumé global du tableau de bord : un paquet de compteurs pour l'écran Dashboard Admin
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
}
