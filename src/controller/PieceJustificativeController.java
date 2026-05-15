package src.controller;

import src.dao.PieceJustificativeDAO;
import src.model.PieceJustificative;
import src.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Gère les pièces justificatives rattachées à une demande.
 *
 * Règles métier :
 *  - Ajout : étudiant (sur ses propres demandes) ou admin.
 *  - Suppression : admin uniquement (pour trace/archivage).
 *  - Consultation : tout utilisateur connecté.
 */
public class PieceJustificativeController {

    private PieceJustificativeDAO pieceDAO;

    public PieceJustificativeController() {
        this.pieceDAO = new PieceJustificativeDAO();
    }

    public boolean ajouterPiece(PieceJustificative piece, int demandeId) {
        if (!SessionManager.isLoggedIn()) return false;
        if (piece == null) return false;
        return pieceDAO.create(piece, demandeId);
    }

    public boolean supprimerPiece(int pieceId) {
        if (!SessionManager.isAdmin()) return false;
        return pieceDAO.delete(pieceId);
    }

    public List<PieceJustificative> listerPieces(int demandeId) {
        if (!SessionManager.isLoggedIn()) return new ArrayList<>();
        return pieceDAO.findByDemande(demandeId);
    }

    public PieceJustificative consulterPiece(int pieceId) {
        if (!SessionManager.isLoggedIn()) return null;
        return pieceDAO.findById(pieceId);
    }

    // ============================================================
    // MÉTHODE AJOUTÉE PAR IDRISS POUR GestionDemandesController
    // ============================================================

    /**
     * Récupère la première pièce jointe d'une demande
     * (si une demande a plusieurs pièces, retourne la première)
     * Appelée depuis GestionDemandesController.showDemandeDetails()
     */
    public PieceJustificative getPieceByDemande(int demandeId) {
        if (!SessionManager.isLoggedIn()) return null;
        List<PieceJustificative> pieces = pieceDAO.findByDemande(demandeId);
        if (pieces != null && !pieces.isEmpty()) {
            return pieces.get(0);
        }
        return null;
    }
}