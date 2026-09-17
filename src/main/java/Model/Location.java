package Model;

import java.util.Date;

public class Location {
    private int id;
    private Date dateDebut;
    private Date dateFin;
    private double prix;
    private String statut;
    private String conditions;
    private int idVoiture;
    private int idClient;
    private String nomClient;
    private String modeleVoiture;

    public Location(int id, Date dateDebut, Date dateFin, double prix, String statut, String conditions,
                    int idVoiture, int idClient, String nomClient, String modeleVoiture) {
        this.id = id;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.prix = prix;
        this.statut = statut;
        this.conditions = conditions;
        this.idVoiture = idVoiture;
        this.idClient = idClient;
        this.nomClient = nomClient;
        this.modeleVoiture = modeleVoiture;
    }

    public int getId() { return id; }
    public Date getDateDebut() { return dateDebut; }
    public Date getDateFin() { return dateFin; }
    public double getPrix() { return prix; }
    public String getStatut() { return statut; }
    public String getConditions() { return conditions; }
    public int getIdVoiture() { return idVoiture; }
    public int getIdClient() { return idClient; }
    public String getNomClient() { return nomClient; }
    public String getModeleVoiture() { return modeleVoiture; }
}
