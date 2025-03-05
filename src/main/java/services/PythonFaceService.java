package services;

import entities.Utilisateur;
import javafx.scene.control.Alert;
import tools.MyConnection;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class PythonFaceService {

    /**
     * Exécute le script Python qui capture le visage et retourne l'encodage sous forme de chaîne (pas JSON).
     */
    public String captureAndGetFaceEmbedding() {
        try {
            // Chemin vers le script Python
            String pythonScriptPath = "C:\\Users\\USER\\Downloads\\Projet-Pidev-Technova (1)\\Projet-Pidev-Technova\\src\\main\\java\\controllers\\face_serviceSignUp.py";

            // Vérifier si le fichier existe
            File scriptFile = new File(pythonScriptPath);
            if (!scriptFile.exists()) {
                System.out.println("Le fichier Python n'existe pas à l'emplacement spécifié : " + pythonScriptPath);
                return null;
            }

            // Exécuter le script Python
            ProcessBuilder processBuilder = new ProcessBuilder("python", pythonScriptPath);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            // Lire la sortie du script (l'encodage sous forme de chaîne)
            String faceEmbedding = reader.readLine();

            // Attendre la fin de l'exécution du script
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.out.println("Erreur lors de l'exécution du script Python.");
                return null;
            }

            return faceEmbedding;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Méthode pour calculer la distance euclidienne entre deux empreintes faciales sous forme de chaîne.
     */
    public double calculateEuclideanDistance(String embedding1, String embedding2) {
        if (embedding1 == null || embedding1.isEmpty() || embedding2 == null || embedding2.isEmpty()) {
            throw new IllegalArgumentException("Les empreintes faciales sont vides.");
        }

        // Convertir les chaînes d'empreintes en tableaux de doubles
        double[] embedding1Array = parseEmbedding(embedding1);
        double[] embedding2Array = parseEmbedding(embedding2);

        // Calculer la distance euclidienne
        double distance = 0.0;
        for (int i = 0; i < embedding1Array.length; i++) {
            distance += Math.pow(embedding1Array[i] - embedding2Array[i], 2);
        }
        return Math.sqrt(distance);
    }

    /**
     * Méthode pour convertir une chaîne d'empreinte en tableau de doubles.
     */
    private double[] parseEmbedding(String embedding) {
        // Enlever les espaces inutiles et convertir la chaîne en tableau de doubles
        String[] values = embedding.trim().split(" ");
        double[] array = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            array[i] = Double.parseDouble(values[i].trim());
        }
        return array;
    }


    /**
     * Vérifie si un utilisateur possède un visage similaire à l'empreinte capturée dans la base de données.
     * Utilise une mesure de distance pour vérifier la similarité.
     */
    public Utilisateur loginWithFace() {
        try {
            // Capturer l'encodage du visage via le script Python
            String faceEmbedding = captureAndGetFaceEmbedding();

            if (faceEmbedding == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Erreur lors de la capture du visage.");
                alert.showAndWait();
                return null;
            }

            // Vérifier la similarité avec les encodages dans la base de données
            Connection cnx = MyConnection.getInstance().getCnx();
            String query = "SELECT * FROM Utilisateur";  // Récupérer tous les utilisateurs

            try (PreparedStatement pstmt = cnx.prepareStatement(query)) {
                ResultSet rs = pstmt.executeQuery();
                Utilisateur matchedUser = null;
                double minDistance = Double.MAX_VALUE;
                double threshold = 0.7;  // Seuil de similarité (distance plus petite est plus similaire)

                while (rs.next()) {
                    String storedEmbedding = rs.getString("visage_hash");
                    // Calculer la distance euclidienne entre l'empreinte capturée et celle stockée
                    double distance = calculateEuclideanDistance(faceEmbedding, storedEmbedding);
                    if (distance < minDistance && distance <= threshold) {
                        minDistance = distance;
                        matchedUser = new Utilisateur();
                        matchedUser.setId(rs.getInt("id"));
                        matchedUser.setNom(rs.getString("nom"));
                        matchedUser.setPrenom(rs.getString("prenom"));
                        matchedUser.setEmail(rs.getString("email"));
                        matchedUser.setVisageHash(storedEmbedding);
                    }
                }

                if (matchedUser != null) {
                    return matchedUser;
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Avertissement");
                    alert.setHeaderText(null);
                    alert.setContentText("Aucun utilisateur trouvé avec un visage similaire.");
                    alert.showAndWait();
                    return null;
                }

            } catch (SQLException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Erreur lors de la connexion : " + e.getMessage());
                alert.showAndWait();
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de la connexion : " + e.getMessage());
            alert.showAndWait();
            return null;
        }
    }
}
