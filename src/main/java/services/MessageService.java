package services;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MessageService {
    private final String filePath;

    public MessageService(String filePath) {
        this.filePath = filePath;
    }

    public void saveMessage(String sender, String receiver, String message) {
        String formattedMessage = String.format("From: %s\nTo: %s\nMessage: %s\n\n", sender, receiver, message);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(formattedMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> readMessages() {
        List<String> messages = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                messages.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return messages;
    }
}