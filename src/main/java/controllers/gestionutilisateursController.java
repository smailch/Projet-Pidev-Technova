package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import entities.Utilisateur;
import entities.Role;
import services.UtilisateurService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class gestionutilisateursController {

    @FXML
    private TableView<Utilisateur> tableUsers;
    @FXML
    private TableColumn<Utilisateur, String> colNom;
    @FXML
    private TableColumn<Utilisateur, String> colPrenom;
    @FXML
    private TableColumn<Utilisateur, String> colEmail;
    @FXML
    private TableColumn<Utilisateur, String> colRole;
    @FXML
    private TableColumn<Utilisateur, String> colDateInscription;
    @FXML
    private TableColumn<Utilisateur, String> colAge;
    @FXML
    private TableColumn<Utilisateur, String> colActiver;

    @FXML
    private TextField txtNom;
    @FXML
    private TextField txtPrenom;
    @FXML
    private TextField txtEmail;
    @FXML
    private ComboBox<Role> comboRole;
    @FXML
    private TextField searchField;
    @FXML



    private final UtilisateurService utilisateurService = new UtilisateurService();
    private final ObservableList<Utilisateur> allUsers = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        loadUserData();
        comboRole.getItems().setAll(Role.values());
        searchField.setOnKeyReleased(this::handleSearch);

        tableUsers.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtNom.setText(newSelection.getNom());
                txtPrenom.setText(newSelection.getPrenom());
                txtEmail.setText(newSelection.getEmail());
                comboRole.setValue(newSelection.getRole());

            }
        });
    }



    @FXML
    public void handleSearch(KeyEvent event) {
        String query = searchField.getText().toLowerCase();
        filterData(query);
        clearFields();
    }

    private void filterData(String query) {
        ObservableList<Utilisateur> filteredList = FXCollections.observableArrayList();
        for (Utilisateur user : allUsers) {
            if (user.getNom().toLowerCase().contains(query) || user.getPrenom().toLowerCase().contains(query) || user.getEmail().toLowerCase().contains(query)) {
                filteredList.add(user);
            }
        }
        tableUsers.setItems(filteredList);
    }

    @FXML
    public void deleteUser() {
        Utilisateur selectedUser = tableUsers.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            utilisateurService.deleteEntity(selectedUser);
            loadUserData();

            clearFields();

            // Instanciation de WriteToFileHistorique
            WriteToFileHistorique writeToFileHistorique = new WriteToFileHistorique();

            // Récupération des informations de l'administrateur connecté
            String adminNom = SharedDataController.getInstance().getUserNom();
            String adminEmail = SharedDataController.getInstance().getUserEmail();

            // Formatage du message avec date et heure
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = now.format(formatter);

            // Récupération des informations supplémentaires de l'utilisateur
            String prenom = selectedUser.getPrenom();
            String role = selectedUser.getRole().toString();
            String dateInscription = selectedUser.getDateInscription().toString();

            // Création du message détaillé
            String message = "Suppression de " + selectedUser.getNom() + " " + prenom + " (Rôle: " + role + ", Date d'inscription: " + dateInscription + ") par L'Admin : " + adminNom + " (" + adminEmail + ") le " + formattedDateTime;

            // Appel de la méthode ecrireDansFichier
            writeToFileHistorique.ecrireDansFichier(message);
        }
    }
   private void loadUserData() {
    List<Utilisateur> users = utilisateurService.getAllData();
    allUsers.setAll(users);

    // Configuration des colonnes existantes
    colNom.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));
    colPrenom.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrenom()));
    colEmail.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
    colRole.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole().toString()));
    colDateInscription.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDateInscription().toString()));

    // Configuration de la colonne pour l'âge en années et jours
    colAge.setCellValueFactory(cellData -> {
        Date dateInscription = cellData.getValue().getDateInscription();
        LocalDate localDateInscription = LocalDate.parse(dateInscription.toString());
        LocalDate now = LocalDate.now();

        long years = ChronoUnit.YEARS.between(localDateInscription, now);
        LocalDate tempDate = localDateInscription.plusYears(years);
        long days = ChronoUnit.DAYS.between(tempDate, now);

        String age = years + " ans et " + days + " jours";
        return new SimpleStringProperty(age);
    });
    colActiver.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getActiver()    == 1 ? "Oui" : "Non"));
    tableUsers.setItems(allUsers);
}

    @FXML
    public void saveUser() {
        Utilisateur selectedUser = tableUsers.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            String nom = txtNom.getText().trim();
            String prenom = txtPrenom.getText().trim();
            String email = txtEmail.getText().trim();
            Role role = comboRole.getValue();

            String errorMessage = validateInputs(nom, prenom, email, role);

            if (errorMessage != null) {
                showAlert("Erreur", errorMessage, Alert.AlertType.ERROR);
                return;
            }

            selectedUser.setNom(nom);
            selectedUser.setPrenom(prenom);
            selectedUser.setEmail(email);
            selectedUser.setRole(role);
            utilisateurService.updateEntity(selectedUser);
            loadUserData();
            clearFields();
            // Instanciation de WriteToFileHistorique
            WriteToFileHistorique writeToFileHistorique = new WriteToFileHistorique();

            // Récupération des informations de l'administrateur connecté
            String adminNom = SharedDataController.getInstance().getUserNom();
            String adminEmail = SharedDataController.getInstance().getUserEmail();

            // Formatage du message avec date et heure
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = now.format(formatter);

            // Création du message détaillé
            String message = "Modification de " + selectedUser.getNom() + " " + prenom + " (Rôle: " + role + ", Email: " + email + ") par L'Admin : " + adminNom + " (" + adminEmail + ") le " + formattedDateTime;

            // Appel de la méthode ecrireDansFichier
            writeToFileHistorique.ecrireDansFichier(message);
        }
    }

    private String validateInputs(String nom, String prenom, String email, Role role) {
        if (nom.isEmpty()) return "Nom requis.";
        if (prenom.isEmpty()) return "Prénom requis.";
        if (email.isEmpty() || !isValidEmail(email)) return "Email invalide.";
        if (role == null) return "Rôle requis.";
        return null;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return Pattern.matches(emailRegex, email);
    }

    private void clearFields() {
        txtNom.clear();
        txtPrenom.clear();
        txtEmail.clear();
        comboRole.setValue(null);
    }

    @FXML
    public void SearchButton() {
        searchField.clear();
        loadUserData();
        clearFields();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Method to activate a user
@FXML
public void activateUser() {
    Utilisateur selectedUser = tableUsers.getSelectionModel().getSelectedItem();
    if (selectedUser != null) {
        utilisateurService.activateUser(selectedUser);
        loadUserData();
        clearFields();
        logUserAction("activé", selectedUser);



        // Instanciation de WriteToFileHistorique
        WriteToFileHistorique writeToFileHistorique = new WriteToFileHistorique();
        String adminNom = SharedDataController.getInstance().getUserNom();
        String adminEmail = SharedDataController.getInstance().getUserEmail();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);
        String prenom = selectedUser.getPrenom();
        String role = selectedUser.getRole().toString();
        String dateInscription = selectedUser.getDateInscription().toString();
        String message = "Activation de " + selectedUser.getNom() + " " + prenom + " (Role: " + role + ", Date d inscription: " + dateInscription + ") par L Admin : " + adminNom + " (" + adminEmail + ") le " + formattedDateTime;

    }
}

