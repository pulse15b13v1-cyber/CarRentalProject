package Controller;

import Model.Database;
import Model.Location;
import Model.Client;
import Model.Utilisateur;
import security.AuthorizationUtils;

import java.sql.*;
import java.util.*;

public class LocationController {
    public static List<Location> getAllLocations() { return getAllLocations(AuthorizationUtils.currentUser()); }
    public static boolean addLocation(Location loc) { return addLocation(AuthorizationUtils.currentUser(), loc); }
    public static boolean updateLocation(Location loc) { return updateLocation(AuthorizationUtils.currentUser(), loc); }
    public static boolean deleteLocation(int id) { return deleteLocation(AuthorizationUtils.currentUser(), id); }

    public static List<Location> getAllLocations(Utilisateur utilisateur) {
        List<Location> list = new ArrayList<>();
        AuthorizationUtils.requireAuthenticated(utilisateur);
        String sql = "SELECT L.*, C.Nom & ' ' & C.Prenom AS ClientNom, V.Modele AS Modele FROM Location L " +
                     "JOIN Client C ON L.ID_client = C.ID_client " +
                     "JOIN Voiture V ON L.ID_voiture = V.ID_voiture";
        if (!AuthorizationUtils.isAdmin(utilisateur)) {
            sql += " WHERE C.ID_utilisateur = ?";
        }
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (!AuthorizationUtils.isAdmin(utilisateur)) {
                ps.setInt(1, utilisateur.getId());
            }
            try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Location(
                    rs.getInt("ID_location"),
                    rs.getDate("Date_debut"),
                    rs.getDate("Date_fin"),
                    rs.getDouble("Prix"),
                    rs.getString("Statut"),
                    rs.getString("Conditions"),
                    rs.getInt("ID_voiture"),
                    rs.getInt("ID_client"),
                    rs.getString("ClientNom"),
                    rs.getString("Modele")
                ));
            }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean addLocation(Utilisateur utilisateur, Location loc) {
        AuthorizationUtils.requireAuthenticated(utilisateur);
        if (!AuthorizationUtils.isAdmin(utilisateur)) {
            Client client = ClientController.getClientByUtilisateurId(utilisateur.getId());
            if (client == null || client.getId() != loc.getIdClient()) {
                throw new SecurityException("La location ne correspond pas au client connecté.");
            }
        }
        String sql = "INSERT INTO Location (Date_debut, Date_fin, Prix, Statut, Conditions, ID_voiture, ID_client) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, new java.sql.Date(loc.getDateDebut().getTime()));
            ps.setDate(2, new java.sql.Date(loc.getDateFin().getTime()));
            ps.setDouble(3, loc.getPrix());
            ps.setString(4, loc.getStatut());
            ps.setString(5, loc.getConditions());
            ps.setInt(6, loc.getIdVoiture());
            ps.setInt(7, loc.getIdClient());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean updateLocation(Utilisateur utilisateur, Location loc) {
        AuthorizationUtils.requireAuthenticated(utilisateur);
        if (!AuthorizationUtils.isAdmin(utilisateur)) {
            Client client = ClientController.getClientByUtilisateurId(utilisateur.getId());
            if (client == null || client.getId() != loc.getIdClient()) {
                throw new SecurityException("La location ne correspond pas au client connecté.");
            }
        }
        String ownership = "";
        if (!AuthorizationUtils.isAdmin(utilisateur)) {
            ownership = " AND ID_client IN (SELECT ID_client FROM Client WHERE ID_utilisateur=?)";
        }
        String sql = "UPDATE Location SET Date_debut=?, Date_fin=?, Prix=?, Statut=?, Conditions=?, ID_voiture=?, ID_client=? WHERE ID_location=?";
        sql += ownership;
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, new java.sql.Date(loc.getDateDebut().getTime()));
            ps.setDate(2, new java.sql.Date(loc.getDateFin().getTime()));
            ps.setDouble(3, loc.getPrix());
            ps.setString(4, loc.getStatut());
            ps.setString(5, loc.getConditions());
            ps.setInt(6, loc.getIdVoiture());
            ps.setInt(7, loc.getIdClient());
            ps.setInt(8, loc.getId()); // Pour la clause WHERE
            if (!AuthorizationUtils.isAdmin(utilisateur)) {
                ps.setInt(9, utilisateur.getId());
            }

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("SQLException dans updateLocation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public static boolean deleteLocation(Utilisateur utilisateur, int id) {
        AuthorizationUtils.requireAuthenticated(utilisateur);
        String sql = "DELETE FROM Location WHERE ID_location=?";
        if (!AuthorizationUtils.isAdmin(utilisateur)) {
            sql += " AND ID_client IN (SELECT ID_client FROM Client WHERE ID_utilisateur=?)";
        }
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (!AuthorizationUtils.isAdmin(utilisateur)) {
                ps.setInt(2, utilisateur.getId());
            }
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
