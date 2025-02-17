package entities;

public class AssistantDocumentaire {

    private int id;
    private int idUtilisateur;
    private int idDocument;
    private String typeAssistance;
    private String dateDemande;
    private String status;
    private String remarque;
    private boolean rappelAutomatique;

    public AssistantDocumentaire() {
    }

    public AssistantDocumentaire(int id, int idUtilisateur, int idDocument, String typeAssistance,
                                 String dateDemande, String status, String remarque, boolean rappelAutomatique) {
        this.id = id;
        this.idUtilisateur = idUtilisateur;
        this.idDocument = idDocument;
        this.typeAssistance = typeAssistance;
        this.dateDemande = dateDemande;
        this.status = status;
        this.remarque = remarque;
        this.rappelAutomatique = rappelAutomatique;
    }

    // Getters et setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public int getIdDocument() {
        return idDocument;
    }

    public void setIdDocument(int idDocument) {
        this.idDocument = idDocument;
    }

    public String getTypeAssistance() {
        return typeAssistance;
    }

    public void setTypeAssistance(String typeAssistance) {
        this.typeAssistance = typeAssistance;
    }

    public String getDateDemande() {
        return dateDemande;
    }

    public void setDateDemande(String dateDemande) {
        this.dateDemande = dateDemande;
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

    public boolean isRappelAutomatique() {
        return rappelAutomatique;
    }

    public void setRappelAutomatique(boolean rappelAutomatique) {
        this.rappelAutomatique = rappelAutomatique;
    }

    @Override
    public String toString() {
        return "id: " + id + ", idUtilisateur: " + idUtilisateur + ", idDocument: " + idDocument +
                ", typeAssistance: " + typeAssistance + ", dateDemande: " + dateDemande +
                ", status: " + status + ", remarque: " + remarque + ", rappelAutomatique: " + rappelAutomatique + "\n";
    }
}
