package Controller;

import Model.Database;
import Model.Utilisateur;
import security.PasswordUtils;

import java.sql.*;

public class AuthController {

    // ----------------------- LOGIN ---------------------------
    public static Utilisateur login(String login, String password) {
        String sql = "SELECT ID_utilisateur, login, mot_de_passe, type_utilisateur " +
                     "FROM Utilisateur WHERE login = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && PasswordUtils.verifyPassword(password, rs.getString("mot_de_passe"))) {
                    return new Utilisateur(
                            rs.getInt("ID_utilisateur"),
                            rs.getString("login"),
                            rs.getString("mot_de_passe"),
                            rs.getString("type_utilisateur")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Échec connexion
    }

    // --------------------- REGISTER --------------------------
    public static boolean register(String login, String password) {
        String checkSql = "SELECT 1 FROM Utilisateur WHERE login = ?";
        String insertSql = "INSERT INTO Utilisateur (login, mot_de_passe, type_utilisateur) VALUES (?, ?, 'client')";
        try (Connection conn = Database.getConnection();
             PreparedStatement psCheck = conn.prepareStatement(checkSql)) {

            psCheck.setString(1, login);
            if (psCheck.executeQuery().next()) {
                return false; // Login déjà utilisé
            }

            try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                String passwordHash = PasswordUtils.hashPassword(password);
                psInsert.setString(1, login);
                psInsert.setString(2, passwordHash);
                psInsert.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
