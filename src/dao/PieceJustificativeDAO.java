package src.dao;

import src.model.PieceJustificative;
import src.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PieceJustificativeDAO {

    // Ajoute une pièce justificative liée à une demande
    public boolean create(PieceJustificative piece, int demandeId) {
        String sql = "INSERT INTO pieces_justificatives (nom_fichier, chemin, date_ajout, demande_id) "
                   + "VALUES (?, ?, NOW(), ?)";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, piece.getNomFichier());
            ps.setString(2, piece.getChemin());
            ps.setInt(3, demandeId);
            int rows = ps.executeUpdate();
            ps.close(); conn.close();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Erreur create piece justificative : " + e.getMessage());
        }

        return false;
    }

    // Récupère une pièce justificative par son id
    public PieceJustificative findById(int id) {
        String sql = "SELECT * FROM pieces_justificatives WHERE id = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                PieceJustificative p = mapToPiece(rs);
                rs.close(); ps.close(); conn.close();
                return p;
            }

            rs.close(); ps.close(); conn.close();
        } catch (SQLException e) {
            System.out.println("Erreur findById piece justificative : " + e.getMessage());
        }

        return null;
    }

    // Récupère toutes les pièces justificatives d'une demande
    public List<PieceJustificative> findByDemande(int demandeId) {
        List<PieceJustificative> pieces = new ArrayList<>();
        String sql = "SELECT * FROM pieces_justificatives WHERE demande_id = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, demandeId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                pieces.add(mapToPiece(rs));
            }

            rs.close(); ps.close(); conn.close();
        } catch (SQLException e) {
            System.out.println("Erreur findByDemande pieces justificatives : " + e.getMessage());
        }

        return pieces;
    }

    // Supprime une pièce justificative par son id
    public boolean delete(int id) {
        String sql = "DELETE FROM pieces_justificatives WHERE id = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            ps.close(); conn.close();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Erreur delete piece justificative : " + e.getMessage());
        }

        return false;
    }

    // Supprime toutes les pièces d'une demande (appelé avant delete demande)
    public boolean deleteByDemande(int demandeId) {
        String sql = "DELETE FROM pieces_justificatives WHERE demande_id = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, demandeId);
            ps.executeUpdate();
            ps.close(); conn.close();
            return true;
        } catch (SQLException e) {
            System.out.println("Erreur deleteByDemande pieces justificatives : " + e.getMessage());
        }

        return false;
    }

    // Transforme une ligne ResultSet en objet PieceJustificative
    private PieceJustificative mapToPiece(ResultSet rs) throws SQLException {
        return new PieceJustificative(
            rs.getInt("id"),
            rs.getString("nom_fichier"),
            rs.getString("chemin"),
            rs.getDate("date_ajout")
        );
    }
}
