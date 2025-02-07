package entities;

public class DocumentAdministratif {

    private int id;
    private int idDossier;
    private String nomDocument;
    private String cheminFichier;
    private String dateEmission;
    private String status;
    private String remarque;

    public DocumentAdministratif() {
    }

    public DocumentAdministratif(int id, int idDossier, String nomDocument, String cheminFichier,
                                 String dateEmission, String status, String remarque) {
        this.id = id;
        this.idDossier = idDossier;
        this.nomDocument = nomDocument;
        this.cheminFichier = cheminFichier;
        this.dateEmission = dateEmission;
        this.status = status;
        this.remarque = remarque;
    }

    // Getters et setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdDossier() {
        return idDossier;
    }

    public void setIdDossier(int idDossier) {
        this.idDossier = idDossier;
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

    public String getDateEmission() {
        return dateEmission;
    }

    public void setDateEmission(String dateEmission) {
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
        return "id: " + id + ", id_dossier: " + idDossier + ", nom_document: " + nomDocument +
                ", chemin_fichier: " + cheminFichier + ", date_emission: " + dateEmission +
                ", status: " + status + ", remarque: " + remarque+"\n";
    }
}
