package entities;

public class DeclarationRevenues {
    private int id;
    private int idDossier;
    private double montantRevenu;
    private String sourceRevenu;
    private String dateDeclaration;
    private String preuveRevenu;


    public DeclarationRevenues() {
    }

    public DeclarationRevenues(int id, int idDossier, double montantRevenu, String sourceRevenu, String dateDeclaration, String preuveRevenu) {
        this.id = id;
        this.idDossier = idDossier;
        this.montantRevenu = montantRevenu;
        this.sourceRevenu = sourceRevenu;
        this.dateDeclaration = dateDeclaration;
        this.preuveRevenu = preuveRevenu;

    }

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

    public double getMontantRevenu() {
        return montantRevenu;
    }

    public void setMontantRevenu(double montantRevenu) {
        this.montantRevenu = montantRevenu;
    }

    public String getSourceRevenu() {
        return sourceRevenu;
    }

    public void setSourceRevenu(String sourceRevenu) {
        this.sourceRevenu = sourceRevenu;
    }

    public String getDateDeclaration() {
        return dateDeclaration;
    }

    public void setDateDeclaration(String dateDeclaration) {
        this.dateDeclaration = dateDeclaration;
    }

    public String getPreuveRevenu() {
        return preuveRevenu;
    }

    public void setPreuveRevenu(String preuveRevenu) {
        this.preuveRevenu = preuveRevenu;
    }





    @Override
    public String toString() {
        return "DeclarationRevenues{" +
                "id=" + id +
                ", idDossier=" + idDossier +
                ", montantRevenu=" + montantRevenu +
                ", sourceRevenu='" + sourceRevenu + '\'' +
                ", dateDeclaration='" + dateDeclaration + '\'' +
                ", preuveRevenu='" + preuveRevenu + '\'' +
                '}';
    }
}
