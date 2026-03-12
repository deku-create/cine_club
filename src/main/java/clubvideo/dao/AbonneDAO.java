package clubvideo.dao;

import clubvideo.database.DatabaseConnection;
import clubvideo.model.Abonne;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AbonneDAO {

    private Connection conn() { return DatabaseConnection.getConnection(); }

    public boolean ajouter(Abonne a) {
        String sql = "INSERT INTO ABONNE(nom_abonne, adresse_abonne, date_abonnement, date_entree, nombre_location) VALUES(?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, a.getNomAbonne());
            ps.setString(2, a.getAdresseAbonne());
            ps.setDate  (3, java.sql.Date.valueOf(a.getDateAbonnement()));
            ps.setDate  (4, java.sql.Date.valueOf(a.getDateEntree()));
            ps.setInt   (5, a.getNombreLocation());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<Abonne> listerTout() {
        List<Abonne> list = new ArrayList<>();
        String sql = "SELECT * FROM ABONNE ORDER BY no_abonne";
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Abonne trouverParId(int id) {
        try (PreparedStatement ps = conn().prepareStatement("SELECT * FROM ABONNE WHERE no_abonne=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Abonne> rechercher(String keyword) {
        List<Abonne> list = new ArrayList<>();
        String sql = "SELECT * FROM ABONNE WHERE LOWER(nom_abonne) LIKE ? OR LOWER(adresse_abonne) LIKE ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            String kw = "%" + keyword.toLowerCase() + "%";
            ps.setString(1, kw); ps.setString(2, kw);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean modifier(Abonne a) {
        String sql = "UPDATE ABONNE SET nom_abonne = ?, adresse_abonne = ?, date_abonnement = ?, date_entree = ?, nombre_location = ? WHERE no_abonne = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, a.getNomAbonne());
            ps.setString(2, a.getAdresseAbonne());
            ps.setDate  (3, java.sql.Date.valueOf(a.getDateAbonnement()));
            ps.setDate  (4, java.sql.Date.valueOf(a.getDateEntree()));
            ps.setInt   (5, a.getNombreLocation());
            ps.setInt   (6, a.getNoAbonne());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean supprimer(int id) {
        try (PreparedStatement ps = conn().prepareStatement("DELETE FROM ABONNE WHERE no_abonne=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean incrementerLocation(int idAbonne) {
        String sql = "UPDATE ABONNE SET nombre_location = nombre_location + 1 WHERE no_abonne=? AND nombre_location < 3";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, idAbonne);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean decrementerLocation(int idAbonne) {
        String sql = "UPDATE ABONNE SET nombre_location = nombre_location - 1 WHERE no_abonne = ? AND nombre_location > 0";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, idAbonne);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private Abonne map(ResultSet rs) throws SQLException {
        Abonne a = new Abonne();
        a.setNoAbonne       (rs.getInt("no_abonne"));
        a.setNomAbonne      (rs.getString("nom_abonne"));
        a.setAdresseAbonne  (rs.getString("adresse_abonne"));
        java.sql.Date dAbo = rs.getDate("date_abonnement");
        a.setDateAbonnement (dAbo != null ? dAbo.toLocalDate() : LocalDate.now());
        java.sql.Date dEnt = rs.getDate("date_entree");
        a.setDateEntree     (dEnt != null ? dEnt.toLocalDate() : LocalDate.now());
        a.setNombreLocation (rs.getInt("nombre_location"));
        return a;
    }
}
