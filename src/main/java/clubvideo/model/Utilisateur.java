package clubvideo.model;

public class Utilisateur {
    private int    idUser;
    private String login;
    private String password;
    private String role;

    public Utilisateur() {}
    public Utilisateur(int id, String login, String role) {
        this.idUser = id; this.login = login; this.role = role;
    }

    public int    getIdUser()          { return idUser; }
    public void   setIdUser(int v)     { this.idUser = v; }
    public String getLogin()           { return login; }
    public void   setLogin(String v)   { this.login = v; }
    public String getPassword()        { return password; }
    public void   setPassword(String v){ this.password = v; }
    public String getRole()            { return role; }
    public void   setRole(String v)    { this.role = v; }
    public boolean isAdmin()           { return "ADMIN".equals(role); }
}
