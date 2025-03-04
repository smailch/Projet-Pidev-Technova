package controllers;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public  class WriteToFileHistorique {

    private String filePath;

    public WriteToFileHistorique() {
        this.filePath = "C:\\Users\\chemlali smail\\OneDrive\\Bureau\\ProjetPI\\ProjetPiDev - Copie\\Historique.txt";
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