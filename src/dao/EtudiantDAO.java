package src.dao;

import src.model.Etudiant;
import src.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EtudiantDAO {

    // Crée un compte étudiant - actif=FALSE par défaut, l'admin doit l'activer
    public boolean create(Etudiant etudiant) {
        String sql = "INSERT INTO users (nom, prenom, email, mot_de_passe, actif, role, matricule, type_handicap, telephone) "
                   + "VALUES (?, ?, ?, ?, FALSE, 'etudiant', ?, ?, ?)";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, etudiant.getNom());
            ps.setString(2, etudiant.getPrenom());
            ps.setString(3, etudiant.getEmail());
            ps.setString(4, etudiant.getMotDePasse());
            ps.setString(5, etudiant.getMatricule());
            ps.setString(6, etudiant.getTypeHandicap());
            ps.setString(7, etudiant.getTelephone());
            int rows = ps.executeUpdate();
            ps.close(); conn.close();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Erreur create etudiant : " + e.getMessage());
        }

        return false;
    }

    // Récupère un étudiant par son id
    public Etudiant findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ? AND role = 'etudiant'";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Etudiant e = mapToEtudiant(rs);
                rs.close(); ps.close(); conn.close();
                return e;
            }

            rs.close(); ps.close(); conn.close();
        } catch (SQLException e) {
            System.out.println("Erreur findById etudiant : " + e.getMessage());
        }

        return null;
    }

    // Récupère tous les étudiants
    public List<Etudiant> findAll() {
        List<Etudiant> etudiants = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'etudiant'";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                etudiants.add(mapToEtudiant(rs));
            }

            rs.close(); ps.close(); conn.close();
        } catch (SQLException e) {
            System.out.println("Erreur findAll etudiants : " + e.getMessage());
        }

        return etudiants;
    }

    // Met à jour les informations d'un étudiant
    public boolean update(Etudiant etudiant) {
        String sql = "UPDATE users SET nom = ?, prenom = ?, email = ?, matricule = ?, type_handicap = ?, telephone = ? "
                   + "WHERE id = ? AND role = 'etudiant'";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, etudiant.getNom());
            ps.setString(2, etudiant.getPrenom());
            ps.setString(3, etudiant.getEmail());
            ps.setString(4, etudiant.getMatricule());
            ps.setString(5, etudiant.getTypeHandicap());
            ps.setString(6, etudiant.getTelephone());
            ps.setInt(7, etudiant.getId());
            int rows = ps.executeUpdate();
            ps.close(); conn.close();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Erreur update etudiant : " + e.getMessage());
        }

        return false;
    }

    // Recherche un étudiant par son matricule
    public Etudiant findByMatricule(String matricule) {
        String sql = "SELECT * FROM users WHERE matricule = ? AND role = 'etudiant'";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, matricule);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Etudiant e = mapToEtudiant(rs);
                rs.close(); ps.close(); conn.close();
                return e;
            }

            rs.close(); ps.close(); conn.close();
        } catch (SQLException e) {
            System.out.println("Erreur findByMatricule : " + e.getMessage());
        }

        return null;
    }

    // Transforme une ligne ResultSet en objet Etudiant
    private Etudiant mapToEtudiant(ResultSet rs) throws SQLException {
        return new Etudiant(
            rs.getInt("id"),
            rs.getString("nom"),
            rs.getString("prenom"),
            rs.getString("email"),
            rs.getString("mot_de_passe"),
            rs.getBoolean("actif"),
            rs.getString("matricule"),
            rs.getString("type_handicap"),
            rs.getString("telephone")
        );
    }
}
