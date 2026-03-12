package clubvideo.dao;

import clubvideo.database.DatabaseConnection;
import clubvideo.model.Location;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LocationDAO {

    private Connection conn() { return DatabaseConnection.getConnection(); }

    /** Enregistre une location. Retourne false si limite atteinte ou déjà en cours. */
    public boolean louer(int noAbonne, int noCassette) {
        // 1. Vérifier la limite de 3
        try (PreparedStatement ps = conn().prepareStatement(
                "SELECT nombre_location FROM ABONNE WHERE no_abonne = ?")) {
            ps.setInt(1, noAbonne);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) >= 3) return false;
            }
        } catch (SQLException e) { e.printStackTrace(); return false; }

        // 2. Vérifier si cette cassette est déjà en cours pour cet abonné
        try (PreparedStatement ps = conn().prepareStatement(
                "SELECT COUNT(*) FROM LOCATION WHERE no_abonne=? AND no_cassette=? AND date_retour IS NULL")) {
            ps.setInt(1, noAbonne); ps.setInt(2, noCassette);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) return false; // déjà en cours
            }
        } catch (SQLException e) { e.printStackTrace(); return false; }

        // 3. INSERT ou mise à jour si retournée auparavant
        String sql = "INSERT INTO LOCATION (no_abonne, no_cassette, date_location, date_retour) " +
                     "VALUES (?, ?, ?, NULL) " +
                     "ON DUPLICATE KEY UPDATE date_location = VALUES(date_location), date_retour = NULL";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt (1, noAbonne);
            ps.setInt (2, noCassette);
            ps.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
            int rows = ps.executeUpdate();
            if (rows > 0) {
                new AbonneDAO().incrementerLocation(noAbonne);
                return true;
            }
            return false;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /** Enregistre le retour d'une cassette. */
    public boolean retourner(int noAbonne, int noCassette) {
        String sql = "UPDATE LOCATION SET date_retour = ? WHERE no_abonne=? AND no_cassette=? AND date_retour IS NULL";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
            ps.setInt (2, noAbonne);
            ps.setInt (3, noCassette);
            boolean ok = ps.executeUpdate() > 0;
            if (ok) new AbonneDAO().decrementerLocation(noAbonne);
            return ok;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<Location> listerTout() {
        return query("""
            SELECT l.*, a.nom_abonne, c.titre
            FROM LOCATION l
            JOIN ABONNE   a ON l.no_abonne   = a.no_abonne
            JOIN CASSETTE c ON l.no_cassette = c.no_cassette
            ORDER BY l.date_location DESC""", null);
    }

    public List<Location> listerEnCours() {
        return query("""
            SELECT l.*, a.nom_abonne, c.titre
            FROM LOCATION l
            JOIN ABONNE   a ON l.no_abonne   = a.no_abonne
            JOIN CASSETTE c ON l.no_cassette = c.no_cassette
            WHERE l.date_retour IS NULL
            ORDER BY l.date_location DESC""", null);
    }

    public List<Location> listerRetournees() {
        return query("""
            SELECT l.*, a.nom_abonne, c.titre
            FROM LOCATION l
            JOIN ABONNE   a ON l.no_abonne   = a.no_abonne
            JOIN CASSETTE c ON l.no_cassette = c.no_cassette
            WHERE l.date_retour IS NOT NULL
            ORDER BY l.date_retour DESC""", null);
    }

    public List<Location> listerParAbonne(int noAbonne) {
        List<Location> list = new ArrayList<>();
        String sql = """
            SELECT l.*, a.nom_abonne, c.titre
            FROM LOCATION l
            JOIN ABONNE   a ON l.no_abonne   = a.no_abonne
            JOIN CASSETTE c ON l.no_cassette = c.no_cassette
            WHERE l.no_abonne = ?
            ORDER BY l.date_location DESC""";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, noAbonne);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /** Supprime une location (ajuste le compteur si elle était en cours). */
    public boolean supprimer(int noAbonne, int noCassette) {
        try (PreparedStatement ps = conn().prepareStatement(
                "DELETE FROM LOCATION WHERE no_abonne=? AND no_cassette=?")) {
            ps.setInt(1, noAbonne); ps.setInt(2, noCassette);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    private List<Location> query(String sql, Object param) {
        List<Location> list = new ArrayList<>();
        try {
            if (param == null) {
                try (Statement st = conn().createStatement();
                     ResultSet rs = st.executeQuery(sql)) {
                    while (rs.next()) list.add(map(rs));
                }
            } else {
                try (PreparedStatement ps = conn().prepareStatement(sql)) {
                    ps.setObject(1, param);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) list.add(map(rs));
                    }
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private Location map(ResultSet rs) throws SQLException {
        Location l = new Location();
        l.setNoAbonne    (rs.getInt("no_abonne"));
        l.setNoCassette  (rs.getInt("no_cassette"));
        java.sql.Date dl = rs.getDate("date_location");
        if (dl != null) l.setDateLocation(dl.toLocalDate());
        java.sql.Date dr = rs.getDate("date_retour");
        if (dr != null) l.setDateRetour(dr.toLocalDate());
        l.setNomAbonne   (rs.getString("nom_abonne"));
        l.setTitreCassette(rs.getString("titre"));
        return l;
    }
}
