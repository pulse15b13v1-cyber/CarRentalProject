package Model;

public class Utilisateur {
    private int id;
    private String login;
    private String motDePasse;
    private String typeUtilisateur;

    public Utilisateur(int id, String login, String motDePasse, String typeUtilisateur) {
        this.id = id;
        this.login = login;
        this.motDePasse = motDePasse;
        this.typeUtilisateur = typeUtilisateur;
    }

    public int getId() { return id; }
    public String getLogin() { return login; }
    public String getMotDePasse() { return motDePasse; }
    public String getTypeUtilisateur() { return typeUtilisateur; }
}
