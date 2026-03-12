package clubvideo.model;

import java.time.LocalDate;

public class Abonne {
    private int       noAbonne;
    private String    nomAbonne;
    private String    adresseAbonne;
    private LocalDate dateAbonnement;
    private LocalDate dateEntree;
    private int       nombreLocation;

    public Abonne() {}

    public int       getNoAbonne()                  { return noAbonne; }
    public void      setNoAbonne(int v)             { this.noAbonne = v; }

    public String    getNomAbonne()                 { return nomAbonne; }
    public void      setNomAbonne(String v)         { this.nomAbonne = v; }

    public String    getAdresseAbonne()             { return adresseAbonne; }
    public void      setAdresseAbonne(String v)     { this.adresseAbonne = v; }

    public LocalDate getDateAbonnement()            { return dateAbonnement; }
    public void      setDateAbonnement(LocalDate v) { this.dateAbonnement = v; }

    public LocalDate getDateEntree()                { return dateEntree; }
    public void      setDateEntree(LocalDate v)     { this.dateEntree = v; }

    public int       getNombreLocation()            { return nombreLocation; }
    public void      setNombreLocation(int v)       { this.nombreLocation = v; }

    @Override public String toString() { return "[" + noAbonne + "] " + nomAbonne; }
}
