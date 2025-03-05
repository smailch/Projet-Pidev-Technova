package entities;

public class Validation {

    private int id;
    private int idDocument;  // Référence à l'id du document
    private String statut;
    private String dateValidation;  // Date de validation (en String ou Date selon ton besoin)
    private String remarque;

    // Constructeur par défaut
    public Validation() {
    }

    // Constructeur avec paramètres
    public Validation(int id, int idDocument, String statut, String dateValidation, String remarque) {
        this.id = id;
        this.idDocument = idDocument;
        this.statut = statut;
        this.dateValidation = dateValidation;
        this.remarque = remarque;
    }

    // Getters et setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdDocument() {
        return idDocument;
    }

    public void setIdDocument(int idDocument) {
        this.idDocument = idDocument;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getDateValidation() {
        return dateValidation;
    }

    public void setDateValidation(String dateValidation) {
        this.dateValidation = dateValidation;
    }

    public String getRemarque() {
        return remarque;
    }

    public void setRemarque(String remarque) {
        this.remarque = remarque;
    }

    @Override
    public String toString() {
        return "id: " + id + ", id_document: " + idDocument + ", statut: " + statut +
                ", date_validation: " + dateValidation + ", remarque: " + remarque + "\n";
    }


}