// Method to deactivate a user
@FXML
public void deactivateUser() {
    Utilisateur selectedUser = tableUsers.getSelectionModel().getSelectedItem();
    if (selectedUser != null) {
        utilisateurService.deactivateUser(selectedUser);
        loadUserData();
        clearFields();

        logUserAction("désactivé", selectedUser);






        // Instanciation de WriteToFileHistorique
        WriteToFileHistorique writeToFileHistorique = new WriteToFileHistorique();
        String adminNom = SharedDataController.getInstance().getUserNom();
        String adminEmail = SharedDataController.getInstance().getUserEmail();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);
        String prenom = selectedUser.getPrenom();
        String role = selectedUser.getRole().toString();
        String dateInscription = selectedUser.getDateInscription().toString();
        String message = "Desactivation de " + selectedUser.getNom() + " " + prenom + " (Role: " + role + ", Date d inscription: " + dateInscription + ") par L Admin : " + adminNom + " (" + adminEmail + ") le " + formattedDateTime;
    }
}

// Helper method to log user actions
private void logUserAction(String action, Utilisateur user) {
    WriteToFileHistorique writeToFileHistorique = new WriteToFileHistorique();
    String adminNom = SharedDataController.getInstance().getUserNom();
    String adminEmail = SharedDataController.getInstance().getUserEmail();
    LocalDateTime now = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    String formattedDateTime = now.format(formatter);
    String message = "Utilisateur " + user.getNom() + " " + user.getPrenom() + " (Email: " + user.getEmail() + ") a été " + action + " par l'Admin : " + adminNom + " (" + adminEmail + ") le " + formattedDateTime;
    writeToFileHistorique.ecrireDansFichier(message);
}

}
