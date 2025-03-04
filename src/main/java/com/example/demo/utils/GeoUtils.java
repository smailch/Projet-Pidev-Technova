package com.example.demo.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URL;
import java.util.Scanner;


public class GeoUtils {

    public static double[] getCoordinates(String placeName) {
        String urlString = "https://nominatim.openstreetmap.org/search?format=json&q=" + placeName.replace(" ", "%20");
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            Scanner scanner = new Scanner(url.openStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNext()) {
                response.append(scanner.nextLine());
            }
            scanner.close();

            // Parse JSON response
            JSONArray jsonArray = new JSONArray(response.toString());
            if (jsonArray.length() > 0) {
                JSONObject location = jsonArray.getJSONObject(0);
                double lat = location.getDouble("lat");
                double lon = location.getDouble("lon");
                return new double[]{lat, lon};
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        double[] coords = getCoordinates("Tunis");
        if (coords != null) {
            System.out.println("Latitude: " + coords[0] + ", Longitude: " + coords[1]);
        } else {
            System.out.println("Could not retrieve coordinates.");
        }
    }
}