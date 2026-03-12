package clubvideo.model;

import java.time.LocalDate;

public class Location {
    private int       noAbonne;
    private int       noCassette;
    private LocalDate dateLocation;
    private LocalDate dateRetour;

    // Dénormalisés pour TableView
    private String nomAbonne;
    private String titreCassette;

    public Location() {}

    public int       getNoAbonne()               { return noAbonne; }
    public void      setNoAbonne(int v)          { this.noAbonne = v; }

    public int       getNoCassette()             { return noCassette; }
    public void      setNoCassette(int v)        { this.noCassette = v; }

    public LocalDate getDateLocation()           { return dateLocation; }
    public void      setDateLocation(LocalDate v){ this.dateLocation = v; }

    public LocalDate getDateRetour()             { return dateRetour; }
    public void      setDateRetour(LocalDate v)  { this.dateRetour = v; }

    public String    getNomAbonne()              { return nomAbonne; }
    public void      setNomAbonne(String v)      { this.nomAbonne = v; }

    public String    getTitreCassette()          { return titreCassette; }
    public void      setTitreCassette(String v)  { this.titreCassette = v; }

    public boolean   isRetournee()               { return dateRetour != null; }
}
