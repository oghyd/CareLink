package src.controller;

import src.dao.DemandeDAO;
import src.dao.ReclamationDAO;
import src.model.Demande;
import src.model.Reclamation;
import src.model.StatutDemande;
import src.model.TypeDemande;
import src.utils.CsvExporter;
import src.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Gère la consultation de l'historique et la recherche multicritère.
 *
 * Couvre les demandes et les réclamations. Les filtrages sur date sont faits en
 * mémoire (les DAOs n'exposent pas de requête SQL par intervalle pour l'instant).
 *
 * Toutes les méthodes sont admin only — l'archivage est un outil de pilotage.
 */
public class ArchivageController {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private DemandeDAO demandeDAO;
    private ReclamationDAO reclamationDAO;

    public ArchivageController() {
        this.demandeDAO = new DemandeDAO();
        this.reclamationDAO = new ReclamationDAO();
    }

    public List<Demande> consulterHistoriqueDemandes() {
        if (!SessionManager.isAdmin()) return new ArrayList<>();
        return demandeDAO.findAll();
    }

    public List<Reclamation> consulterHistoriqueReclamations() {
        if (!SessionManager.isAdmin()) return new ArrayList<>();
        return reclamationDAO.findAll();
    }

    // Recherche multicritère sur les demandes. Critères null = pas de filtre.
    public List<Demande> rechercherDemandes(StatutDemande statut, TypeDemande type,
                                            Date dateDebut, Date dateFin) {
        if (!SessionManager.isAdmin()) return new ArrayList<>();

        List<Demande> filtres = new ArrayList<>();
        for (Demande d : demandeDAO.findAll()) {
            if (statut != null && d.getStatut() != statut) continue;
            if (type != null && d.getType() != type) continue;
            if (dateDebut != null && d.getDateCreation().before(dateDebut)) continue;
            if (dateFin != null && d.getDateCreation().after(dateFin)) continue;
            filtres.add(d);
        }
        return filtres;
    }

    public List<Reclamation> rechercherReclamations(StatutDemande statut,
                                                     Date dateDebut, Date dateFin) {
        if (!SessionManager.isAdmin()) return new ArrayList<>();

        List<Reclamation> filtres = new ArrayList<>();
        for (Reclamation r : reclamationDAO.findAll()) {
            if (statut != null && r.getStatut() != statut) continue;
            if (dateDebut != null && r.getDateCreation().before(dateDebut)) continue;
            if (dateFin != null && r.getDateCreation().after(dateFin)) continue;
            filtres.add(r);
        }
        return filtres;
    }

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

    public List<Demande> filtrerDemandesParDate(Date dateDebut, Date dateFin) {
        return rechercherDemandes(null, null, dateDebut, dateFin);
    }

    public List<Reclamation> filtrerReclamationsParDate(Date dateDebut, Date dateFin) {
        return rechercherReclamations(null, dateDebut, dateFin);
    }

    // ===========================
    //          EXPORT
    // ===========================

    public boolean exporterDemandesCsv(String filePath, List<Demande> demandes) {
        if (!SessionManager.isAdmin()) return false;
        if (demandes == null) demandes = demandeDAO.findAll();

        String[] headers = { "ID", "Titre", "Description", "Type", "Statut", "Date création", "ID Étudiant" };
        List<String[]> rows = new ArrayList<>();
        for (Demande d : demandes) {
            rows.add(new String[] {
                String.valueOf(d.getId()),
                d.getTitre() != null ? d.getTitre() : "",
                d.getDescription() != null ? d.getDescription() : "",
                d.getType() != null ? d.getType().name() : "",
                d.getStatut() != null ? d.getStatut().name() : "",
                d.getDateCreation() != null ? DATE_FORMAT.format(d.getDateCreation()) : "",
                String.valueOf(d.getEtudiantId())
            });
        }
        return CsvExporter.export(filePath, headers, rows);
    }

    public boolean exporterReclamationsCsv(String filePath, List<Reclamation> reclamations) {
        if (!SessionManager.isAdmin()) return false;
        if (reclamations == null) reclamations = reclamationDAO.findAll();

        String[] headers = { "ID", "Objet", "Description", "Statut", "Date création", "ID Étudiant" };
        List<String[]> rows = new ArrayList<>();
        for (Reclamation r : reclamations) {
            rows.add(new String[] {
                String.valueOf(r.getId()),
                r.getObjet() != null ? r.getObjet() : "",
                r.getDescription() != null ? r.getDescription() : "",
                r.getStatut() != null ? r.getStatut().name() : "",
                r.getDateCreation() != null ? DATE_FORMAT.format(r.getDateCreation()) : "",
                String.valueOf(r.getEtudiantId())
            });
        }
        return CsvExporter.export(filePath, headers, rows);
    }

    public boolean exporterToutesDemandesCsv(String filePath) {
        return exporterDemandesCsv(filePath, null);
    }

    public boolean exporterToutesReclamationsCsv(String filePath) {
        return exporterReclamationsCsv(filePath, null);
    }
}