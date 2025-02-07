package entities;

public class DossierFiscale {

    private int id;
    private int idUser;
    private int anneeFiscale;
    private double totalImpot;
    private double totalImpotPaye;
    private String status;
    private String dateCreation;
    private String moyenPaiement;


    public DossierFiscale() {
    }

    public DossierFiscale(int id, int idUser, int anneeFiscale, double totalImpot, double totalImpotPaye,
                          String status, String dateCreation, String moyenPaiement) {
        this.id = id;
        this.idUser = idUser;
        this.anneeFiscale = anneeFiscale;
        this.totalImpot = totalImpot;
        this.totalImpotPaye = totalImpotPaye;
        this.status = status;
        this.dateCreation = dateCreation;
        this.moyenPaiement = moyenPaiement;
    }

    // Getters et setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getAnneeFiscale() {
        return anneeFiscale;
    }

    public void setAnneeFiscale(int anneeFiscale) {
        this.anneeFiscale = anneeFiscale;
    }

    public double getTotalImpot() {
        return totalImpot;
    }

    public void setTotalImpot(double totalImpot) {
        this.totalImpot = totalImpot;
    }

    public double getTotalImpotPaye() {
        return totalImpotPaye;
    }

    public void setTotalImpotPaye(double totalImpotPaye) {
        this.totalImpotPaye = totalImpotPaye;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(String dateCreation) {
        this.dateCreation = dateCreation;
    }

    public String getMoyenPaiement() {
        return moyenPaiement;
    }

    public void setMoyenPaiement(String moyenPaiement) {
        this.moyenPaiement = moyenPaiement;
    }

    // Méthode toString() pour afficher les informations de l'objet
    @Override
    public String toString() {
        return "id: " + id + ", id_user: " + idUser + ", annee_fiscale: " + anneeFiscale +
                ", total_impot: " + totalImpot + ", total_impot_paye: " + totalImpotPaye +
                ", status: " + status + ", date_creation: " + dateCreation + ", moyen_paiement: " + moyenPaiement+"\n";
    }
}
