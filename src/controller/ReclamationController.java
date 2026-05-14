package src.controller;

import src.dao.ReclamationDAO;
import src.model.Reclamation;
import src.model.StatutDemande;
import src.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Gère les réclamations (CRUD + règles métier).
 *
 * Mêmes règles que DemandeController :
 *  - Création : étudiant, sur ses propres réclamations.
 *  - Modification : étudiant propriétaire ET statut ∈ {CREE, EN_COURS_DE_TRAITEMENT}.
 *                   Admin sans condition.
 *  - Suppression : admin uniquement.
 *  - Changement de statut : admin uniquement.
 */
public class ReclamationController {

    private ReclamationDAO reclamationDAO;

    public ReclamationController() {
        this.reclamationDAO = new ReclamationDAO();
    }

    public boolean creerReclamation(Reclamation reclamation) {
        if (!SessionManager.isEtudiant()) return false;
        if (reclamation == null) return false;
        int etudiantId = SessionManager.getCurrentUserId();
        reclamation.setEtudiantId(etudiantId);
        return reclamationDAO.create(reclamation, etudiantId);
    }

    public boolean modifierReclamation(Reclamation reclamation) {
        if (reclamation == null || !SessionManager.isLoggedIn()) return false;

        Reclamation existante = reclamationDAO.findById(reclamation.getId());
        if (existante == null) return false;

        if (SessionManager.isAdmin()) {
            return reclamationDAO.update(reclamation);
        }

        if (SessionManager.isEtudiant()) {
            if (existante.getEtudiantId() != SessionManager.getCurrentUserId()) {
                return false;
            }
            StatutDemande statut = existante.getStatut();
            boolean modifiable = (statut == StatutDemande.CREE
                                  || statut == StatutDemande.EN_COURS_DE_TRAITEMENT);
            if (!modifiable) return false;
            reclamation.setEtudiantId(existante.getEtudiantId());
            return reclamationDAO.update(reclamation);
        }

        return false;
    }

    public boolean supprimerReclamation(int reclamationId) {
        if (!SessionManager.isAdmin()) return false;
        return reclamationDAO.delete(reclamationId);
    }

    public boolean traiterReclamation(int reclamationId, StatutDemande nouveauStatut) {
        if (!SessionManager.isAdmin()) return false;
        if (nouveauStatut == null) return false;
        return reclamationDAO.changerStatut(reclamationId, nouveauStatut);
    }

    public Reclamation consulterReclamation(int reclamationId) {
        if (!SessionManager.isLoggedIn()) return null;
        Reclamation r = reclamationDAO.findById(reclamationId);
        if (r == null) return null;
        if (SessionManager.isEtudiant() && r.getEtudiantId() != SessionManager.getCurrentUserId()) {
            return null;
        }
        return r;
    }

    public List<Reclamation> listerReclamations() {
        if (!SessionManager.isAdmin()) return new ArrayList<>();
        return reclamationDAO.findAll();
    }

    public List<Reclamation> listerReclamationsEtudiant(int etudiantId) {
        if (!SessionManager.isLoggedIn()) return new ArrayList<>();
        if (SessionManager.isEtudiant() && SessionManager.getCurrentUserId() != etudiantId) {
            return new ArrayList<>();
        }
        return reclamationDAO.findByEtudiant(etudiantId);
    }

    public List<Reclamation> listerMesReclamations() {
        if (!SessionManager.isEtudiant()) return new ArrayList<>();
        return reclamationDAO.findByEtudiant(SessionManager.getCurrentUserId());
    }

    public List<Reclamation> filtrerParStatut(StatutDemande statut) {
        if (!SessionManager.isAdmin()) return new ArrayList<>();
        return reclamationDAO.findByStatut(statut);
    }
}