package clubvideo.dao;

import clubvideo.database.DatabaseConnection;
import clubvideo.model.Categorie;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategorieDAO {

    private Connection conn() { return DatabaseConnection.getConnection(); }

    public boolean ajouter(String libelle) {
        try (PreparedStatement ps = conn().prepareStatement("INSERT INTO CATEGORIE(libelle) VALUES(?)")) {
            ps.setString(1, libelle);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<Categorie> listerTout() {
        List<Categorie> list = new ArrayList<>();
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM CATEGORIE ORDER BY libelle")) {
            while (rs.next())
                list.add(new Categorie(rs.getInt("id_categorie"), rs.getString("libelle")));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean modifier(int id, String libelle) {
        try (PreparedStatement ps = conn().prepareStatement("UPDATE CATEGORIE SET libelle=? WHERE id_categorie=?")) {
            ps.setString(1, libelle); ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean supprimer(int id) {
        try (PreparedStatement ps = conn().prepareStatement("DELETE FROM CATEGORIE WHERE id_categorie=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
