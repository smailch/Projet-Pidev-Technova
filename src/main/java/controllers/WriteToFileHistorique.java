package controllers;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public  class WriteToFileHistorique {

    private String filePath;

    public WriteToFileHistorique() {
        this.filePath = "C:\\Users\\ichaa\\Desktop\\ESPRIT\\ESPRIT 3A\\Git Projects\\Nouveau dossier\\Projet-Pidev-Technova\\incident\\Projet-Pidev-Technova\\Historique.txt";
    }

    public void ecrireDansFichier(String contenu) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(contenu);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}