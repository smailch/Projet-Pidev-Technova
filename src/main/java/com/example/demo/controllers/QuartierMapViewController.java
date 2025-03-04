package com.example.demo.controllers;

import com.example.demo.models.Lampadaire;
import com.example.demo.models.Quartier;
import com.example.demo.services.LampadaireService;
import com.sothawo.mapjfx.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONObject;

public class QuartierMapViewController {

    @FXML private MapView mapView;
    @FXML private Label quartierNameLabel;
    @FXML private Label lampadairesInfoLabel;
    @FXML private VBox markerDetailsContainer;
    @FXML private ProgressIndicator loadingIndicator;

    private Quartier quartier;
    private LampadaireService lampadaireService = new LampadaireService();
    private List<Marker> markers = new ArrayList<>();

    // Cache for geocoded locations
    private Map<String, Coordinate> locationCache = new HashMap<>();

    // Constants
    private static final Coordinate TUNISIA_CENTER = new Coordinate(36.8625, 10.1956);
    private static final int DEFAULT_ZOOM = 13;
    private static final String NOMINATIM_API = "https://nominatim.openstreetmap.org/search?format=json&limit=1&q=";
    // Tunisia bounding box to improve geocoding accuracy
    private static final String TUNISIA_VIEWBOX = "&viewbox=8.0,30.0,12.0,38.0&bounded=1";
    private static final String COUNTRY_CODE = "&countrycodes=tn";

    @FXML
    public void initialize() {
        // Log startup
        logActivity("Map view initializing");

        // Initially hide the loading indicator
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(false);
        }

        // Set zoom level before initialization
        mapView.setZoom(DEFAULT_ZOOM);

        // Initialize the map
        mapView.initializedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                System.out.println("Map initialized");
                logActivity("Map initialized");

                // Configure map type
                mapView.setMapType(MapType.OSM);

                // Set initial center and zoom
                mapView.setCenter(TUNISIA_CENTER);

                // Apply CSS styling to the map container
                Platform.runLater(() -> {
                    if (mapView.getScene() != null) {
                        try {
                            String css = getClass().getResource("/com/example/demo/styles/modern-purple.css").toExternalForm();
                            mapView.getScene().getStylesheets().add(css);
                        } catch (Exception e) {
                            System.err.println("Failed to load CSS: " + e.getMessage());
                        }
                    }
                });

