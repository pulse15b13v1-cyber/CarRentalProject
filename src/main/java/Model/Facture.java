package Model;

import java.util.Date;

public class Facture {
    private int id;
    private double montant;
    private Date dateFacture;
    private int idClient;
    private int idLocation;
    private String clientNom;
    private String voitureModele;

    public Facture(int id, double montant, Date dateFacture, int idClient, int idLocation, String clientNom, String voitureModele) {
        this.id = id;
        this.montant = montant;
        this.dateFacture = dateFacture;
        this.idClient = idClient;
        this.idLocation = idLocation;
        this.clientNom = clientNom;
        this.voitureModele = voitureModele;
    }

    public int getId() { return id; }
    public double getMontant() { return montant; }
    public Date getDateFacture() { return dateFacture; }
    public int getIdClient() { return idClient; }
    public int getIdLocation() { return idLocation; }
    public String getClientNom() { return clientNom; }
    public String getVoitureModele() { return voitureModele; }
}