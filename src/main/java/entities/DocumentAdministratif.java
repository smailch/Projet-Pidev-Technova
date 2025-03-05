package entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DocumentAdministratif {

    private int id;
    private String nomDocument;
    private String cheminFichier;
    private LocalDate dateEmission;  // Changed from String to LocalDateTime
    private String status;
    private String remarque;

    public DocumentAdministratif() {
    }

    // Constructor without dateEmission (database fills it automatically)
    public DocumentAdministratif(int id, String nomDocument, String cheminFichier, String status, String remarque) {
        this.id = id;
        this.nomDocument = nomDocument;
        this.cheminFichier = cheminFichier;
        this.status = status;
        this.remarque = remarque;
    }

    // Constructor including dateEmission (used when retrieving from DB)
    public DocumentAdministratif(int id, String nomDocument, String cheminFichier,
                                 LocalDate dateEmission, String status, String remarque) {
        this.id = id;
        this.nomDocument = nomDocument;
        this.cheminFichier = cheminFichier;
        this.dateEmission = dateEmission;
        this.status = status;
        this.remarque = remarque;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomDocument() {
        return nomDocument;
    }

    public void setNomDocument(String nomDocument) {
        this.nomDocument = nomDocument;
    }

    public String getCheminFichier() {
        return cheminFichier;
    }

    public void setCheminFichier(String cheminFichier) {
        this.cheminFichier = cheminFichier;
    }

    public LocalDate getDateEmission() {
        return dateEmission;
    }

    public void setDateEmission(LocalDate dateEmission) {
        this.dateEmission = dateEmission;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarque() {
        return remarque;
    }

    public void setRemarque(String remarque) {
        this.remarque = remarque;
    }

    @Override
    public String toString() {
        return "id: " + id + ", nomDocument: " + nomDocument +
                ", cheminFichier: " + cheminFichier +
                ", dateEmission: " + (dateEmission != null ? dateEmission.toString() : "Non défini") +
                ", status: " + status + ", remarque: " + remarque + "\n";
    }
}
