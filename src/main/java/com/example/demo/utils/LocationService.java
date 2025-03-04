package com.example.demo.utils;

import com.dlsc.gmapsfx.service.geocoding.GeocoderRequest;
import com.dlsc.gmapsfx.service.geocoding.GeocoderStatus;
import com.dlsc.gmapsfx.service.geocoding.GeocodingResult;
import com.dlsc.gmapsfx.service.geocoding.GeocodingService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Service to handle location operations like geocoding globally
 */
public class LocationService {

    private final Map<String, double[]> locationCache = new HashMap<>();
    private GeocodingService geocodingService;

    public LocationService() {
        // Initialize the cache with common global locations
        initializeCache();

        // Initialize the geocoding service
        try {
            geocodingService = new GeocodingService();
        } catch (Exception e) {
            System.err.println("Failed to initialize geocoding service: " + e.getMessage());
        }
    }

    /**
     * Initialize cache with common global locations
     */
    private void initializeCache() {
        // Tunisia
        locationCache.put("tunis", new double[]{36.8065, 10.1815});
        locationCache.put("sfax", new double[]{34.7406, 10.7603});
        locationCache.put("sousse", new double[]{35.8283, 10.6408});

        // Japan
        locationCache.put("tokyo", new double[]{35.6762, 139.6503});
        locationCache.put("osaka", new double[]{34.6937, 135.5022});
        locationCache.put("kyoto", new double[]{35.0116, 135.7681});
        locationCache.put("nagano", new double[]{36.6485, 138.1950}); // Added Nagano

        // USA
        locationCache.put("new york", new double[]{40.7128, -74.0060});
        locationCache.put("los angeles", new double[]{34.0522, -118.2437});
        locationCache.put("chicago", new double[]{41.8781, -87.6298});

        // Europe
        locationCache.put("paris", new double[]{48.8566, 2.3522});
        locationCache.put("london", new double[]{51.5074, -0.1278});
        locationCache.put("berlin", new double[]{52.5200, 13.4050});

        // Asia
        locationCache.put("beijing", new double[]{39.9042, 116.4074});
        locationCache.put("delhi", new double[]{28.6139, 77.2090});
        locationCache.put("singapore", new double[]{1.3521, 103.8198});

        // Australia
        locationCache.put("sydney", new double[]{-33.8688, 151.2093});
        locationCache.put("melbourne", new double[]{-37.8136, 144.9631});

        // South America
        locationCache.put("rio de janeiro", new double[]{-22.9068, -43.1729});
        locationCache.put("buenos aires", new double[]{-34.6037, -58.3816});

        // Africa
        locationCache.put("cairo", new double[]{30.0444, 31.2357});
        locationCache.put("cape town", new double[]{-33.9249, 18.4241});

        // Add more global cities as needed...
    }

    /**
     * Geocode a location name to coordinates
     * @param locationName The name of the location to geocode
     * @return An array of [latitude, longitude] or null if geocoding failed
     */
    public double[] geocodeLocation(String locationName) throws Exception {
        if (locationName == null || locationName.trim().isEmpty()) {
            return null;
        }

        // Try cache first
        String normalized = locationName.toLowerCase().trim();
        if (locationCache.containsKey(normalized)) {
            System.out.println("Found in cache: " + normalized);
            return locationCache.get(normalized);
        }

        // If no geocoding service, use the fallback method
        if (geocodingService == null) {
            return geocodeFallback(normalized);
        }

        // Use GMapsFX geocoding - without restricting to Tunisia
        // Use GMapsFX geocoding
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<double[]> result = new AtomicReference<>();

// Execute the geocoding request directly with the address string
        String addressWithContext = locationName + ", Tunisia"; // Add Tunisia for context
        geocodingService.geocode(addressWithContext, (results, status) -> {
            if (status == GeocoderStatus.OK && results.length > 0) {
                GeocodingResult firstResult = results[0];
                double lat = firstResult.getGeometry().getLocation().getLatitude();
                double lng = firstResult.getGeometry().getLocation().getLongitude();
                result.set(new double[]{lat, lng});
            }
            latch.countDown();
        });

        // Wait for the result (with timeout)
        if (latch.await(5, TimeUnit.SECONDS)) {
            double[] coordinates = result.get();
            if (coordinates != null) {
                // Cache the result for future use
                locationCache.put(normalized, coordinates);
                return coordinates;
            }
        }

        // If geocoding failed, use fallback
        return geocodeFallback(normalized);
    }

    /**
     * Fallback geocoding method for when the service is unavailable
     */
    private double[] geocodeFallback(String normalizedName) {
        // Try partial matching with cache
        for (Map.Entry<String, double[]> entry : locationCache.entrySet()) {
            if (normalizedName.contains(entry.getKey()) || entry.getKey().contains(normalizedName)) {
                System.out.println("Found partial match in cache: " + normalizedName + " -> " + entry.getKey());
                return entry.getValue();
            }
        }

        System.out.println("Using default coordinates for: " + normalizedName);
        return null; // Return null instead of default coordinates to indicate failure
    }
}