package com.example.demo.services;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import org.json.JSONObject;

public class GoogleMapsService {

    private static final String API_KEY = "AIzaSyDFUn6EuiuNTZ0TsETQ-BhCpmMcvOA7FME";

    // Utilisation de l'API de géocodage inverse
    public static String fetchAddress(double latitude, double longitude) {
        try {
            String urlString = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&key=" + API_KEY;
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            // Traitement de la réponse JSON
            JSONObject jsonResponse = new JSONObject(content.toString());
            if (jsonResponse.getJSONArray("results").length() > 0) {
                return jsonResponse.getJSONArray("results").getJSONObject(0).getString("formatted_address");
            } else {
                return "Adresse non trouvée";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors de la récupération de l'adresse";
        }
    }
}
