package com.example.demo.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

public class MapWindowController {

    @FXML private WebView mapView;
    @FXML private Button saveButton;

    private WebEngine webEngine;
    private AddLampadaireController mainController;
    private double selectedLat;
    private double selectedLng;
    private String selectedPlaceName = "";

    @FXML
    public void initialize() {
        webEngine = mapView.getEngine();
        saveButton.setDisable(true); // Disable save button until location is selected

        // Set up error and exception handlers for debugging
        webEngine.setOnError(event -> System.err.println("WebEngine Error: " + event.getMessage()));
        webEngine.setOnAlert(event -> System.out.println("WebEngine Alert: " + event.getData()));
        webEngine.getLoadWorker().exceptionProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                System.err.println("WebEngine Exception: " + newValue.getMessage());
                newValue.printStackTrace();
            }
        });

        // Load the map
        loadMap();
    }

    public void setMainController(AddLampadaireController mainController) {
        this.mainController = mainController;
    }

    private void loadMap() {
        // HTML content with Leaflet map and JavaScript for reverse geocoding
        String htmlContent = """
        <!DOCTYPE html>
        <html>
        <head>
            <title>OpenStreetMap</title>
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"/>
            <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
            <style>
                html, body {
                    margin: 0;
                    padding: 0;
                    height: 100%;
                    width: 100%;
                }
                #map {
                    height: 100%;
                    width: 100%;
                }
                #statusPanel {
                    position: absolute;
                    bottom: 10px;
                    left: 10px;
                    background: white;
                    padding: 5px;
                    border: 1px solid #ccc;
                    z-index: 1000;
                    max-width: 300px;
                }
            </style>
        </head>
        <body>
            <div id="map"></div>
            <div id="statusPanel">Click on the map to select a location</div>
            <script>
                // Global variables for coordinates - can be accessed from Java
                window.selectedLatitude = 0;
                window.selectedLongitude = 0;
                window.selectedPlaceName = "";
                window.locationSelected = false;
                
                var map;
                var marker;
                var statusPanel = document.getElementById('statusPanel');
                
                function updateStatus(message) {
                    statusPanel.innerHTML = message;
                }
                
                function initMap() {
                    map = L.map('map').setView([36.8625, 10.1956], 13);
                    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                        attribution: '© OpenStreetMap contributors'
                    }).addTo(map);
                    
                    map.on('click', function(e) {
                        if (marker) {
                            map.removeLayer(marker);
                        }
                        
                        var lat = e.latlng.lat;
                        var lng = e.latlng.lng;
                        
                        // Store coordinates globally for Java to access
                        window.selectedLatitude = lat;
                        window.selectedLongitude = lng;
                        
                        updateStatus("Getting location name...");
                        
                        // Create a marker at the clicked position
                        marker = L.marker(e.latlng).addTo(map);
                        
                        // Use Nominatim for reverse geocoding
                        fetch(`https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lng}&zoom=18&addressdetails=1`)
                            .then(response => response.json())
                            .then(data => {
                                // Extract a meaningful location name
                                var placeName = "";
                                
                                if (data.address) {
                                    // Try to create a meaningful description
                                    var parts = [];
                                    
                                    // Add road/street name if available
                                    if (data.address.road) {
                                        parts.push(data.address.road);
                                    } else if (data.address.pedestrian) {
                                        parts.push(data.address.pedestrian);
                                    }
                                    
                                    // Add neighborhood/suburb if available
                                    if (data.address.suburb) {
                                        parts.push(data.address.suburb);
                                    } else if (data.address.neighbourhood) {
                                        parts.push(data.address.neighbourhood);
                                    }
                                    
                                    // Add city/town
                                    if (data.address.city) {
                                        parts.push(data.address.city);
                                    } else if (data.address.town) {
                                        parts.push(data.address.town);
                                    }
                                    
                                    placeName = parts.join(", ");
                                    
                                    // If we couldn't build a good name, use the display_name
                                    if (!placeName) {
                                        placeName = data.display_name;
                                    }
                                } else {
                                    // Fallback if no address data
                                    placeName = `Location (${lat.toFixed(5)}, ${lng.toFixed(5)})`;
                                }
                                
                                window.selectedPlaceName = placeName;
                                marker.bindPopup(placeName).openPopup();
                                
                                updateStatus("Location selected: " + placeName);
                                
                                // Set flag for Java to know location is selected
                                window.locationSelected = true;
                                
                                // Try to call Java directly
                                try {
                                    if (window.javaApp) {
                                        window.javaApp.locationSelectedWithName(lat, lng, placeName);
                                    }
                                } catch (err) {
                                    console.error("Error calling Java:", err);
                                }
                            })
                            .catch(error => {
                                console.error("Geocoding error:", error);
                                window.selectedPlaceName = `Location (${lat.toFixed(5)}, ${lng.toFixed(5)})`;
                                marker.bindPopup(window.selectedPlaceName).openPopup();
                                updateStatus("Error getting location name. Using coordinates.");
                                
                                // Still set the flag and try to call Java
                                window.locationSelected = true;
                                try {
                                    if (window.javaApp) {
                                        window.javaApp.locationSelectedWithName(lat, lng, window.selectedPlaceName);
                                    }
                                } catch (err) {
                                    console.error("Error calling Java:", err);
                                }
                            });
                    });
                }
                
                // Initialize when the page loads
                window.onload = function() {
                    initMap();
                };
            </script>
        </body>
        </html>
        """;

        // Load the HTML content into the WebView
        webEngine.loadContent(htmlContent);

        // Wait for the page to load before setting up the bridge
        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == javafx.concurrent.Worker.State.SUCCEEDED) {
                try {
                    System.out.println("WebView loaded successfully");

                    // Set up the Java-to-JavaScript bridge
                    JSObject window = (JSObject) webEngine.executeScript("window");
                    window.setMember("javaApp", this);

                    // Set up a polling mechanism to check for location selection
                    setupPollingMechanism();

                } catch (Exception e) {
                    System.err.println("Error setting up JavaScript bridge: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    private void setupPollingMechanism() {
        // Create a polling timer that checks for location updates
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(
                        javafx.util.Duration.seconds(0.5),
                        event -> checkForLocationUpdates()
                )
        );
        timeline.setCycleCount(javafx.animation.Animation.INDEFINITE);
        timeline.play();
    }

    private void checkForLocationUpdates() {
        try {
            // Check if the locationSelected flag is set
            JSObject window = (JSObject) webEngine.executeScript("window");
            Object locationSelected = window.getMember("locationSelected");

            if (locationSelected instanceof Boolean && (Boolean) locationSelected) {
                // Get the latitude, longitude, and place name from the global variables
                double lat = (Double) window.getMember("selectedLatitude");
                double lng = (Double) window.getMember("selectedLongitude");
                String placeName = (String) window.getMember("selectedPlaceName");

                // Process the location
                System.out.println("Location updated through polling: " + placeName);
                locationSelectedWithName(lat, lng, placeName);

                // Reset the flag
                webEngine.executeScript("window.locationSelected = false;");
            }
        } catch (Exception e) {
            System.err.println("Error in polling: " + e.getMessage());
        }
    }

    // Method for JavaScript to call with coordinates and place name
    public void locationSelectedWithName(double lat, double lng, String placeName) {
        System.out.println("Location selected in Java: " + placeName);
        this.selectedLat = lat;
        this.selectedLng = lng;
        this.selectedPlaceName = placeName;

        // Update UI on JavaFX thread
        javafx.application.Platform.runLater(() -> {
            saveButton.setDisable(false);
        });
    }

    @FXML
    private void handleSave() {
        if (mainController != null) {
            // Pass the selected location name back to the main controller
            mainController.setLocation(selectedLat, selectedLng, selectedPlaceName);
        }

        // Print the selected information to the terminal
        System.out.println("Saving - Selected Location: " + selectedPlaceName);
        System.out.println("Saving - Coordinates: " + selectedLat + ", " + selectedLng);

        // Close the window
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}