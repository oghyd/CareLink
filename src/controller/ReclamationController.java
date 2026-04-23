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
 * Règles métier (identiques à DemandeController) :
 *  - Création : étudiant, sur ses propres réclamations.
 *  - Modification : étudiant UNIQUEMENT si statut = CREE ou EN_COURS_DE_TRAITEMENT. Admin sans condition.
 *  - Suppression : admin uniquement.
 *  - Changement de statut : admin uniquement (traiterReclamation).
 *
 * Remarque : Reclamation réutilise l'enum StatutDemande (pas d'enum dédié dans les models).
 */
public class ReclamationController {

    private ReclamationDAO reclamationDAO;

    public ReclamationController() {
        this.reclamationDAO = new ReclamationDAO();
    }

    // Crée une nouvelle réclamation pour l'étudiant connecté
    public boolean creerReclamation(Reclamation reclamation) {
        if (!SessionManager.isEtudiant()) return false;
        if (reclamation == null) return false;
        int etudiantId = SessionManager.getCurrentUserId();
        return reclamationDAO.create(reclamation, etudiantId);
    }

    // Modifier une réclamation
    // - Admin : sans condition.
    // - Étudiant : uniquement si statut = CREE ou EN_COURS_DE_TRAITEMENT.
    public boolean modifierReclamation(Reclamation reclamation) {
        if (reclamation == null || !SessionManager.isLoggedIn()) return false;

        Reclamation existante = reclamationDAO.findById(reclamation.getId());
        if (existante == null) return false;

        if (SessionManager.isAdmin()) {
            return reclamationDAO.update(reclamation);
        }

        if (SessionManager.isEtudiant()) {
            StatutDemande statut = existante.getStatut();
            boolean modifiable = (statut == StatutDemande.CREE
                                  || statut == StatutDemande.EN_COURS_DE_TRAITEMENT);
            if (!modifiable) return false;
            return reclamationDAO.update(reclamation);
        }

        return false;
    }

    // Supprime une réclamation — admin uniquement
    public boolean supprimerReclamation(int reclamationId) {
        if (!SessionManager.isAdmin()) return false;
        return reclamationDAO.delete(reclamationId);
    }

    // Change le statut — admin uniquement
    public boolean traiterReclamation(int reclamationId, StatutDemande nouveauStatut) {
        if (!SessionManager.isAdmin()) return false;
        if (nouveauStatut == null) return false;
        return reclamationDAO.changerStatut(reclamationId, nouveauStatut);
    }

    // Retourne une réclamation précise
    public Reclamation consulterReclamation(int reclamationId) {
        if (!SessionManager.isLoggedIn()) return null;
        return reclamationDAO.findById(reclamationId);
    }

    // Liste toutes les réclamations — admin uniquement
    public List<Reclamation> listerReclamations() {
        if (!SessionManager.isAdmin()) return new ArrayList<>();
        return reclamationDAO.findAll();
    }

    // Liste les réclamations d'un étudiant — admin, OU l'étudiant lui-même
    public List<Reclamation> listerReclamationsEtudiant(int etudiantId) {
        if (!SessionManager.isLoggedIn()) return new ArrayList<>();
        if (SessionManager.isEtudiant() && SessionManager.getCurrentUserId() != etudiantId) {
            return new ArrayList<>();
        }
        return reclamationDAO.findByEtudiant(etudiantId);
    }

    // Raccourci : les réclamations de l'étudiant actuellement connecté
    public List<Reclamation> listerMesReclamations() {
        if (!SessionManager.isEtudiant()) return new ArrayList<>();
        return reclamationDAO.findByEtudiant(SessionManager.getCurrentUserId());
    }

    // Filtrage par statut — admin uniquement
    public List<Reclamation> filtrerParStatut(StatutDemande statut) {
        if (!SessionManager.isAdmin()) return new ArrayList<>();
        return reclamationDAO.findByStatut(statut);
    }
}
