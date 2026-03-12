package clubvideo.model;

import java.time.LocalDate;

public class Cassette {
    private int        noCassette;
    private LocalDate  dateAchat;
    private String     titre;
    private String     auteur;
    private int        duree;       // minutes
    private double     prix;
    private int        idCategorie;
    private String     libelleCategorie; // dénormalisé pour affichage

    public Cassette() {}

    // ── Getters / Setters ──────────────────────────────────
    public int        getNoCassette()              { return noCassette; }
    public void       setNoCassette(int v)         { this.noCassette = v; }

    public LocalDate  getDateAchat()               { return dateAchat; }
    public void       setDateAchat(LocalDate v)    { this.dateAchat = v; }

    public String     getTitre()                   { return titre; }
    public void       setTitre(String v)           { this.titre = v; }

    public String     getAuteur()                  { return auteur; }
    public void       setAuteur(String v)          { this.auteur = v; }

    public int        getDuree()                   { return duree; }
    public void       setDuree(int v)              { this.duree = v; }

    public double     getPrix()                    { return prix; }
    public void       setPrix(double v)            { this.prix = v; }

    public int        getIdCategorie()             { return idCategorie; }
    public void       setIdCategorie(int v)        { this.idCategorie = v; }

    public String     getLibelleCategorie()        { return libelleCategorie; }
    public void       setLibelleCategorie(String v){ this.libelleCategorie = v; }

    @Override public String toString() { return "[" + noCassette + "] " + titre; }
}
