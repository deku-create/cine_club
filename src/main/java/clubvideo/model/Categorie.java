package clubvideo.model;

// ════════════════════════════════════════════════════════════
//  MODÈLES — Club Vidéo
// ════════════════════════════════════════════════════════════

/** Représente une catégorie de cassettes. */
public class Categorie {
    private int    idCategorie;
    private String libelle;

    public Categorie() {}
    public Categorie(int id, String libelle) { this.idCategorie = id; this.libelle = libelle; }

    public int    getIdCategorie()         { return idCategorie; }
    public void   setIdCategorie(int v)    { this.idCategorie = v; }
    public String getLibelle()             { return libelle; }
    public void   setLibelle(String v)     { this.libelle = v; }

    @Override public String toString() { return libelle; }
}
