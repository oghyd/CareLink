package src.controller;

import src.dao.DemandeDAO;
import src.dao.PieceJustificativeDAO;
import src.model.Demande;
import src.model.PieceJustificative;
import src.model.StatutDemande;
import src.model.TypeDemande;
import src.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Gère les demandes (CRUD + règles métier).
 *
 * Règles métier issues des diagrammes UC :
 *  - Création : étudiant (sur ses propres demandes uniquement).
 *  - Modification : étudiant UNIQUEMENT si la demande lui appartient ET si le statut est
 *                   CREE ou EN_COURS_DE_TRAITEMENT. Admin sans condition.
 *  - Suppression : admin uniquement.
 *  - Changement de statut : admin uniquement.
 */
public class DemandeController {

    private DemandeDAO demandeDAO;
    private PieceJustificativeDAO pieceDAO;

    public DemandeController() {
        this.demandeDAO = new DemandeDAO();
        this.pieceDAO = new PieceJustificativeDAO();
    }

    // Crée une nouvelle demande pour l'étudiant connecté + ses éventuelles PJ.
    public boolean creerDemande(Demande demande, List<PieceJustificative> pieces) {
        if (!SessionManager.isEtudiant()) return false;
        if (demande == null) return false;

        int etudiantId = SessionManager.getCurrentUserId();
        demande.setEtudiantId(etudiantId); // force le propriétaire
        boolean created = demandeDAO.create(demande, etudiantId);
        if (!created) return false;

        // Récupère l'id de la demande qu'on vient de créer pour attacher les PJ
        if (pieces != null && !pieces.isEmpty()) {
            List<Demande> mesDemandes = demandeDAO.findByEtudiant(etudiantId);
            if (!mesDemandes.isEmpty()) {
                int demandeId = mesDemandes.get(0).getId(); // findByEtudiant trie DESC
                for (PieceJustificative piece : pieces) {
                    pieceDAO.create(piece, demandeId);
                }
            }
        }
        return true;
    }

    public boolean creerDemande(Demande demande) {
        return creerDemande(demande, null);
    }

    // Modifier une demande.
    // - Admin : sans condition.
    // - Étudiant : doit être propriétaire ET statut ∈ {CREE, EN_COURS_DE_TRAITEMENT}.
    public boolean modifierDemande(Demande demande) {
        if (demande == null || !SessionManager.isLoggedIn()) return false;

        Demande existante = demandeDAO.findById(demande.getId());
        if (existante == null) return false;

        if (SessionManager.isAdmin()) {
            return demandeDAO.update(demande);
        }

        if (SessionManager.isEtudiant()) {
            // Ownership : la demande doit appartenir à l'étudiant connecté
            if (existante.getEtudiantId() != SessionManager.getCurrentUserId()) {
                return false;
            }
            StatutDemande statut = existante.getStatut();
            boolean modifiable = (statut == StatutDemande.CREE
                                  || statut == StatutDemande.EN_COURS_DE_TRAITEMENT);
            if (!modifiable) return false;
            // Empêche un étudiant de réassigner sa demande à un autre étudiant
            demande.setEtudiantId(existante.getEtudiantId());
            return demandeDAO.update(demande);
        }

        return false;
    }

    // Supprime une demande — admin uniquement. Nettoie d'abord les PJ liées (FK).
    public boolean supprimerDemande(int demandeId) {
        if (!SessionManager.isAdmin()) return false;
        pieceDAO.deleteByDemande(demandeId);
        return demandeDAO.delete(demandeId);
    }

    // Change le statut d'une demande — admin uniquement.
    public boolean traiterDemande(int demandeId, StatutDemande nouveauStatut) {
        if (!SessionManager.isAdmin()) return false;
        if (nouveauStatut == null) return false;
        return demandeDAO.changerStatut(demandeId, nouveauStatut);
    }

    // Consulte une demande. Un étudiant ne peut consulter que ses propres demandes.
    public Demande consulterDemande(int demandeId) {
        if (!SessionManager.isLoggedIn()) return null;
        Demande d = demandeDAO.findById(demandeId);
        if (d == null) return null;
        if (SessionManager.isEtudiant() && d.getEtudiantId() != SessionManager.getCurrentUserId()) {
            return null;
        }
        return d;
    }

    public List<Demande> listerDemandes() {
        if (!SessionManager.isAdmin()) return new ArrayList<>();
        return demandeDAO.findAll();
    }

    public List<Demande> listerDemandesEtudiant(int etudiantId) {
        if (!SessionManager.isLoggedIn()) return new ArrayList<>();
        if (SessionManager.isEtudiant() && SessionManager.getCurrentUserId() != etudiantId) {
            return new ArrayList<>();
        }
        return demandeDAO.findByEtudiant(etudiantId);
    }

    public List<Demande> listerMesDemandes() {
        if (!SessionManager.isEtudiant()) return new ArrayList<>();
        return demandeDAO.findByEtudiant(SessionManager.getCurrentUserId());
    }

    public List<Demande> filtrerParStatut(StatutDemande statut) {
        if (!SessionManager.isAdmin()) return new ArrayList<>();
        return demandeDAO.findByStatut(statut);
    }

    public List<Demande> filtrerParType(TypeDemande type) {
        if (!SessionManager.isAdmin()) return new ArrayList<>();
        return demandeDAO.findByType(type);
    }
}