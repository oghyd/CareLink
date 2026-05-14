package src.dao;

import src.model.Reclamation;
import src.model.StatutDemande;
import src.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReclamationDAO {

    // Crée une nouvelle réclamation, statut initial = CREE
    public boolean create(Reclamation reclamation, int etudiantId) {
        String sql = "INSERT INTO reclamations (objet, description, date_creation, statut, etudiant_id) "
                   + "VALUES (?, ?, NOW(), 'CREE', ?)";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, reclamation.getObjet());
            ps.setString(2, reclamation.getDescription());
            ps.setInt(3, etudiantId);
            int rows = ps.executeUpdate();
            ps.close(); conn.close();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Erreur create reclamation : " + e.getMessage());
        }

        return false;
    }

    // Récupère une réclamation par son id
    public Reclamation findById(int id) {
        String sql = "SELECT * FROM reclamations WHERE id = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Reclamation r = mapToReclamation(rs);
                rs.close(); ps.close(); conn.close();
                return r;
            }

            rs.close(); ps.close(); conn.close();
        } catch (SQLException e) {
            System.out.println("Erreur findById reclamation : " + e.getMessage());
        }

        return null;
    }

    // Récupère toutes les réclamations (vue admin)
    public List<Reclamation> findAll() {
        List<Reclamation> reclamations = new ArrayList<>();
        String sql = "SELECT * FROM reclamations ORDER BY date_creation DESC";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                reclamations.add(mapToReclamation(rs));
            }

            rs.close(); ps.close(); conn.close();
        } catch (SQLException e) {
            System.out.println("Erreur findAll reclamations : " + e.getMessage());
        }

        return reclamations;
    }

    // Récupère les réclamations d'un étudiant (vue étudiant)
    public List<Reclamation> findByEtudiant(int etudiantId) {
        List<Reclamation> reclamations = new ArrayList<>();
        String sql = "SELECT * FROM reclamations WHERE etudiant_id = ? ORDER BY date_creation DESC";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, etudiantId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                reclamations.add(mapToReclamation(rs));
            }

            rs.close(); ps.close(); conn.close();
        } catch (SQLException e) {
            System.out.println("Erreur findByEtudiant reclamations : " + e.getMessage());
        }

        return reclamations;
    }

    // Filtre les réclamations par statut
    public List<Reclamation> findByStatut(StatutDemande statut) {
        List<Reclamation> reclamations = new ArrayList<>();
        String sql = "SELECT * FROM reclamations WHERE statut = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, statut.name());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                reclamations.add(mapToReclamation(rs));
            }

            rs.close(); ps.close(); conn.close();
        } catch (SQLException e) {
            System.out.println("Erreur findByStatut reclamations : " + e.getMessage());
        }

        return reclamations;
    }

    // Met à jour objet et description d'une réclamation
    // Règle : l'étudiant ne peut modifier que si statut=EN_COURS_DE_TRAITEMENT — vérifié dans le controller
    public boolean update(Reclamation reclamation) {
        String sql = "UPDATE reclamations SET objet = ?, description = ? WHERE id = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, reclamation.getObjet());
            ps.setString(2, reclamation.getDescription());
            ps.setInt(3, reclamation.getId());
            int rows = ps.executeUpdate();
            ps.close(); conn.close();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Erreur update reclamation : " + e.getMessage());
        }

        return false;
    }

    // Change le statut d'une réclamation — admin seulement (vérifié dans le controller)
    public boolean changerStatut(int id, StatutDemande nouveauStatut) {
        String sql = "UPDATE reclamations SET statut = ? WHERE id = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nouveauStatut.name());
            ps.setInt(2, id);
            int rows = ps.executeUpdate();
            ps.close(); conn.close();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Erreur changerStatut reclamation : " + e.getMessage());
        }

        return false;
    }

    // Supprime une réclamation — admin seulement (vérifié dans le controller)
    public boolean delete(int id) {
        String sql = "DELETE FROM reclamations WHERE id = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            ps.close(); conn.close();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Erreur delete reclamation : " + e.getMessage());
        }

        return false;
    }

    // Compte les réclamations par statut (pour le tableau de bord)
    public int countByStatut(StatutDemande statut) {
        String sql = "SELECT COUNT(*) FROM reclamations WHERE statut = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, statut.name());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                rs.close(); ps.close(); conn.close();
                return count;
            }

            rs.close(); ps.close(); conn.close();
        } catch (SQLException e) {
            System.out.println("Erreur countByStatut reclamations : " + e.getMessage());
        }

        return 0;
    }

    // Filtre les réclamations par année (pour les statistiques annuelles)
    public List<Reclamation> findByAnnee(int annee) {
        List<Reclamation> reclamations = new ArrayList<>();
        String sql = "SELECT * FROM reclamations WHERE YEAR(date_creation) = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, annee);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                reclamations.add(mapToReclamation(rs));
            }

            rs.close(); ps.close(); conn.close();
        } catch (SQLException e) {
            System.out.println("Erreur findByAnnee reclamations : " + e.getMessage());
        }

        return reclamations;
    }

    // Transforme une ligne ResultSet en objet Reclamation
    private Reclamation mapToReclamation(ResultSet rs) throws SQLException {
        return new Reclamation(
            rs.getInt("id"),
            rs.getString("objet"),
            rs.getString("description"),
            rs.getDate("date_creation"),
            StatutDemande.valueOf(rs.getString("statut")), 
            rs.getInt("etudiant_id")
        );
    }
}