                // Wait a moment for the map to properly render, then add markers
                if (quartier != null) {
                    // Delay loading lampadaires to ensure map is fully ready
                    CompletableFuture.delayedExecutor(1, java.util.concurrent.TimeUnit.SECONDS)
                            .execute(() -> {
                                Platform.runLater(() -> loadLampadaires());
                            });
                }
            }
        });

        // Initialize the map
        mapView.initialize();
    }

    public void setQuartier(Quartier quartier) {
        this.quartier = quartier;
        quartierNameLabel.setText("District: " + quartier.getNom());

        logActivity("Setting quartier: " + quartier.getNom());

        if (mapView.initializedProperty().get()) {
            // Delay loading to ensure map is fully initialized
            CompletableFuture.delayedExecutor(1, java.util.concurrent.TimeUnit.SECONDS)
                    .execute(() -> {
                        Platform.runLater(() -> loadLampadaires());
                    });
        }
    }

    private void loadLampadaires() {
        try {
            if (loadingIndicator != null) {
                loadingIndicator.setVisible(true);
            }

            logActivity("Loading lampadaires for quartier: " + quartier.getNom());

            // Clear existing markers
            for (Marker marker : new ArrayList<>(markers)) {
                try {
                    mapView.removeMarker(marker);
                } catch (Exception e) {
                    // Ignore errors when removing markers
                }
            }
            markers.clear();

            // Get all lampadaires for this quartier
            List<Lampadaire> lampadaires = lampadaireService.getAllLampadaires()
                    .stream()
                    .filter(l -> l.getId_quartier() == quartier.getId())
                    .collect(Collectors.toList());

            System.out.println("Found " + lampadaires.size() + " lampadaires for quartier: " + quartier.getNom());

            // Update info label
            lampadairesInfoLabel.setText(String.format(
                    "Processing %d streetlight(s)...",
                    lampadaires.size()
            ));

            // Start a background task to process geocoding
            Task<Void> geocodingTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    // Default center coordinate
                    Coordinate center = TUNISIA_CENTER;
                    boolean hasValidCoordinate = false;

                    // List to store lampadaires with coordinates
                    List<Lampadaire> processedLamps = new ArrayList<>();

                    // Process each lampadaire
                    for (int i = 0; i < lampadaires.size(); i++) {
                        Lampadaire lamp = lampadaires.get(i);

                        // Update progress
                        final int currentIndex = i;
                        Platform.runLater(() -> {
                            lampadairesInfoLabel.setText(String.format(
                                    "Processing streetlight %d of %d...",
                                    currentIndex + 1,
                                    lampadaires.size()
                            ));
                        });

                        // Try to extract coordinates or geocode the location
                        Coordinate position = getCoordinatesForLocation(lamp.getLocalisation());

                        if (position != null) {
                            // Store a copy of the lamp with its position for later use
                            lamp.setLocalisation(position.getLatitude() + "," + position.getLongitude());
                            processedLamps.add(lamp);

                            // Use the first valid coordinate as center
                            if (!hasValidCoordinate) {
                                center = position;
                                hasValidCoordinate = true;
                            }
                        }

                        // Add a small delay to avoid overwhelming the geocoding API
                        Thread.sleep(100);
                    }

                    // Final coordinates for use in the UI thread
                    final Coordinate finalCenter = hasValidCoordinate ? center : TUNISIA_CENTER;
                    final List<Lampadaire> finalProcessedLamps = processedLamps;

                    // Update UI with processed lampadaires
                    Platform.runLater(() -> {
                        // Update lamp count information
                        lampadairesInfoLabel.setText(String.format(
                                "Showing %d of %d streetlight(s) - Total consumption: %.2f W",
                                finalProcessedLamps.size(),
                                lampadaires.size(),
                                quartier.getConsomTot()
                        ));

                        // Add all markers to map
                        for (Lampadaire lamp : finalProcessedLamps) {
                            double[] coords = extractCoordinates(lamp.getLocalisation());
                            if (coords != null) {
                                Coordinate position = new Coordinate(coords[0], coords[1]);

                                // Create a marker with different color based on lamp state
                                Marker marker;
                                if (lamp.isEtat()) {
                                    // Active lamp (blue)
                                    marker = Marker.createProvided(Marker.Provided.BLUE);
                                } else {
                                    // Inactive lamp (red)
                                    marker = Marker.createProvided(Marker.Provided.RED);
                                }

                                // Configure and add the marker
                                try {
                                    marker.setPosition(position);
                                    marker.setVisible(true);
                                    mapView.addMarker(marker);
                                    markers.add(marker);
                                } catch (Exception e) {
                                    System.err.println("Error adding marker: " + e.getMessage());
                                }
                            }
                        }

                        // Set the center and zoom of the map
                        mapView.setCenter(finalCenter);
                        mapView.setZoom(DEFAULT_ZOOM);

                        // Update buttons in the sidebar
                        updateLampButtons(finalProcessedLamps);

                        // Hide loading indicator
                        if (loadingIndicator != null) {
                            loadingIndicator.setVisible(false);
                        }
                    });

                    return null;
                }
            };

            // Handle any errors in the task
            geocodingTask.setOnFailed(e -> {
                Throwable exception = geocodingTask.getException();
                System.err.println("Error in geocoding task: " + exception.getMessage());
                exception.printStackTrace();

                Platform.runLater(() -> {
                    lampadairesInfoLabel.setText("Error processing locations. Please try again.");
                    if (loadingIndicator != null) {
                        loadingIndicator.setVisible(false);
                    }
                });
            });

            // Start the geocoding task
            new Thread(geocodingTask).start();

        } catch (SQLException e) {
            System.err.println("Error loading lampadaires: " + e.getMessage());
            e.printStackTrace();
            if (loadingIndicator != null) {
                loadingIndicator.setVisible(false);
            }
        }
    }

    /**
     * Get coordinates for a location, either from existing coordinates or by geocoding a place name
     */
    private Coordinate getCoordinatesForLocation(String location) {
        if (location == null || location.isEmpty()) {
            return null;
        }

        // First check if we already have this location cached
        if (locationCache.containsKey(location)) {
            return locationCache.get(location);
        }

        // Try to extract coordinates if the location already contains them
        double[] coords = extractCoordinates(location);
        if (coords != null) {
            Coordinate coordinate = new Coordinate(coords[0], coords[1]);
            locationCache.put(location, coordinate);
            return coordinate;
        }

        // If no coordinates found, try to geocode the location name
        try {
            // Add Tunisia to improve geocoding accuracy
            String searchQuery = location;
            if (!searchQuery.toLowerCase().contains("tunisia") &&
                    !searchQuery.toLowerCase().contains("tunisie")) {
                searchQuery += ", Tunisia";
            }

            // Encode the query for URL
            String encodedQuery = URLEncoder.encode(searchQuery, StandardCharsets.UTF_8.toString());

            // Build the complete URL with Tunisia viewbox to improve accuracy
            String urlString = NOMINATIM_API + encodedQuery + COUNTRY_CODE + TUNISIA_VIEWBOX;

            // Create and configure the connection
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "SmartCity-Lampadaire-Management");

            // Get the response
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Parse the JSON response
                JSONArray jsonArray = new JSONArray(response.toString());
                if (jsonArray.length() > 0) {
                    JSONObject result = jsonArray.getJSONObject(0);
                    double lat = result.getDouble("lat");
                    double lon = result.getDouble("lon");

                    System.out.println("Geocoded " + location + " to: " + lat + "," + lon);

                    Coordinate coordinate = new Coordinate(lat, lon);
                    locationCache.put(location, coordinate);
                    return coordinate;
                }
            } else {
                System.err.println("Geocoding request failed with response code: " + responseCode);
            }
        } catch (Exception e) {
            System.err.println("Error geocoding location '" + location + "': " + e.getMessage());
        }

        return null;
    }

    private void updateLampButtons(List<Lampadaire> lampadaires) {
        // Clear existing content
        markerDetailsContainer.getChildren().clear();

        // Add label
        Label headerLabel = new Label("Streetlights in this district:");
        headerLabel.getStyleClass().add("sub-title");
        markerDetailsContainer.getChildren().add(headerLabel);

        // Add a button for each lamp that will show its details
        for (Lampadaire lamp : lampadaires) {
            String buttonText = "ID: " + lamp.getId() + " (" + (lamp.isEtat() ? "Active" : "Inactive") + ")";
            Button lampBtn = new Button(buttonText);

            // Apply CSS classes based on lamp state
            lampBtn.getStyleClass().add("button-light");
            if (lamp.isEtat()) {
                lampBtn.getStyleClass().add("status-active");
            } else {
                lampBtn.getStyleClass().add("status-inactive");
            }

            // Set action to show details
            lampBtn.setOnAction(e -> showLampadaireDetails(lamp));

            // Make buttons fill the width
            lampBtn.setMaxWidth(Double.MAX_VALUE);

            // Add to container
            markerDetailsContainer.getChildren().add(lampBtn);
        }
    }

    private void showLampadaireDetails(Lampadaire lamp) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Streetlight Details");
        alert.setHeaderText("ID: " + lamp.getId());

        // Convert coordinates to a readable format if possible
        String locationStr = lamp.getLocalisation();
        double[] coords = extractCoordinates(locationStr);
        if (coords != null) {
            // Format as readable coordinates
            locationStr = String.format("Latitude: %.6f, Longitude: %.6f", coords[0], coords[1]);
        }

        String content = String.format(
                "Location: %s\nStatus: %s\nConsumption: %.2f W\nInstallation Date: %s",
                locationStr,
                lamp.isEtat() ? "Active" : "Inactive",
                lamp.getConsommation(),
                lamp.getDate_installation().toString()
        );

        alert.setContentText(content);

        // Apply CSS to the alert dialog
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
                getClass().getResource("/com/example/demo/styles/modern-purple.css").toExternalForm()
        );
        dialogPane.getStyleClass().add("custom-alert");

        alert.showAndWait();

        logActivity("Viewed details for lamp ID: " + lamp.getId());
    }

    private void logActivity(String action) {
        String currentDateTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println("[" + currentDateTime + "] " + action);
    }

    private double[] extractCoordinates(String location) {
        if (location == null || location.isEmpty()) {
            return null;
        }

        try {
            // Check if the location contains comma-separated coordinates
            if (location.contains(",")) {
                String[] parts = location.split(",");
                if (parts.length >= 2) {
                    // Try to parse as numbers
                    String latStr = parts[0].trim();
                    String lngStr = parts[1].trim();

                    // Check if these look like valid coordinates
                    if (latStr.matches("-?\\d+(\\.\\d+)?") && lngStr.matches("-?\\d+(\\.\\d+)?")) {
                        double lat = Double.parseDouble(latStr);
                        double lng = Double.parseDouble(lngStr);

                        // Basic validation
                        if (lat >= -90 && lat <= 90 && lng >= -180 && lng <= 180) {
                            return new double[] {lat, lng};
                        }
                    }
                }
            }
        } catch (NumberFormatException e) {
            System.err.println("Error parsing coordinates from: " + location);
        }

        return null;
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) mapView.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleRefresh() {
        if (quartier != null) {
            loadLampadaires();
        }
    }
}