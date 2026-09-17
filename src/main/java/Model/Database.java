package Model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;

public class Database {
    private static final Path DB_PATH = Paths.get("database", "car_rental.accdb").toAbsolutePath().normalize();
    private static final String URL = "jdbc:ucanaccess://" + DB_PATH.toString().replace('\\', '/');
    private static Connection connection;

    static {
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            System.out.println("Driver UCanAccess chargé avec succès.");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                System.out.println("Tentative de connexion à : " + DB_PATH);
                connection = DriverManager.getConnection(URL);
                System.out.println("Connexion à la base de données établie avec succès.");
            }
        } catch (SQLException e) {
            connection = null;
            throw new IllegalStateException("Échec de la connexion à la base de données : " + DB_PATH, e);
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connexion à la base de données fermée.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}