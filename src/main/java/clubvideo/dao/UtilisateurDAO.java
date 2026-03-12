package clubvideo.dao;

import clubvideo.database.DatabaseConnection;
import clubvideo.model.Utilisateur;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDAO {

    private Connection conn() { return DatabaseConnection.getConnection(); }

    public Utilisateur authentifier(String login, String password) {
        try (PreparedStatement ps = conn().prepareStatement(
                "SELECT * FROM UTILISATEUR WHERE login=? AND password=?")) {
            ps.setString(1, login);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Utilisateur(
                        rs.getInt("id_user"),
                        rs.getString("login"),
                        rs.getString("role")
                    );
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Utilisateur> listerTout() {
        List<Utilisateur> list = new ArrayList<>();
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM UTILISATEUR ORDER BY login")) {
            while (rs.next()) {
                Utilisateur u = new Utilisateur(rs.getInt("id_user"), rs.getString("login"), rs.getString("role"));
                list.add(u);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean ajouter(String login, String password, String role) {
        try (PreparedStatement ps = conn().prepareStatement(
                "INSERT INTO UTILISATEUR(login,password,role) VALUES(?,?,?)")) {
            ps.setString(1, login); ps.setString(2, password); ps.setString(3, role);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean changerMotDePasse(int idUser, String newPassword) {
        try (PreparedStatement ps = conn().prepareStatement(
                "UPDATE UTILISATEUR SET password=? WHERE id_user=?")) {
            ps.setString(1, newPassword); ps.setInt(2, idUser);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean supprimer(int idUser) {
        try (PreparedStatement ps = conn().prepareStatement("DELETE FROM UTILISATEUR WHERE id_user=?")) {
            ps.setInt(1, idUser);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
