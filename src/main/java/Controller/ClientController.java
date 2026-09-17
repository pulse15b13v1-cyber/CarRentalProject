package Controller;

import Model.Client;
import Model.Database;
import Model.Utilisateur;
import security.AuthorizationUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientController {
    public static List<Client> getAllClients() { return getAllClients(AuthorizationUtils.currentUser()); }
    public static boolean addClient(Client c) { return addClient(AuthorizationUtils.currentUser(), c); }
    public static boolean updateClient(Client c) { return updateClient(AuthorizationUtils.currentUser(), c); }
    public static boolean deleteClient(int id) { return deleteClient(AuthorizationUtils.currentUser(), id); }

    public static Client getClientByUtilisateurId(int idUtilisateur) {
        String sql = "SELECT ID_client, Nom, Prenom, Telephone, Mail, ID_utilisateur " +
                     "FROM Client WHERE ID_utilisateur = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUtilisateur);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Client(
                            rs.getInt("ID_client"),
                            rs.getString("Nom"),
                            rs.getString("Prenom"),
                            rs.getInt("Telephone"),
                            rs.getString("Mail"),
                            rs.getInt("ID_utilisateur")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("SQLException dans getClientByUtilisateurId: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static List<Client> getAllClients(Utilisateur utilisateur) {
        AuthorizationUtils.requireAdmin(utilisateur);
        List<Client> list = new ArrayList<>();
        // Assure-toi que les noms de colonnes sont exacts (ID_client, Nom, Prenom, Telephone, Email, ID_utilisateur)
        String sql = "SELECT ID_client, Nom, Prenom, Telephone, Mail, ID_utilisateur FROM Client";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (conn == null) {
                System.err.println("Erreur: La connexion à la base de données est null dans getAllClients.");
                return list; // Retourne une liste vide
            }

            while (rs.next()) {
                list.add(new Client(
                        rs.getInt("ID_client"),
                        rs.getString("Nom"),
                        rs.getString("Prenom"),
                        rs.getInt("Telephone"), // Correspond à la BDD Numérique
                        rs.getString("Mail"),
                        rs.getInt("ID_utilisateur") // Récupérer ID_utilisateur
                ));
            }
        } catch (SQLException e) {
            System.err.println("SQLException dans getAllClients: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public static boolean addClient(Utilisateur utilisateur, Client c) {
        AuthorizationUtils.requireAdmin(utilisateur);
        // Si ID_utilisateur est géré et requis, ajoute-le à la requête et au setInt
        // Par exemple: "INSERT INTO Client (Nom, Prenom, Telephone, Email, ID_utilisateur) VALUES (?, ?, ?, ?, ?)";
        String sql = "INSERT INTO Client (Nom, Prenom, Telephone, Mail, ID_utilisateur) VALUES (?, ?, ?, ?, ?)"; // Ajout ID_utilisateur
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (conn == null) {
                System.err.println("Erreur: La connexion à la base de données est null dans addClient.");
                return false;
            }

            ps.setString(1, c.getNom());
            ps.setString(2, c.getPrenom());
            ps.setInt(3, c.getTelephone()); // Correspond à la BDD Numérique
            ps.setString(4, c.getMail());
            ps.setInt(5, c.getIdUtilisateur()); // Définir ID_utilisateur
                                                // Si ID_utilisateur peut être null dans la BDD et que c.getIdUtilisateur() pourrait retourner 0/valeur sentinelle:
                                                // if (c.getIdUtilisateur() != 0) ps.setInt(5, c.getIdUtilisateur()); else ps.setNull(5, Types.INTEGER);


            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("SQLException dans addClient: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("ErrorCode: " + e.getErrorCode());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateClient(Utilisateur utilisateur, Client c) {
        AuthorizationUtils.requireAdmin(utilisateur);
        // Si ID_utilisateur est géré, ajoute-le à la requête et au setInt
        // Par exemple: "UPDATE Client SET Nom=?, Prenom=?, Telephone=?, Email=?, ID_utilisateur=? WHERE ID_client=?";
        String sql = "UPDATE Client SET Nom=?, Prenom=?, Telephone=?, Mail=?, ID_utilisateur=? WHERE ID_client=?"; // Ajout ID_utilisateur
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (conn == null) {
                System.err.println("Erreur: La connexion à la base de données est null dans updateClient.");
                return false;
            }

            ps.setString(1, c.getNom());
            ps.setString(2, c.getPrenom());
            ps.setInt(3, c.getTelephone()); // Correspond à la BDD Numérique
            ps.setString(4, c.getMail());
            ps.setInt(5, c.getIdUtilisateur()); // Définir ID_utilisateur
            ps.setInt(6, c.getId());

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("SQLException dans updateClient: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("ErrorCode: " + e.getErrorCode());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteClient(Utilisateur utilisateur, int id) {
        AuthorizationUtils.requireAdmin(utilisateur);
        String sql = "DELETE FROM Client WHERE ID_client=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (conn == null) {
                System.err.println("Erreur: La connexion à la base de données est null dans deleteClient.");
                return false;
            }
            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("SQLException dans deleteClient pour ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}