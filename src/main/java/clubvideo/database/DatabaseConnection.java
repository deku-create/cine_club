package clubvideo.database;

import java.sql.*;

/**
 * ╔══════════════════════════════════════════════════════════╗
 *  DatabaseConnection — Connexion MySQL XAMPP (singleton)
 *  Base : club_video | Host : localhost:3306 | User : root
 *  ► Modifiez PASSWORD si votre XAMPP a un mot de passe MySQL
 * ╚══════════════════════════════════════════════════════════╝
 */
public class DatabaseConnection {

    // ═══ PARAMÈTRES XAMPP (à adapter si besoin) ══════════════════════
    private static final String HOST     = "localhost";
    private static final int    PORT     = 3306;
    private static final String DB_NAME  = "club_video";
    private static final String USER     = "";
    private static final String PASSWORD = "";   // ← vide par défaut XAMPP

    private static final String URL =
        "jdbc:mysql://localhost/3306/club_video"
        + "?useSSL=false"
        + "&serverTimezone=UTC"
        + "&allowPublicKeyRetrieval=true"
        + "&useUnicode=true"
        + "&characterEncoding=UTF-8"
        + "&autoReconnect=true";

    private static Connection connection = null;

    // ── Connexion singleton avec reconnexion automatique ─────────────
    public static synchronized Connection getConnection() {
        try {
            if (connection == null || connection.isClosed() || !connection.isValid(2)) {
                connect();
            }
        } catch (SQLException e) {
            connect();
        }
        return connection;
    }

    private static void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            connection.setAutoCommit(true);
            System.out.println("[DB] ✅ Connecté à MySQL XAMPP — Base : " + DB_NAME);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                "Driver MySQL introuvable.\n" +
                "Vérifiez que mysql-connector-java est dans les dépendances Maven.", e);
        } catch (SQLException e) {
            throw new RuntimeException(
                "❌ Connexion MySQL impossible.\n\n" +
                "Vérifications à faire :\n" +
                "  1. XAMPP est lancé (Apache + MySQL en vert)\n" +
                "  2. La base 'club_video' existe dans phpMyAdmin\n" +
                "  3. Exécutez le fichier : club_video.sql dans phpMyAdmin\n" +
                "  4. Port MySQL = 3306 (vérifiez dans XAMPP)\n\n" +
                "Erreur technique : " + e.getMessage(), e);
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Connexion fermée.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
