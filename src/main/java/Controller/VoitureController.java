package Controller;

import Model.Database;
import Model.Utilisateur;
import Model.Voiture;
import security.AuthorizationUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VoitureController {
    public static List<Voiture> getAllVoitures() { return getAllVoitures(AuthorizationUtils.currentUser()); }
    public static boolean addVoiture(Voiture v) { return addVoiture(AuthorizationUtils.currentUser(), v); }
    public static boolean updateVoiture(Voiture v) { return updateVoiture(AuthorizationUtils.currentUser(), v); }
    public static boolean deleteVoiture(int id) { return deleteVoiture(AuthorizationUtils.currentUser(), id); }
    public static boolean updateDisponibilite(int id, boolean dispo) { return updateDisponibilite(AuthorizationUtils.currentUser(), id, dispo); }

    public static List<Voiture> getAllVoitures(Utilisateur utilisateur) {
        AuthorizationUtils.requireAuthenticated(utilisateur);
        List<Voiture> list = new ArrayList<>();
        String sql = "SELECT * FROM Voiture";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Voiture(
                        rs.getInt("ID_voiture"),
                        rs.getString("Marque"),
                        rs.getString("Modele"),
                        rs.getString("Immatricule"),
                        rs.getDouble("Tarif"),
                        rs.getBoolean("Disponibilite")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean addVoiture(Utilisateur utilisateur, Voiture v) {
        AuthorizationUtils.requireAdmin(utilisateur);
        String sql = "INSERT INTO Voiture (Marque, Modele, Immatricule, Tarif, Disponibilite) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, v.getMarque());
            ps.setString(2, v.getModele());
            ps.setString(3, v.getImmatricule());
            ps.setDouble(4, v.getTarif());
            ps.setBoolean(5, v.isDisponible());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateVoiture(Utilisateur utilisateur, Voiture v) {
        AuthorizationUtils.requireAdmin(utilisateur);
        String sql = "UPDATE Voiture SET Marque=?, Modele=?, Immatricule=?, Tarif=?, Disponibilite=? WHERE ID_voiture=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, v.getMarque());
            ps.setString(2, v.getModele());
            ps.setString(3, v.getImmatricule());
            ps.setDouble(4, v.getTarif());
            ps.setBoolean(5, v.isDisponible());
            ps.setInt(6, v.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteVoiture(Utilisateur utilisateur, int id) {
        AuthorizationUtils.requireAdmin(utilisateur);
        String sql = "DELETE FROM Voiture WHERE ID_voiture=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateDisponibilite(Utilisateur utilisateur, int id, boolean dispo) {
        AuthorizationUtils.requireAdmin(utilisateur);
        String sql = "UPDATE Voiture SET Disponibilite=? WHERE ID_voiture=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, dispo);
            ps.setInt(2, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean reserveVehicle(Utilisateur utilisateur, int id) {
        AuthorizationUtils.requireAuthenticated(utilisateur);
        String sql = "UPDATE Voiture SET Disponibilite=? WHERE ID_voiture=? AND Disponibilite=true";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, false);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}