package clubvideo.util;

import clubvideo.model.Utilisateur;

/** Garde en mémoire l'utilisateur connecté. */
public class Session {
    private static Utilisateur utilisateurCourant;

    public static void connecter(Utilisateur u)    { utilisateurCourant = u; }
    public static void deconnecter()               { utilisateurCourant = null; }
    public static Utilisateur getUtilisateur()     { return utilisateurCourant; }
    public static boolean isAdmin()                { return utilisateurCourant != null && utilisateurCourant.isAdmin(); }
    public static boolean isConnecte()             { return utilisateurCourant != null; }
}
