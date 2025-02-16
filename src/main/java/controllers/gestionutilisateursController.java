package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import entities.Utilisateur;
import entities.Role;
import services.UtilisateurService;

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
    private TextField txtNom;
    @FXML
    private TextField txtPrenom;
    @FXML
    private TextField txtEmail;
    @FXML
    private ComboBox<Role> comboRole;
    @FXML
    private Label lblNomError;
    @FXML
    private Label lblPrenomError;
    @FXML
    private Label lblEmailError;
    @FXML
    private Label lblRoleError;
    @FXML
    private TextField searchField;

    private UtilisateurService utilisateurService = new UtilisateurService();
    private ObservableList<Utilisateur> allUsers = FXCollections.observableArrayList();

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
                clearErrorMessages();
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
            String nom = txtNom.getText();
            String prenom = txtPrenom.getText();
            String email = txtEmail.getText();
            Role role = comboRole.getValue();

            clearErrorMessages();

            if (nom.isEmpty()) {
                lblNomError.setText("Nom requis");
                lblNomError.setVisible(true);
                return;
            }
            if (prenom.isEmpty()) {
                lblPrenomError.setText("Prénom requis");
                lblPrenomError.setVisible(true);
                return;
            }
            if (email.isEmpty() || !isValidEmail(email)) {
                lblEmailError.setText("Email invalide");
                lblEmailError.setVisible(true);
                return;
            }
            if (role == null) {
                lblRoleError.setText("Rôle requis");
                lblRoleError.setVisible(true);
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

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return Pattern.matches(emailRegex, email);
    }

    private void clearFields() {
        txtNom.clear();
        txtPrenom.clear();
        txtEmail.clear();
        comboRole.setValue(null);
        clearErrorMessages();
    }

    private void clearErrorMessages() {
        lblNomError.setText("");
        lblNomError.setVisible(false);
        lblPrenomError.setText("");
        lblPrenomError.setVisible(false);
        lblEmailError.setText("");
        lblEmailError.setVisible(false);
        lblRoleError.setText("");
        lblRoleError.setVisible(false);
    }

    @FXML
    public void SearchButton() {
        searchField.clear();
        loadUserData();
        clearFields();
    }
}