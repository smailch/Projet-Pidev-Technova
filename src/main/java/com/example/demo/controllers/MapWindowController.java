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

    @FXML
    public void initialize() {
        webEngine = mapView.getEngine();

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
        // HTML content with Leaflet map and JavaScript logic
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
                #debugPanel {
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
            <div id="debugPanel"></div>
            <script>
                // Global variables for coordinates - can be accessed from Java
                window.selectedLatitude = 0;
                window.selectedLongitude = 0;
                
                var map;
                var marker;
                var debugPanel = document.getElementById('debugPanel');
                
                function updateDebug(message) {
                    debugPanel.innerHTML += message + '<br>';
                    // Keep only the last 5 messages
                    if (debugPanel.innerHTML.split('<br>').length > 5) {
                        var lines = debugPanel.innerHTML.split('<br>');
                        debugPanel.innerHTML = lines.slice(lines.length - 5).join('<br>');
                    }
                }
                
                function initMap() {
                    map = L.map('map').setView([36.8625, 10.1956], 16);
                    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                        attribution: '© OpenStreetMap contributors'
                    }).addTo(map);
                    
                    map.on('click', function(e) {
                        if (marker) {
                            map.removeLayer(marker);
                        }
                        marker = L.marker(e.latlng)
                            .bindPopup(`Lat: ${e.latlng.lat.toFixed(5)}<br>Lng: ${e.latlng.lng.toFixed(5)}`)
                            .addTo(map)
                            .openPopup();
                        
                        // Store coordinates globally for Java to access
                        window.selectedLatitude = e.latlng.lat;
                        window.selectedLongitude = e.latlng.lng;
                        
                        updateDebug("Map clicked: " + e.latlng.lat.toFixed(5) + ", " + e.latlng.lng.toFixed(5));
                        
                        // Try multiple approaches to call Java
                        try {
                            // Method 1: Direct call via window.javaApp
                            if (window.javaApp) {
                                updateDebug("Calling Java using window.javaApp...");
                                window.javaApp.locationSelected(e.latlng.lat, e.latlng.lng);
                                updateDebug("Direct call succeeded");
                            } else {
                                updateDebug("ERROR: javaApp not defined");
                            }
                        } catch (err) {
                            updateDebug("ERROR calling Java: " + err.message);
                            
                            // Method 2: Try global callback if direct call failed
                            try {
                                if (typeof javaCallback === 'function') {
                                    updateDebug("Trying javaCallback function...");
                                    javaCallback(e.latlng.lat, e.latlng.lng);
                                    updateDebug("Callback call succeeded");
                                }
                            } catch (err2) {
                                updateDebug("ERROR using callback: " + err2.message);
                            }
                        }
                    });
                }
                
                // Wait for the page to fully load before setting up the bridge
                window.onload = function() {
                    // Initialize the map
                    initMap();
                    
                    // Test if javaApp is accessible
                    updateDebug("javaApp available: " + (typeof javaApp !== 'undefined'));
                    
                    // Test button for debugging
                    var testButton = document.createElement('button');
                    testButton.innerHTML = 'Test Java Bridge';
                    testButton.style.position = 'absolute';
                    testButton.style.top = '10px';
                    testButton.style.right = '10px';
                    testButton.style.zIndex = '1000';
                    testButton.onclick = function() {
                        try {
                            if (window.javaApp) {
                                window.javaApp.locationSelected(36.8625, 10.1956);
                                updateDebug("Test call successful");
                            } else {
                                updateDebug("ERROR: javaApp not defined");
                            }
                        } catch (e) {
                            updateDebug("Test call failed: " + e.message);
                        }
                    };
                    document.body.appendChild(testButton);
                    
                    // Create a backup "notify Java" button 
                    var notifyButton = document.createElement('button');
                    notifyButton.innerHTML = 'Notify Java';
                    notifyButton.style.position = 'absolute';
                    notifyButton.style.top = '40px';
                    notifyButton.style.right = '10px';
                    notifyButton.style.zIndex = '1000';
                    notifyButton.onclick = function() {
                        // This will set a flag Java can poll for
                        window.locationUpdated = true;
                        updateDebug("Notification flag set");
                    };
                    document.body.appendChild(notifyButton);
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

                    // Set up a global callback function
                    webEngine.executeScript(
                            "var javaCallback = function(lat, lng) { window.javaApp.locationSelected(lat, lng); };"
                    );

                    // Verify that javaApp is set correctly
                    webEngine.executeScript(
                            "console.log('javaApp is set:', typeof javaApp !== 'undefined');" +
                                    "console.log('locationSelected method exists:', typeof javaApp.locationSelected === 'function');"
                    );

                    // Try a test call
                    webEngine.executeScript(
                            "try { " +
                                    "  console.log('Testing Java bridge...');" +
                                    "  if(window.javaApp) { " +
                                    "    window.javaApp.locationSelected(0, 0); " +
                                    "    console.log('Test call successful'); " +
                                    "  } else { " +
                                    "    console.error('javaApp not defined in window'); " +
                                    "  }" +
                                    "} catch(e) { " +
                                    "  console.error('Test call failed:', e.message); " +
                                    "}"
                    );

                    // Set up a polling mechanism as a backup approach
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
                        javafx.util.Duration.seconds(1),
                        event -> checkForLocationUpdates()
                )
        );
        timeline.setCycleCount(javafx.animation.Animation.INDEFINITE);
        timeline.play();
    }

    private void checkForLocationUpdates() {
        try {
            // Check if the locationUpdated flag is set
            JSObject window = (JSObject) webEngine.executeScript("window");
            Object locationUpdated = window.getMember("locationUpdated");

            if (locationUpdated instanceof Boolean && (Boolean) locationUpdated) {
                // Get the latitude and longitude from the global variables
                double lat = (Double) window.getMember("selectedLatitude");
                double lng = (Double) window.getMember("selectedLongitude");

                // Process the location
                System.out.println("Location updated through polling: Lat = " + lat + ", Lng = " + lng);
                locationSelected(lat, lng);

                // Reset the flag
                webEngine.executeScript("window.locationUpdated = false;");
            }
        } catch (Exception e) {
            System.err.println("Error in polling: " + e.getMessage());
        }
    }

    // This method is called from JavaScript when a location is selected
    // Added public keyword to ensure it's accessible from JavaScript
    public void locationSelected(double lat, double lng) {
        System.out.println("Location selected in Java: Lat = " + lat + ", Lng = " + lng);
        this.selectedLat = lat;
        this.selectedLng = lng;

        // Update UI on JavaFX thread
        javafx.application.Platform.runLater(() -> {
            saveButton.setVisible(true);
        });
    }

    @FXML
    private void handleSave() {
        if (mainController != null) {
            // Pass the selected location back to the main controller
            mainController.setLocation(selectedLat, selectedLng);
        }

        // Print the selected latitude and longitude to the terminal
        System.out.println("Saving - Selected Latitude: " + selectedLat);
        System.out.println("Saving - Selected Longitude: " + selectedLng);

        // Close the window
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}