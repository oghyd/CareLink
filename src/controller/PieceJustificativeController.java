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

    // Ajoute une pièce justificative à une demande
    public boolean ajouterPiece(PieceJustificative piece, int demandeId) {
        if (!SessionManager.isLoggedIn()) return false;
        if (piece == null) return false;
        return pieceDAO.create(piece, demandeId);
    }

    // Supprime une pièce justificative — admin uniquement
    public boolean supprimerPiece(int pieceId) {
        if (!SessionManager.isAdmin()) return false;
        return pieceDAO.delete(pieceId);
    }

    // Liste les pièces justificatives d'une demande
    public List<PieceJustificative> listerPieces(int demandeId) {
        if (!SessionManager.isLoggedIn()) return new ArrayList<>();
        return pieceDAO.findByDemande(demandeId);
    }

    // Consulte une pièce par son id
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
            return pieces.get(0); // retourne la première pièce
        }
        return null;
    }
}