package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import entities.Utilisateur;
import entities.Role;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import services.UtilisateurService;

import java.util.List;
import java.util.regex.Pattern;

import static controllers.SignUpController.isValidEmail;

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
    private TextField txtNom;
    @FXML
    private TextField txtPrenom;
    @FXML
    private TextField txtEmail;
    @FXML
    private ComboBox<Role> comboRole;
    @FXML
    private TextField searchField;

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

    private void loadUserData() {
        List<Utilisateur> users = utilisateurService.getAllData();
        allUsers.setAll(users);

        colNom.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));
        colPrenom.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrenom()));
        colEmail.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        colRole.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole().toString()));
        colDateInscription.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDateInscription().toString()));

        tableUsers.setItems(allUsers);
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
        }
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
        }
    }

    private String validateInputs(String nom, String prenom, String email, Role role) {
        if (nom.isEmpty()) return "Nom requis.";
        if (prenom.isEmpty()) return "Prénom requis.";
        if (nom.matches(".*\\d.*")) return "Le nom ne peut pas contenir de chiffres.";
        if (prenom.matches(".*\\d.*")) return "Le prénom ne peut pas contenir de chiffres.";
        if (email.isEmpty()) return "Email vide.";
        if (!isValidEmail(email)) return "Email invalide.";
        if (role == null) return "Rôle requis.";
        return null;
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
    @FXML
    public void Logout(Event event){
        Utilisateur utilisateur = SharedDataController.getInstance().getUtilisateur();
        utilisateurService.deconnexion(utilisateur);
        Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        HBox titleBar = NavigationUtils.createCustomTitleBar(currentStage);
        NavigationUtils.switchPage("/Login.fxml", currentStage, titleBar);
    }
}
