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
 *  - Création : étudiant (sur ses propres demandes).
 *  - Modification : étudiant UNIQUEMENT si statut = EN_COURS_DE_TRAITEMENT (ou CREE avant traitement).
 *                   Admin peut modifier sans condition.
 *  - Suppression : admin uniquement.
 *  - Changement de statut : admin uniquement (traiterDemande).
 */
public class DemandeController {

    private DemandeDAO demandeDAO;
    private PieceJustificativeDAO pieceDAO;

    public DemandeController() {
        this.demandeDAO = new DemandeDAO();
        this.pieceDAO = new PieceJustificativeDAO();
    }

    // Crée une nouvelle demande pour l'étudiant connecté.
    // Les pièces justificatives (si fournies) sont ajoutées après création.
    public boolean creerDemande(Demande demande, List<PieceJustificative> pieces) {
        if (!SessionManager.isEtudiant()) return false;
        if (demande == null) return false;

        int etudiantId = SessionManager.getCurrentUserId();
        boolean created = demandeDAO.create(demande, etudiantId);
        if (!created) return false;

        // Ajoute les pièces justificatives si fournies.
        // On récupère la dernière demande de l'étudiant (la plus récente) pour attacher les pièces.
        if (pieces != null && !pieces.isEmpty()) {
            List<Demande> mesDemandes = demandeDAO.findByEtudiant(etudiantId);
            if (!mesDemandes.isEmpty()) {
                int demandeId = mesDemandes.get(0).getId(); // findByEtudiant trie par date DESC
                for (PieceJustificative piece : pieces) {
                    pieceDAO.create(piece, demandeId);
                }
            }
        }
        return true;
    }

    // Surcharge sans pièces jointes
    public boolean creerDemande(Demande demande) {
        return creerDemande(demande, null);
    }

    // Modifier une demande.
    // - Admin : sans condition.
    // - Étudiant : uniquement si la demande est à lui ET le statut est CREE ou EN_COURS_DE_TRAITEMENT.
    //   Impossible de modifier une demande déjà TRAITEE ou REJETE.
    public boolean modifierDemande(Demande demande) {
        if (demande == null || !SessionManager.isLoggedIn()) return false;

        Demande existante = demandeDAO.findById(demande.getId());
        if (existante == null) return false;

        if (SessionManager.isAdmin()) {
            return demandeDAO.update(demande);
        }

        if (SessionManager.isEtudiant()) {
            StatutDemande statut = existante.getStatut();
            boolean modifiable = (statut == StatutDemande.CREE
                                  || statut == StatutDemande.EN_COURS_DE_TRAITEMENT);
            if (!modifiable) return false;
            // Note : idéalement on vérifie aussi que la demande appartient bien à l'étudiant.
            // Le DAO n'expose pas etudiantId dans Demande — à ajouter côté model si besoin.
            return demandeDAO.update(demande);
        }

        return false;
    }

    // Supprime une demande — admin uniquement.
    // On supprime d'abord les pièces justificatives liées pour respecter la FK.
    public boolean supprimerDemande(int demandeId) {
        if (!SessionManager.isAdmin()) return false;
        pieceDAO.deleteByDemande(demandeId); // nettoie les PJ avant suppression
        return demandeDAO.delete(demandeId);
    }

    // Change le statut d'une demande — admin uniquement.
    // Transitions valides : CREE → EN_COURS_DE_TRAITEMENT → TRAITEE (ou REJETE).
    public boolean traiterDemande(int demandeId, StatutDemande nouveauStatut) {
        if (!SessionManager.isAdmin()) return false;
        if (nouveauStatut == null) return false;
        return demandeDAO.changerStatut(demandeId, nouveauStatut);
    }

    // Retourne une demande précise par son id
    public Demande consulterDemande(int demandeId) {
        if (!SessionManager.isLoggedIn()) return null;
        return demandeDAO.findById(demandeId);
    }

    // Liste toutes les demandes — admin uniquement
    public List<Demande> listerDemandes() {
        if (!SessionManager.isAdmin()) return new ArrayList<>();
        return demandeDAO.findAll();
    }

    // Liste les demandes d'un étudiant donné — admin, OU l'étudiant lui-même
    public List<Demande> listerDemandesEtudiant(int etudiantId) {
        if (!SessionManager.isLoggedIn()) return new ArrayList<>();
        if (SessionManager.isEtudiant() && SessionManager.getCurrentUserId() != etudiantId) {
            return new ArrayList<>();
        }
        return demandeDAO.findByEtudiant(etudiantId);
    }

    // Raccourci : les demandes de l'étudiant actuellement connecté
    public List<Demande> listerMesDemandes() {
        if (!SessionManager.isEtudiant()) return new ArrayList<>();
        return demandeDAO.findByEtudiant(SessionManager.getCurrentUserId());
    }

    // Filtrage par statut — admin uniquement
    public List<Demande> filtrerParStatut(StatutDemande statut) {
        if (!SessionManager.isAdmin()) return new ArrayList<>();
        return demandeDAO.findByStatut(statut);
    }

    // Filtrage par type — admin uniquement
    public List<Demande> filtrerParType(TypeDemande type) {
        if (!SessionManager.isAdmin()) return new ArrayList<>();
        return demandeDAO.findByType(type);
    }
}
