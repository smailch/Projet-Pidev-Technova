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
    public Utilisateur(int id, String nom, String prenom, String email, Role role, java.sql.Date dateInscription) {
        this.id = id;
    }

    public Utilisateur() {
    }

    public Utilisateur(String nom, String prenom, String email, Role role, Date dateInscription, String motDePasse) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.role = role;
        this.dateInscription = dateInscription;
        this.motDePasse = motDePasse;
    }

    public Utilisateur(int id, String nom, String prenom, String email, Role role, Date dateInscription , String motDePasse) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.role = role;
        this.dateInscription = dateInscription;
        this.motDePasse = motDePasse;
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

    public String getEmail() {
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
}
