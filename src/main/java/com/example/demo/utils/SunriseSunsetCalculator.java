package com.example.demo.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;

/**
 * Simple calculator for sunrise and sunset times based on geographical coordinates
 */
public class SunriseSunsetCalculator {

    /**
     * Calculate sunrise time for a specific location and date
     * @param latitude Location latitude
     * @param longitude Location longitude
     * @param date The date for calculation
     * @return The approximate sunrise time
     */
    public static LocalTime calculateSunrise(double latitude, double longitude, LocalDate date) {
        // This is a simplified calculation based on the day of year
        int dayOfYear = date.getDayOfYear();

        // Calculate solar declination for the day
        double declination = 23.45 * Math.sin(Math.toRadians((360.0 / 365.0) * (dayOfYear - 81)));

        // Calculate sunrise hour angle
        double hourAngle = Math.acos(-Math.tan(Math.toRadians(latitude)) *
                Math.tan(Math.toRadians(declination)));

        // Convert to hours
        double sunriseHours = 12.0 - (hourAngle * 12.0 / Math.PI) - (longitude / 15.0);

        // Convert to LocalTime (ensuring hours are within 0-24 range)
        sunriseHours = (sunriseHours + 24.0) % 24.0;
        int hours = (int) sunriseHours;
        int minutes = (int) ((sunriseHours - hours) * 60);

        return LocalTime.of(hours, minutes);
    }

    /**
     * Calculate sunset time for a specific location and date
     * @param latitude Location latitude
     * @param longitude Location longitude
     * @param date The date for calculation
     * @return The approximate sunset time
     */
    public static LocalTime calculateSunset(double latitude, double longitude, LocalDate date) {
        // This is a simplified calculation based on the day of year
        int dayOfYear = date.getDayOfYear();

        // Calculate solar declination for the day
        double declination = 23.45 * Math.sin(Math.toRadians((360.0 / 365.0) * (dayOfYear - 81)));

        // Calculate sunset hour angle
        double hourAngle = Math.acos(-Math.tan(Math.toRadians(latitude)) *
                Math.tan(Math.toRadians(declination)));

        // Convert to hours
        double sunsetHours = 12.0 + (hourAngle * 12.0 / Math.PI) - (longitude / 15.0);

        // Convert to LocalTime (ensuring hours are within 0-24 range)
        sunsetHours = (sunsetHours + 24.0) % 24.0;
        int hours = (int) sunsetHours;
        int minutes = (int) ((sunsetHours - hours) * 60);

        return LocalTime.of(hours, minutes);
    }

    /**
     * Determines if it is currently nighttime at the given coordinates
     * @param latitude Location latitude
     * @param longitude Location longitude
     * @return true if it's currently night at the location
     */
    public static boolean isNightTime(double latitude, double longitude) {
        // Get current UTC time
        LocalDateTime utcNow = LocalDateTime.now(ZoneOffset.UTC);

        // Calculate local time based on longitude
        double timeZoneOffset = longitude / 15.0;
        LocalDateTime localDateTime = utcNow.plusHours((long) Math.round(timeZoneOffset));

        // Get the local date and time
        LocalDate localDate = localDateTime.toLocalDate();
        LocalTime localTime = localDateTime.toLocalTime();

        // Calculate sunrise and sunset for today
        LocalTime sunrise = calculateSunrise(latitude, longitude, localDate);
        LocalTime sunset = calculateSunset(latitude, longitude, localDate);

        // It's night if the current time is before sunrise or after sunset
        return localTime.isBefore(sunrise) || localTime.isAfter(sunset) || localTime.equals(sunset);
    }
}