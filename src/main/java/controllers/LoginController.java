package controllers;

import entities.Role;
import entities.Utilisateur;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import services.*;
import services.UtilisateurService;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import tools.MyConnection;
import services.PythonFaceService;

import static controllers.SignUpController.showAlert;

public class LoginController implements Initializable {

    @FXML
    private Label lblErrors;

    @FXML
    private TextField txtUsername;

    @FXML
    private TextField txtPassword;

    @FXML
    private Button btnSignin;
    @FXML
    private AnchorPane AnchorPane;
    @FXML
    private ImageView iconFace; // Doit correspondre au fx:id du FXML
    private UtilisateurService utilisateurService;
    private JwtService jwtService;

    Connection con = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;

    @FXML
    public void handleButtonAction(MouseEvent event) {
        if (event.getSource() == btnSignin) {
            Utilisateur utilisateur = logIn();
            if (utilisateur != null) {
                try {
                    if (utilisateur.getActiver() == 0) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Compte désactivé");
                        alert.setHeaderText(null);
                        alert.setContentText("❌ Votre compte est désactivé.");
                        alert.showAndWait();
                        return;
                    }
                    SharedDataController.getInstance().setUtilisateur(utilisateur);
                    SharedDataController.getInstance().setUserEmail(utilisateur.getEmail());

                    System.out.println("✅ Connexion réussie : " + utilisateur.getNom());

                    // Générer le JWT
                    String jwtToken = jwtService.generateToken(utilisateur);
                    System.out.println("🔑 Token JWT : " + jwtToken);

                    Node node = (Node) event.getSource();
                    Stage stage = (Stage) node.getScene().getWindow();
                    stage.close();

                    FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("gestionutilisateurs.fxml"));
                    Scene scene = new Scene(loader.load());
                    stage.setScene(scene);
                    stage.show();
                    SessionManager.getInstance().setUserId(utilisateur.getId());
                    SessionManager.getInstance().setUtilisateurConnecte(utilisateur);

                } catch (IOException ex) {
                    System.err.println("Error loading the scene: " + ex.getMessage());
                    setLblError(Color.TOMATO, "Failed to load next screen");
                }
            }
        }
    }

    @FXML
    public void redirectToForgotPassword(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ForgetPassword.fxml"));
            Scene scene = new Scene(loader.load());
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.setTitle("Mot de passe oublié");
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            setLblError(Color.TOMATO, "Erreur lors du chargement de l'écran de réinitialisation du mot de passe.");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        utilisateurService = new UtilisateurService();
        jwtService = new JwtService();
        con = MyConnection.getInstance().getCnx();

        if (con == null) {
            lblErrors.setTextFill(Color.TOMATO);
            lblErrors.setText("Server Error : Check");
        } else {
            lblErrors.setTextFill(Color.GREEN);
            lblErrors.setText("Server is up : Good to go");
        }
    }

    private Utilisateur logIn() {
        String email = txtUsername.getText();
        String password = txtPassword.getText();

        if (email.isEmpty() || password.isEmpty()) {
            setLblError(Color.TOMATO, "⚠️ Email et mot de passe requis !");
            return null;
        }

        Utilisateur utilisateur = utilisateurService.connexion(email, password);

        if (utilisateur == null) {
            setLblError(Color.TOMATO, "❌ Email ou mot de passe incorrect.");
        } else {
            setLblError(Color.GREEN, "✅ Connexion réussie !");
        }

        return utilisateur;
    }

    private void setLblError(Color color, String text) {
        lblErrors.setTextFill(color);
        lblErrors.setText(text);
        System.out.println(text);
    }

    @FXML
    public void redirectToSignUp(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SignUp.fxml"));
            Scene scene = new Scene(loader.load());
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.setTitle("SignUp");
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            setLblError(Color.TOMATO, "Erreur lors du chargement de l'écran de réinitialisation du SignUp.");
        }
    }


    public void handleFaceLoginAction(MouseEvent event) {
        if (event.getSource() == iconFace) {
            Utilisateur utilisateur = loginWithFace();

            if (utilisateur != null) {
                if (utilisateur.getActiver() == 0) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Compte désactivé");
                    alert.setHeaderText(null);
                    alert.setContentText("❌ Votre compte est désactivé.");
                    alert.showAndWait();
                    return;
                }
                System.out.println("✅ Connexion faciale réussie : " + utilisateur.getNom());

                String jwtToken = jwtService.generateToken(utilisateur);
                System.out.println("🔑 Token JWT : " + jwtToken);

                try {
                    Node node = (Node) event.getSource();
                    Stage stage = (Stage) node.getScene().getWindow();
                    stage.close();

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/gestionutilisateurs.fxml"));
                    Scene scene = new Scene(loader.load());
                    stage.setScene(scene);
                    stage.show();
                    SessionManager.getInstance().setUserId(utilisateur.getId());

                } catch (IOException ex) {
                    setLblError(Color.TOMATO, "Erreur lors du chargement du profil.");
                }
            }
        }
    }

    public Utilisateur loginWithFace() {
        try {
            // Capturer l'encodage du visage via le script Python
            String faceEmbedding = captureAndGetFaceEmbedding();

            if (faceEmbedding == "Aucun visage détecté") {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Erreur lors de la capture du visage.");
                alert.showAndWait();
                return null;
            }

            // Vérifier la similarité avec les encodages dans la base de données
            Connection cnx = MyConnection.getInstance().getCnx();
            String query = "SELECT * FROM Utilisateur WHERE visage_hash IS NOT NULL";  // Récupérer tous les utilisateurs

            try (PreparedStatement pstmt = cnx.prepareStatement(query)) {
                ResultSet rs = pstmt.executeQuery();
                Utilisateur matchedUser = null;
                double minDistance = Double.MAX_VALUE;
                double threshold = 0.45; // Seuil de similarité (distance plus petite est plus similaire)

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
                        matchedUser.setRole(Role.valueOf(rs.getString("role"))); // Conversion String -> Enum
                        matchedUser.setDateInscription(rs.getDate("dateInscription"));
                        matchedUser.setVisageHash(storedEmbedding);
                        matchedUser.setActiver(rs.getInt("activer"));

                        matchedUser.setVisageHash(storedEmbedding);
                    }
                }
                if (matchedUser == null || minDistance > threshold) {
                    showAlert("Avertissement", "Aucun utilisateur trouvé avec un visage similaire.", Alert.AlertType.WARNING);
                    return null;  // Refuser la connexion si aucune correspondance n'est trouvée ou si la distance est trop grande


                }else if (matchedUser != null) {

                    SharedDataController.getInstance().setUtilisateur(matchedUser);
                    SharedDataController.getInstance().setUserEmail(matchedUser.getEmail());
                    return matchedUser;
                }


            } catch (SQLException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                //alert.setContentText("Erreur lors de la connexion : " + e.getMessage());
                alert.setContentText("Erreur lors de la capture du visage.");
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
        return null;
    }


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

    public String captureAndGetFaceEmbedding() {
        try {
            // Chemin vers le script Python
            String pythonScriptPath = "C:\\Users\\USER\\Downloads\\Projet-Pidev-Technova (1)\\Projet-Pidev-Technova\\src\\main\\java\\controllers\\face_service.py";

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


}
