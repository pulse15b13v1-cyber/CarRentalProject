package Controller;

import Model.Database;
import Model.Facture;
import Model.Utilisateur;
import security.AuthorizationUtils;

import java.sql.*;
import java.util.*;

public class FactureController {
    public static List<Facture> getAllFactures() { return getAllFactures(AuthorizationUtils.currentUser()); }
    public static boolean addFacture(double montant, java.util.Date date, int idClient, int idLocation) {
        return addFacture(AuthorizationUtils.currentUser(), montant, date, idClient, idLocation);
    }
    public static boolean deleteFacture(int id) { return deleteFacture(AuthorizationUtils.currentUser(), id); }

    public static List<Facture> getAllFactures(Utilisateur utilisateur) {
        List<Facture> list = new ArrayList<>();
        AuthorizationUtils.requireAuthenticated(utilisateur);
        String sql = "SELECT F.ID_facture, F.Montant, F.Date_facture, F.ID_client, F.ID_location, " +
                     "C.Nom & ' ' & C.Prenom AS ClientNom, V.Modele " +
                     "FROM Facture F " +
                     "JOIN Client C ON F.ID_client = C.ID_client " +
                     "JOIN Location L ON F.ID_location = L.ID_location " +
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
                list.add(new Facture(
                    rs.getInt("ID_facture"),
                    rs.getDouble("Montant"),
                    rs.getDate("Date_facture"),
                    rs.getInt("ID_client"),
                    rs.getInt("ID_location"),
                    rs.getString("ClientNom"),
                    rs.getString("Modele")));
            }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean addFacture(Utilisateur utilisateur, double montant, java.util.Date date, int idClient, int idLocation) {
        AuthorizationUtils.requireAdmin(utilisateur);
        String sql = "INSERT INTO Facture (Montant, Date_facture, ID_client, ID_location) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, montant);
            ps.setDate(2, new java.sql.Date(date.getTime()));
            ps.setInt(3, idClient);
            ps.setInt(4, idLocation);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteFacture(Utilisateur utilisateur, int id) {
        AuthorizationUtils.requireAdmin(utilisateur);
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM Facture WHERE ID_facture=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
