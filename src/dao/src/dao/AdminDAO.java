package src.dao;

import src.model.Admin;
import src.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminDAO {

    // Crée un compte admin - actif=TRUE directement, pas besoin d'activation
    public boolean create(Admin admin) {
        String sql = "INSERT INTO users (nom, prenom, email, mot_de_passe, actif, role, fonction) "
                   + "VALUES (?, ?, ?, ?, TRUE, 'admin', ?)";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, admin.getNom());
            ps.setString(2, admin.getPrenom());
            ps.setString(3, admin.getEmail());
            ps.setString(4, admin.getMotDePasse());
            ps.setString(5, admin.getFonction());
            int rows = ps.executeUpdate();
            ps.close(); conn.close();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Erreur create admin : " + e.getMessage());
        }

        return false;
    }

    // Récupère un admin par son id
    public Admin findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ? AND role = 'admin'";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Admin a = mapToAdmin(rs);
                rs.close(); ps.close(); conn.close();
                return a;
            }

            rs.close(); ps.close(); conn.close();
        } catch (SQLException e) {
            System.out.println("Erreur findById admin : " + e.getMessage());
        }

        return null;
    }

    // Récupère tous les admins
    public List<Admin> findAll() {
        List<Admin> admins = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'admin'";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                admins.add(mapToAdmin(rs));
            }

            rs.close(); ps.close(); conn.close();
        } catch (SQLException e) {
            System.out.println("Erreur findAll admins : " + e.getMessage());
        }

        return admins;
    }

    // Met à jour les informations d'un admin
    public boolean update(Admin admin) {
        String sql = "UPDATE users SET nom = ?, prenom = ?, email = ?, fonction = ? WHERE id = ? AND role = 'admin'";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, admin.getNom());
            ps.setString(2, admin.getPrenom());
            ps.setString(3, admin.getEmail());
            ps.setString(4, admin.getFonction());
            ps.setInt(5, admin.getId());
            int rows = ps.executeUpdate();
            ps.close(); conn.close();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Erreur update admin : " + e.getMessage());
        }

        return false;
    }

    // Transforme une ligne ResultSet en objet Admin
    private Admin mapToAdmin(ResultSet rs) throws SQLException {
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
