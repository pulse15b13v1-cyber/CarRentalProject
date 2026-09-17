package Model;

public class Voiture {
    private int id;
    private String marque;
    private String modele;
    private String immatricule;
    private double tarif;
    private boolean disponible;

    public Voiture(int id, String marque, String modele, String immatricule, double tarif, boolean disponible) {
        this.id = id;
        this.marque = marque;
        this.modele = modele;
        this.immatricule = immatricule;
        this.tarif = tarif;
        this.disponible = disponible;
    }

    public int getId() { return id; }
    public String getMarque() { return marque; }
    public String getModele() { return modele; }
    public String getImmatricule() { return immatricule; }
    public double getTarif() { return tarif; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean dispo) { this.disponible = dispo; }
}