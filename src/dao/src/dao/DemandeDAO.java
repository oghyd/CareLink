package src.dao;

import src.model.Demande;
import src.model.StatutDemande;
import src.model.TypeDemande;
import src.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DemandeDAO {

    // Crée une nouvelle demande, statut initial = CREE
    public boolean create(Demande demande, int etudiantId) {
        String sql = "INSERT INTO demandes (titre, description, type, date_creation, statut, etudiant_id) "
                   + "VALUES (?, ?, ?, NOW(), 'CREE', ?)";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, demande.getTitre());
            ps.setString(2, demande.getDescription());
            ps.setString(3, demande.getType().name());
            ps.setInt(4, etudiantId);
            int rows = ps.executeUpdate();
            ps.close(); conn.close();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Erreur create demande : " + e.getMessage());
        }

        return false;
    }

    // Récupère une demande par son id
    public Demande findById(int id) {
        String sql = "SELECT * FROM demandes WHERE id = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Demande d = mapToDemande(rs);
                rs.close(); ps.close(); conn.close();
                return d;
            }

            rs.close(); ps.close(); conn.close();
        } catch (SQLException e) {
            System.out.println("Erreur findById demande : " + e.getMessage());
        }

        return null;
    }

    // Récupère toutes les demandes (vue admin)
    public List<Demande> findAll() {
        List<Demande> demandes = new ArrayList<>();
        String sql = "SELECT * FROM demandes ORDER BY date_creation DESC";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                demandes.add(mapToDemande(rs));
            }

            rs.close(); ps.close(); conn.close();
        } catch (SQLException e) {
            System.out.println("Erreur findAll demandes : " + e.getMessage());
        }

        return demandes;
    }

    // Récupère les demandes d'un étudiant (vue étudiant)
    public List<Demande> findByEtudiant(int etudiantId) {
        List<Demande> demandes = new ArrayList<>();
        String sql = "SELECT * FROM demandes WHERE etudiant_id = ? ORDER BY date_creation DESC";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, etudiantId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                demandes.add(mapToDemande(rs));
            }

            rs.close(); ps.close(); conn.close();
        } catch (SQLException e) {
            System.out.println("Erreur findByEtudiant demandes : " + e.getMessage());
        }

        return demandes;
    }

    // Filtre les demandes par statut
    public List<Demande> findByStatut(StatutDemande statut) {
        List<Demande> demandes = new ArrayList<>();
        String sql = "SELECT * FROM demandes WHERE statut = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, statut.name());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                demandes.add(mapToDemande(rs));
            }

            rs.close(); ps.close(); conn.close();
        } catch (SQLException e) {
            System.out.println("Erreur findByStatut demandes : " + e.getMessage());
        }

        return demandes;
    }

    // Filtre les demandes par type
    public List<Demande> findByType(TypeDemande type) {
        List<Demande> demandes = new ArrayList<>();
        String sql = "SELECT * FROM demandes WHERE type = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, type.name());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                demandes.add(mapToDemande(rs));
            }

            rs.close(); ps.close(); conn.close();
        } catch (SQLException e) {
            System.out.println("Erreur findByType demandes : " + e.getMessage());
        }

        return demandes;
    }

    // Met à jour titre et description d'une demande
    // Règle : l'étudiant ne peut modifier que si statut=EN_COURS_DE_TRAITEMENT — vérifié dans le controller
    public boolean update(Demande demande) {
        String sql = "UPDATE demandes SET titre = ?, description = ?, type = ? WHERE id = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, demande.getTitre());
            ps.setString(2, demande.getDescription());
            ps.setString(3, demande.getType().name());
            ps.setInt(4, demande.getId());
            int rows = ps.executeUpdate();
            ps.close(); conn.close();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Erreur update demande : " + e.getMessage());
        }

        return false;
    }

    // Change le statut d'une demande — admin seulement (vérifié dans le controller)
    public boolean changerStatut(int id, StatutDemande nouveauStatut) {
        String sql = "UPDATE demandes SET statut = ? WHERE id = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nouveauStatut.name());
            ps.setInt(2, id);
            int rows = ps.executeUpdate();
            ps.close(); conn.close();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Erreur changerStatut demande : " + e.getMessage());
        }

        return false;
    }

    // Supprime une demande — admin seulement (vérifié dans le controller)
    public boolean delete(int id) {
        String sql = "DELETE FROM demandes WHERE id = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            ps.close(); conn.close();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Erreur delete demande : " + e.getMessage());
        }

        return false;
    }

    // Compte les demandes par statut (pour le tableau de bord)
    public int countByStatut(StatutDemande statut) {
        String sql = "SELECT COUNT(*) FROM demandes WHERE statut = ?";

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
            System.out.println("Erreur countByStatut demandes : " + e.getMessage());
        }

        return 0;
    }

    // Filtre les demandes par année (pour les statistiques annuelles)
    public List<Demande> findByAnnee(int annee) {
        List<Demande> demandes = new ArrayList<>();
        String sql = "SELECT * FROM demandes WHERE YEAR(date_creation) = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, annee);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                demandes.add(mapToDemande(rs));
            }

            rs.close(); ps.close(); conn.close();
        } catch (SQLException e) {
            System.out.println("Erreur findByAnnee demandes : " + e.getMessage());
        }

        return demandes;
    }

    // Transforme une ligne ResultSet en objet Demande
    private Demande mapToDemande(ResultSet rs) throws SQLException {
        return new Demande(
            rs.getInt("id"),
            rs.getString("titre"),
            rs.getString("description"),
            TypeDemande.valueOf(rs.getString("type")),
            rs.getDate("date_creation"),
            StatutDemande.valueOf(rs.getString("statut"))
        );
    }
}
