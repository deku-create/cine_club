package clubvideo.dao;

import clubvideo.database.DatabaseConnection;
import clubvideo.model.Cassette;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CassetteDAO {

    private Connection conn() { return DatabaseConnection.getConnection(); }

    // ── CREATE ───────────────────────────────────────────────────────────────
    public boolean ajouter(Cassette c) {
        String sql = "INSERT INTO CASSETTE(date_achat, titre, auteur, duree, prix, id_categorie) VALUES(?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setDate  (1, java.sql.Date.valueOf(c.getDateAchat()));
            ps.setString(2, c.getTitre());
            ps.setString(3, c.getAuteur());
            ps.setInt   (4, c.getDuree());
            ps.setDouble(5, c.getPrix());
            ps.setInt   (6, c.getIdCategorie());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ── READ ALL ─────────────────────────────────────────────────────────────
    public List<Cassette> listerTout() {
        List<Cassette> list = new ArrayList<>();
        String sql = """
            SELECT c.*, cat.libelle AS libelle_categorie
            FROM CASSETTE c
            LEFT JOIN CATEGORIE cat ON c.id_categorie = cat.id_categorie
            ORDER BY c.no_cassette""";
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // ── READ ONE ─────────────────────────────────────────────────────────────
    public Cassette trouverParId(int id) {
        String sql = """
            SELECT c.*, cat.libelle AS libelle_categorie
            FROM CASSETTE c
            LEFT JOIN CATEGORIE cat ON c.id_categorie = cat.id_categorie
            WHERE c.no_cassette = ?""";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // ── SEARCH ───────────────────────────────────────────────────────────────
    public List<Cassette> rechercher(String keyword) {
        List<Cassette> list = new ArrayList<>();
        String sql = """
            SELECT c.*, cat.libelle AS libelle_categorie
            FROM CASSETTE c
            LEFT JOIN CATEGORIE cat ON c.id_categorie = cat.id_categorie
            WHERE LOWER(c.titre) LIKE ? OR LOWER(c.auteur) LIKE ?
            ORDER BY c.titre""";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            String kw = "%" + keyword.toLowerCase() + "%";
            ps.setString(1, kw); ps.setString(2, kw);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // ── UPDATE ───────────────────────────────────────────────────────────────
    public boolean modifier(Cassette c) {
        String sql = "UPDATE CASSETTE SET date_achat = ?, titre = ?, auteur = ?, duree = ?, prix = ?, id_categorie = ? WHERE no_cassette = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setDate  (1, java.sql.Date.valueOf(c.getDateAchat()));
            ps.setString(2, c.getTitre());
            ps.setString(3, c.getAuteur());
            ps.setInt   (4, c.getDuree());
            ps.setDouble(5, c.getPrix());
            ps.setInt   (6, c.getIdCategorie());
            ps.setInt   (7, c.getNoCassette());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ── DELETE ───────────────────────────────────────────────────────────────
    public boolean supprimer(int id) {
        try (PreparedStatement ps = conn().prepareStatement("DELETE FROM CASSETTE WHERE no_cassette=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ── MAPPER ───────────────────────────────────────────────────────────────
    private Cassette map(ResultSet rs) throws SQLException {
        Cassette c = new Cassette();
        c.setNoCassette  (rs.getInt("no_cassette"));
        java.sql.Date da = rs.getDate("date_achat");
        c.setDateAchat   (da != null ? da.toLocalDate() : LocalDate.now());
        c.setTitre       (rs.getString("titre"));
        c.setAuteur      (rs.getString("auteur"));
        c.setDuree       (rs.getInt("duree"));
        c.setPrix        (rs.getDouble("prix"));
        c.setIdCategorie (rs.getInt("id_categorie"));
        c.setLibelleCategorie(rs.getString("libelle_categorie"));
        return c;
    }
}
