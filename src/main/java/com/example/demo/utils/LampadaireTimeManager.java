package com.example.demo.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * Manages time-based operations for lampadaires including sunrise/sunset calculations
 * with global timezone awareness
 */
public class LampadaireTimeManager {

    /**
     * Get the current time at the specified coordinates
     * @param latitude The latitude
     * @param longitude The longitude
     * @return The current time at that location
     */
    public static ZonedDateTime getCurrentTimeAtLocation(double latitude, double longitude) {
        // Get the timezone for the location
        ZoneId zoneId = getTimezoneForLocation(longitude);

        // Get current time in that timezone
        return ZonedDateTime.now(zoneId);
    }

    /**
     * Determines if it's night time at the specified coordinates
     * @param latitude The latitude
     * @param longitude The longitude
     * @return true if it's night time, false otherwise
     */
    public static boolean isNightTime(double latitude, double longitude) {
        // Get timezone for location
        ZoneId zoneId = getTimezoneForLocation(longitude);

        // Get current time in that timezone
        ZonedDateTime now = ZonedDateTime.now(zoneId);

        // Calculate sunrise and sunset times
        ZonedDateTime sunrise = calculateSunrise(latitude, longitude, now.toLocalDate());
        ZonedDateTime sunset = calculateSunset(latitude, longitude, now.toLocalDate());

        // It's night if the current time is before sunrise or after sunset
        return now.isBefore(sunrise) || now.isAfter(sunset);
    }

    /**
     * Calculate approximate sunrise time for the given coordinates and date
     */
    public static ZonedDateTime calculateSunrise(double latitude, double longitude, LocalDate date) {
        // Get timezone for location
        ZoneId zoneId = getTimezoneForLocation(longitude);

        // Get day of year
        int dayOfYear = date.getDayOfYear();

        // Calculate solar declination based on day of year
        double solarDeclination = 23.45 * Math.sin(Math.toRadians((360.0/365.0) * (dayOfYear - 81)));

        // Calculate sunrise equation parts
        double cosHourAngle = -Math.tan(Math.toRadians(latitude)) *
                Math.tan(Math.toRadians(solarDeclination));

        // Ensure value is in valid range for arccos
        cosHourAngle = Math.max(-1.0, Math.min(1.0, cosHourAngle));

        // Convert to hour angle in radians
        double hourAngle = Math.acos(cosHourAngle);

        // Convert to hours
        double sunriseHours = 12.0 - hourAngle * 180.0 / (15.0 * Math.PI);

        // Adjust for longitude within the timezone
        // Each timezone is roughly 15 degrees wide, centered on a multiple of 15
        // Find the center longitude of the timezone
        int timeZoneHour = (int) Math.round(longitude / 15.0);
        double centerLongitude = timeZoneHour * 15.0;

        // Adjust for the difference between the actual longitude and the timezone center
        // Each degree of longitude is about 4 minutes of time
        double longitudeAdjustment = (longitude - centerLongitude) * 4.0 / 60.0;

        // Apply adjustment
        sunriseHours -= longitudeAdjustment;

        // Normalize to 0-24 range
        sunriseHours = (sunriseHours + 24.0) % 24.0;

        // Convert to hour and minute
        int hour = (int) sunriseHours;
        int minute = (int) ((sunriseHours - hour) * 60);

        // Create the ZonedDateTime for sunrise
        return ZonedDateTime.of(date, LocalTime.of(hour, minute), zoneId);
    }

    /**
     * Calculate approximate sunset time for the given coordinates and date
     */
    public static ZonedDateTime calculateSunset(double latitude, double longitude, LocalDate date) {
        // Get timezone for location
        ZoneId zoneId = getTimezoneForLocation(longitude);

        // Get day of year
        int dayOfYear = date.getDayOfYear();

        // Calculate solar declination based on day of year
        double solarDeclination = 23.45 * Math.sin(Math.toRadians((360.0/365.0) * (dayOfYear - 81)));

        // Calculate sunset equation parts
        double cosHourAngle = -Math.tan(Math.toRadians(latitude)) *
                Math.tan(Math.toRadians(solarDeclination));

        // Ensure value is in valid range for arccos
        cosHourAngle = Math.max(-1.0, Math.min(1.0, cosHourAngle));

        // Convert to hour angle in radians
        double hourAngle = Math.acos(cosHourAngle);

        // Convert to hours (sunset uses positive hour angle)
        double sunsetHours = 12.0 + hourAngle * 180.0 / (15.0 * Math.PI);

        // Adjust for longitude within the timezone
        // Each timezone is roughly 15 degrees wide, centered on a multiple of 15
        // Find the center longitude of the timezone
        int timeZoneHour = (int) Math.round(longitude / 15.0);
        double centerLongitude = timeZoneHour * 15.0;

        // Adjust for the difference between the actual longitude and the timezone center
        // Each degree of longitude is about 4 minutes of time
        double longitudeAdjustment = (longitude - centerLongitude) * 4.0 / 60.0;

        // Apply adjustment
        sunsetHours -= longitudeAdjustment;

        // Normalize to 0-24 range
        sunsetHours = (sunsetHours + 24.0) % 24.0;

        // Convert to hour and minute
        int hour = (int) sunsetHours;
        int minute = (int) ((sunsetHours - hour) * 60);

        // Create the ZonedDateTime for sunset
        return ZonedDateTime.of(date, LocalTime.of(hour, minute), zoneId);
    }

    /**
     * Get a timezone approximation based on longitude
     * @param longitude The longitude
     * @return The approximate ZoneId for that longitude
     */
    public static ZoneId getTimezoneForLocation(double longitude) {
        // Calculate approximate timezone offset in hours
        // Each 15 degrees of longitude corresponds to 1 hour
        int offsetHours = (int) Math.round(longitude / 15.0);

        // Create a ZoneOffset
        ZoneOffset offset = ZoneOffset.ofHours(offsetHours);

        // Return as ZoneId
        return offset;
    }

    /**
     * Get a user-friendly description of sunrise/sunset times for a location
     */
    public static String getTimeDescription(double latitude, double longitude, String locationName) {
        // Get the timezone for the location
        ZoneId zoneId = getTimezoneForLocation(longitude);

        // Get current date and time at that location
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        LocalDate today = now.toLocalDate();

        // Calculate sunrise and sunset
        ZonedDateTime sunrise = calculateSunrise(latitude, longitude, today);
        ZonedDateTime sunset = calculateSunset(latitude, longitude, today);

        // Check if it's currently night time
        boolean isNight = now.isBefore(sunrise) || now.isAfter(sunset);

        // Format for display
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        return String.format(
                "Location: %s (%.4f, %.4f) | Timezone: UTC%+d | " +
                        "Current time: %s | Sunrise: %s | Sunset: %s | " +
                        "Status: %s",
                locationName, latitude, longitude, zoneId.getRules().getOffset(now.toInstant()).getTotalSeconds() / 3600,
                now.format(timeFormatter),
                sunrise.format(timeFormatter),
                sunset.format(timeFormatter),
                isNight ? "Night time (lamp ON)" : "Day time (lamp OFF)"
        );
    }
}