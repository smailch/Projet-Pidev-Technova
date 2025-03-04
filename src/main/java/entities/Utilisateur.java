package entities;

import java.util.Date;

public class Utilisateur {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private Role role;
    private Date dateInscription;
    private String motDePasse;
    private String visageHash;  // Le hash du visage (ajouté)
    private int activer;
    public Utilisateur(int id, String nom, String prenom, String email, java.sql.Date role, String dateInscription) {
        this.id = id;
    }

    public Utilisateur() {
    }

    public Utilisateur(String nom, String prenom, String email, Role role, Date dateInscription, String motDePasse,int activer) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.role = role;
        this.dateInscription = dateInscription;
        this.motDePasse = motDePasse;
        this.activer = activer;

    }

    public Utilisateur(int id, String nom, String prenom, String email, Role role, Date dateInscription , String motDePasse, int activer) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.role = role;
        this.dateInscription = dateInscription;
        this.motDePasse = motDePasse;
        this.activer = activer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public  String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Date getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(Date dateInscription) {
        this.dateInscription = dateInscription;
    }
    public String getMotDePasse() {
        return motDePasse;
    }
    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }
    public int getActiver() {
        return activer;
    }
    public void setActiver(int activer) {
        this.activer = activer;
    }





    @Override
    public String toString() {
        return "Utilisateur{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", dateInscription=" + dateInscription +
                '}';
    }
    public String getVisageHash() {
        return visageHash;
    }

    public void setVisageHash(String visageHash) {
        this.visageHash = visageHash;
    }

}
