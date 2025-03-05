package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import services.MessageService;
import tools.MyConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class MessageController {

    @FXML
    private TextArea messageTextArea;
    @FXML
    private ComboBox<String> receiverComboBox;
    @FXML
    private TextField messageField;

    private final MessageService messageService = new MessageService("C:\\Users\\ichaa\\Desktop\\ESPRIT\\ESPRIT 3A\\Git Projects\\Nouveau dossier\\Projet-Pidev-Technova\\incident\\Projet-Pidev-Technova\\messages.txt");

    @FXML
    public void initialize() {
        loadMessages();
        loadAdminUsers();
    }

    @FXML
    public void handleSendMessage() {
        String sender = SharedDataController.getInstance().getUserNom(); // Get the sender from SharedDataController
        String receiver = receiverComboBox.getValue();
        String message = messageField.getText();
        messageService.saveMessage(sender, receiver, message);
        loadMessages();
    }

    private void loadMessages() {
    List<String> messages = messageService.readMessages();
    StringBuilder allMessages = new StringBuilder();
    for (String message : messages) {
        if (message.startsWith("From: ")) {
            allMessages.append("--------------------------------------------").append("\n").append(message).append("\n");
        } else if (message.startsWith("To: ") || message.startsWith("Message: ")) {
            allMessages.append(message).append("\n");

        } else {
            System.out.println("Invalid message format: " + message);
        }
        // Add a blue line separator after each message

    }
    // Add the new line here


    messageTextArea.setText(allMessages.toString());
}

    private void loadAdminUsers() {
        ObservableList<String> adminUsers = FXCollections.observableArrayList();
        adminUsers.add("tout le monde"); // Add "tout le monde" option
        try {
            Connection cnx = MyConnection.getInstance().getCnx();
            String query = "SELECT nom FROM Utilisateur WHERE role = 'Admin'";
            PreparedStatement pstmt = cnx.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                adminUsers.add(rs.getString("nom"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        receiverComboBox.setItems(adminUsers);
    }
    @FXML
    private void handleRetour(ActionEvent event) {
        Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        HBox titleBar = NavigationUtils.createCustomTitleBar(currentStage);
        NavigationUtils.switchPage("/gestionutilisateurs.fxml", currentStage, titleBar);

    }
}