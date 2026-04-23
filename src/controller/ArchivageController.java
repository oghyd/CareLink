package src.controller;

import src.dao.DemandeDAO;
import src.dao.ReclamationDAO;
import src.model.Demande;
import src.model.Reclamation;
import src.model.StatutDemande;
import src.model.TypeDemande;
import src.utils.SessionManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Gère la consultation de l'historique et la recherche multicritère.
 *
 * Couvre à la fois les demandes et les réclamations (archivage complet).
 * Les filtrages sur date sont faits en mémoire (les DAOs n'exposent pas
 * de requête SQL par intervalle de dates pour l'instant).
 *
 * Toutes les méthodes sont admin only — l'archivage est un outil de pilotage.
 */
public class ArchivageController {

    private DemandeDAO demandeDAO;
    private ReclamationDAO reclamationDAO;

    public ArchivageController() {
        this.demandeDAO = new DemandeDAO();
        this.reclamationDAO = new ReclamationDAO();
    }

    // Retourne toutes les demandes (historique complet)
    public List<Demande> consulterHistoriqueDemandes() {
        if (!SessionManager.isAdmin()) return new ArrayList<>();
        return demandeDAO.findAll();
    }

    // Retourne toutes les réclamations (historique complet)
    public List<Reclamation> consulterHistoriqueReclamations() {
        if (!SessionManager.isAdmin()) return new ArrayList<>();
        return reclamationDAO.findAll();
    }

    // Recherche multicritère sur les demandes.
    // Tous les critères sont optionnels : null = pas de filtrage sur ce critère.
    public List<Demande> rechercherDemandes(StatutDemande statut, TypeDemande type,
                                            Date dateDebut, Date dateFin) {
        if (!SessionManager.isAdmin()) return new ArrayList<>();

        List<Demande> resultats = demandeDAO.findAll();
        List<Demande> filtres = new ArrayList<>();

        for (Demande d : resultats) {
            if (statut != null && d.getStatut() != statut) continue;
            if (type != null && d.getType() != type) continue;
            if (dateDebut != null && d.getDateCreation().before(dateDebut)) continue;
            if (dateFin != null && d.getDateCreation().after(dateFin)) continue;
            filtres.add(d);
        }
        return filtres;
    }

    // Recherche multicritère sur les réclamations.
    public List<Reclamation> rechercherReclamations(StatutDemande statut,
                                                     Date dateDebut, Date dateFin) {
        if (!SessionManager.isAdmin()) return new ArrayList<>();

        List<Reclamation> resultats = reclamationDAO.findAll();
        List<Reclamation> filtres = new ArrayList<>();

        for (Reclamation r : resultats) {
            if (statut != null && r.getStatut() != statut) continue;
            if (dateDebut != null && r.getDateCreation().before(dateDebut)) continue;
            if (dateFin != null && r.getDateCreation().after(dateFin)) continue;
            filtres.add(r);
        }
        return filtres;
    }

    // Recherche textuelle (sur titre et description des demandes)
    public List<Demande> rechercherDemandesParMotCle(String motCle) {
        if (!SessionManager.isAdmin() || motCle == null || motCle.isEmpty()) {
            return new ArrayList<>();
        }
        String cle = motCle.toLowerCase();
        List<Demande> resultats = new ArrayList<>();
        for (Demande d : demandeDAO.findAll()) {
            String titre = d.getTitre() != null ? d.getTitre().toLowerCase() : "";
            String desc = d.getDescription() != null ? d.getDescription().toLowerCase() : "";
            if (titre.contains(cle) || desc.contains(cle)) {
                resultats.add(d);
            }
        }
        return resultats;
    }

    // Recherche textuelle (sur objet et description des réclamations)
    public List<Reclamation> rechercherReclamationsParMotCle(String motCle) {
        if (!SessionManager.isAdmin() || motCle == null || motCle.isEmpty()) {
            return new ArrayList<>();
        }
        String cle = motCle.toLowerCase();
        List<Reclamation> resultats = new ArrayList<>();
        for (Reclamation r : reclamationDAO.findAll()) {
            String objet = r.getObjet() != null ? r.getObjet().toLowerCase() : "";
            String desc = r.getDescription() != null ? r.getDescription().toLowerCase() : "";
            if (objet.contains(cle) || desc.contains(cle)) {
                resultats.add(r);
            }
        }
        return resultats;
    }

    // Filtre spécifique par date (demandes)
    public List<Demande> filtrerDemandesParDate(Date dateDebut, Date dateFin) {
        return rechercherDemandes(null, null, dateDebut, dateFin);
    }

    // Filtre spécifique par date (réclamations)
    public List<Reclamation> filtrerReclamationsParDate(Date dateDebut, Date dateFin) {
        return rechercherReclamations(null, dateDebut, dateFin);
    }
}
