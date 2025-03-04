package test;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import controllers.NavigationUtils;
import javafx.event.Event;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.json.JSONObject;
import services.DossierFiscaleService;
import services.PaymentProcessor;
import services.SessionManager;

import javax.sound.midi.SysexMessage;
import java.io.*;
import java.net.InetSocketAddress;

public class PaymentBackendServer {
    public static boolean etat;
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8083), 0);
        server.createContext("/process-payment", new PaymentHandler());
        server.setExecutor(null); // Creates a default executor
        server.start();
        System.out.println("Server started on http://localhost:8083");

    }

    static class PaymentHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Only handle POST requests
            if ("POST".equals(exchange.getRequestMethod())) {
                // Read the JSON string from the request body
                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
                BufferedReader reader = new BufferedReader(isr);
                StringBuilder jsonBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonBuilder.append(line);
                }
                reader.close();
                isr.close();

                // Parse the JSON string
                JSONObject jsonObject = new JSONObject(jsonBuilder.toString());
                String token = jsonObject.optString("token", "defaultToken");
                String userId = jsonObject.optString("userId", "defaultUserId");
                String dossierId = jsonObject.optString("dossierId", "defaultDossierId");

                System.out.println("Token: " + token);
                System.out.println("User ID: " + userId);
                System.out.println("Dossier ID: " + dossierId);

                // Extract the tokenId (assuming it's part of the token string)

                // Process the payment
                PaymentProcessor.processPayment(token,Integer.parseInt(userId),Integer.parseInt(dossierId));



                // Send a response
                String response = "Payment processed successfully!";
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
                etat=true;
            } else {
                // Handle non-POST requests if necessary
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
                etat=false;
            }
        }
    }

}
