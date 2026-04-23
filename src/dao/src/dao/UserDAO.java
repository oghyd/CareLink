package src.dao;

import src.model.Admin;
import src.model.Etudiant;
import src.model.User;
import src.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // Récupère tous les utilisateurs (étudiants + admins)
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                users.add(mapToUser(rs));
            }

            rs.close(); ps.close(); conn.close();
        } catch (SQLException e) {
            System.out.println("Erreur findAll users : " + e.getMessage());
        }

        return users;
    }

    // Récupère un utilisateur par son id
    public User findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User user = mapToUser(rs);
                rs.close(); ps.close(); conn.close();
                return user;
            }

            rs.close(); ps.close(); conn.close();
        } catch (SQLException e) {
            System.out.println("Erreur findById user : " + e.getMessage());
        }

        return null;
    }

    // Récupère un utilisateur par son email
    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User user = mapToUser(rs);
                rs.close(); ps.close(); conn.close();
                return user;
            }

            rs.close(); ps.close(); conn.close();
        } catch (SQLException e) {
            System.out.println("Erreur findByEmail : " + e.getMessage());
        }

        return null;
    }

    // Vérifie email + mot de passe, retourne l'utilisateur si valide et actif
    public User authenticate(String email, String motDePasse) {
        String sql = "SELECT * FROM users WHERE email = ? AND mot_de_passe = ? AND actif = TRUE";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, motDePasse);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User user = mapToUser(rs);
                rs.close(); ps.close(); conn.close();
                return user;
            }

            rs.close(); ps.close(); conn.close();
        } catch (SQLException e) {
            System.out.println("Erreur authenticate : " + e.getMessage());
        }

        return null;
    }

    // Active ou désactive un compte (admin seulement - vérification dans le controller)
    public boolean setActif(int id, boolean actif) {
        String sql = "UPDATE users SET actif = ? WHERE id = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setBoolean(1, actif);
            ps.setInt(2, id);
            int rows = ps.executeUpdate();
            ps.close(); conn.close();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Erreur setActif : " + e.getMessage());
        }

        return false;
    }

    // Supprime un utilisateur par son id
    public boolean delete(int id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            ps.close(); conn.close();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Erreur delete user : " + e.getMessage());
        }

        return false;
    }

    // Transforme une ligne ResultSet en objet User (Etudiant ou Admin selon le rôle)
    private User mapToUser(ResultSet rs) throws SQLException {
        String role = rs.getString("role");

        if (role.equals("etudiant")) {
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
        } else {
            return new Admin(
                rs.getInt("id"),
                rs.getString("nom"),
                rs.getString("prenom"),
                rs.getString("email"),
                rs.getString("mot_de_passe"),
                rs.getBoolean("actif"),
                rs.getString("fonction")
            );
        }
    }
}
