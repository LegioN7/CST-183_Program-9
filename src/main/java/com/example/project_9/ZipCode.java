package com.example.project_9;

// Class for the Zipcode object
public class ZipCode {
    private final String code;
    private final double latitude;
    private final double longitude;
    private final String state;
    private final String city;

    // Constructor for the ZipCode object
    // Example: "48001 42.61500 -82.59780 MI Algonac"
    // The data is in the format: zipcode latitude longitude state city

    public ZipCode(String code, double latitude, double longitude, String state, String city) {
        this.code = code;
        this.latitude = latitude;
        this.longitude = longitude;
        this.state = state;
        this.city = city;
    }

    // Getters for the ZipCode object
    public String getCode() {
        return code;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getState() {
        return state;
    }

    public String getCity() {
        return city;
    }
}