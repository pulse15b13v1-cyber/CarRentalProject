package Model;

public class Client {
    private int id;
    private String nom;
    private String prenom;
    private int telephone; // Gardé en int pour correspondre à la BDD Numérique
    private String mail;
    private int idUtilisateur; // Ajouté pour correspondre à la BDD (optionnel mais bonne pratique)

    // Constructeur principal
    public Client(int id, String nom, String prenom, int telephone, String mail, int idUtilisateur) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.mail = mail;
        this.idUtilisateur = idUtilisateur;
    }

    // Constructeur sans idUtilisateur (si non géré activement partout pour l'instant)
    public Client(int id, String nom, String prenom, int telephone, String mail) {
        this(id, nom, prenom, telephone, mail, 0); // Mettre une valeur par défaut pour idUtilisateur ou gérer autrement
    }


    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public int getTelephone() { return telephone; }
    public String getMail() { return mail; }
    public int getIdUtilisateur() { return idUtilisateur; } // Getter pour idUtilisateur

    // Setters (optionnels, ajoutez si besoin)
    public void setId(int id) { this.id = id; }
    public void setNom(String nom) { this.nom = nom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public void setTelephone(int telephone) { this.telephone = telephone; }
    public void setMail(String mail) { this.mail = mail; }
    public void setIdUtilisateur(int idUtilisateur) { this.idUtilisateur = idUtilisateur; }
}