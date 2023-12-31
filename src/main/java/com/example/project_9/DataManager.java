package com.example.project_9;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataManager {
    // The array is fixed size, so we need to know the capacity
    // It is 1169 entries, but we will use 1200 to be safe
    private static final int ARRAY_CAPACITY = 1200;
    // Radius of the earth in miles
    final double RADIUS_EARTH = 3958.8;
    private final ZipCode[] zipCodeArray = new ZipCode[ARRAY_CAPACITY];
    private int size = 0;

    // Testing some logging features I found on stackoverflow (see below)
    // https://stackoverflow.com/questions/33779127/loggerfactory-getloggerclassname-class-vs-loggerfactory-getloggerthis-getclas
    // https://stackoverflow.com/questions/194765/how-do-i-get-java-logging-output-to-appear-on-a-single-line
    // https://www.geeksforgeeks.org/logger-getlogger-method-in-java-with-examples/
    // 100% admitting I'm still learning this and when this is submitted I'll still be learning how this works but I wanted to know what the Robust Logging "Error" in IntelliJ was about and how to improve
    private static final Logger LOGGER = Logger.getLogger(DataManager.class.getName());


    // This will pull in the data from the file "zipMIcity.txt"
    // The data is in the format of "48001 42.61500 -82.59780 MI Algonac"
    // The data is in the format: zipcode latitude longitude state city
    // The data is separated by spaces
    public void readZipCodeData(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                ZipCode zipCode = parseZipCode(line);
                zipCodeArray[size++] = zipCode;

                // Debug: Output the data
                // Add a "#" next to each entry for debugging purposes
                for (int i = 0; i < size; i++) {
                    System.out.println("#" + (i + 1) + " - " + zipCodeArray[i].getCode() + " - " +
                            zipCodeArray[i].getLatitude() + ", " + zipCodeArray[i].getLongitude() + " - " +
                            zipCodeArray[i].getState() + " - " + zipCodeArray[i].getCity());
                }
            }
            System.out.println("File found and read successfully: " + filePath);
        } catch (IOException e) {
            System.err.println("Error reading file: " + filePath);
            // Enhanced logging for IOException
            zipCodeDataLogging(e);
        }
    }

    // See logging comment
    private void zipCodeDataLogging(IOException e) {
        LOGGER.log(Level.SEVERE, "IOException Details:", e);
    }

    // This will search the array for the zip code
    // It will return the ZipCode object if found
    // It will return null if not found
    public ZipCode searchZipCode(String zipCode) {
        for (int i = 0; i < size; i++) {
            if (zipCodeArray[i].getCode().equals(zipCode) ||
                    zipCodeArray[i].getCode().equals(String.format("%05d", Integer.parseInt(zipCode)))) {
                return zipCodeArray[i];
            }
        }
        return null;
    }

    // Calculate the Distance based on your provided code
    public double calculateDistance(double latitude1, double longitude1, double latitude2, double longitude2) {
        // Convert local variables to radians
        double lat1 = Math.toRadians(latitude1);
        double lon1 = Math.toRadians(longitude1);
        double lat2 = Math.toRadians(latitude2);
        double lon2 = Math.toRadians(longitude2);

        // Calculate great circle distance and return
        return RADIUS_EARTH * Math.acos(Math.sin(lat1) * Math.sin(lat2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1));
    }

    // This will parse the line of data and create a ZipCode object
    private ZipCode parseZipCode(String line) {
        String[] parts = line.split(" ");
        String code = parts[0];
        double latitude = Double.parseDouble(parts[1]);
        double longitude = Double.parseDouble(parts[2]);
        String state = parts[3];
        String city = parts[4];
        return new ZipCode(code, latitude, longitude, state, city);
    }
}