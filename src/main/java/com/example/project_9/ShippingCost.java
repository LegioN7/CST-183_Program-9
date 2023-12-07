// Aaron Pelto
// CST-183
// Fall 2023

// Project 9 Requirements
/*
Write a Java application to simulate shipping tool for Michigan zip codes.
Your company maintains several shipping centers around Michigan.

You need to build an application to calculate the shipping cost between one of the shipping centers and any other community in the
state. Your shipping centers are in:
    ● University Center (48710)
    ● Mackinaw_City (49701)
    ● Grand Rapids (49501)
    ● Marquette (49855)
    ● Traverse_City (49684)


From post offices at these locations, your company can ship to any other post office in Michigan
(except of course itself).

***************************************************************************************************************************************************************
Build a JavaFX user interface that allows the user to enter a shipping center and a zip code for the
product destination.

Then, calculate and provide the shipping cost for the order.
Create a drop-down list that includes the small list of shipping center cities.
include an empty default choice in the drop-down list for an initial setting.

Next, build a numerical key pad as an interface to key in the digits of the zip code.

Include a (non-editable) text field to display the digits of the zip code as they are keyed in.

Finally, include buttons to Calculate, Clear, and Quit.

The Calculate button should determine the shipping cost and the Clear button should empty the zip code text field and reset the
drop-down list to the empty "non-choice".


Upon entering a valid zip code, the user should receive a dialog box containing the post office name,
the distance from the shipping source (see below), and the cost to ship.

For simplicity, assume it requires only 6 cents per mile to ship a product.


For error checking, be sure that the zip code entered (1) has five digits, (2) exists in Michigan, and (3)
does not match the selected shipping center.

A simple message dialog can be used to display an error the user.


Your application requires a file zipMIcity.txt that contains all zip codes in Michigan including the
location (latitude,longitude) and name of the post office. A sample line of input from the file will be:
48706 43.60880 -83.95300 MI Bay_City
(note: Western Hemisphere longitudes are negative).


Your application should be object-oriented containing:
● The main application JavaFX GUI
● Include one class to store information for one zip code
● Include another class that will act as a "data manager" for the list of zip code info objects


The data manager class should perform the following tasks "behind the scenes":
● Read the raw zip code information from the provided data files.
● Store the information in one or more arrays within your class(es).
● Search for the name, latitude, and longitude for the zip code entered.
● Perform required distance calculations.
● Combine zip code and county information as needed for given user input.
● Return the required information to the main application for display.


 */
package com.example.project_9;

// Imports
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.Objects;

public class ShippingCost extends Application {

    // The cost per mile to ship a product is 6 cents.
    // Final as this is not going to change
    private final double COST_PER_MILE = 0.06;
    private DataManager dataManager;

    
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        dataManager = new DataManager();

        // Read the file and store the data
        dataManager.readZipCodeData("zipMIcity.txt");

        // I need to create a stackpane to hold the gridpane
        StackPane root = new StackPane();
        Scene scene = new Scene(root, 400, 320);

        // Title - Shipping Cost Estimator
        primaryStage.setTitle("Shipping Cost Estimator");

        // This is the gridpane that will hold all the elements
        GridPane gridPane = createGridPane();
        root.getChildren().add(gridPane);

        // Import the Style Sheet
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());

        
        primaryStage.setScene(scene);
        
        primaryStage.show();
    }

    // This is the number pad used to input the zip code.
    // I used some elements across the internet and mixed them together
    // References
    // Using an Array to create a number pad
    // http://www.java2s.com/Tutorials/Java/JavaFX_How_to/GridPane/Layout_number_pad_with_GridPane.htm
    // https://stackoverflow.com/questions/42201650/creating-virtual-number-pad-in-java-fx

    private static GridPane createNumberPad(TextField numPadField) {
        String[] numberPad = {
                "1", "2", "3",
                "4", "5", "6",
                "7", "8", "9",
                "", "0", ""
        };

        // Gridpane to center the numpad
        // This didn't work but I don't really want to test it again
        GridPane numPad = new GridPane();
        numPad.setAlignment(Pos.CENTER);
        numPad.setHgap(5);
        numPad.setVgap(5);

        // This is the textfield that will hold the zip code
        // It is not editable and will be updated by the number pad
        numPadField.setPrefColumnCount(5);
        numPadField.setEditable(false);
        GridPane.setConstraints(numPadField, 0, 0, 3, 1);
        
        numPad.getChildren().add(numPadField);
        
        for (int i = 0; i < numberPad.length; i++) {
            String digit = numberPad[i];
            Button numPadButton = new Button(digit);
            // I had to place the style here because it was being overridden by the stylesheet
            numPadButton.getStyleClass().add("number-pad-button");
            numPadButton.setOnAction(e -> appendToNumPadField(numPadField, digit));
            GridPane.setConstraints(numPadButton, i % 3, i / 3 + 1);
            numPad.getChildren().add(numPadButton);
        }

        return numPad;
    }

    // Using this to ensure the length is 5 digits
    private static void appendToNumPadField(TextField displayField, String digit) {
        if (displayField.getText().length() < 5) {
            displayField.appendText(digit);
        }
    }

    // This gridpane will house all the elements of the application
    // This includes the labels, text field, combo box and buttons
    private GridPane createGridPane() {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 20, 20, 20));

        // Importing the combo box and text field
        ComboBox<String> shippingComboBox = createShippingCenterComboBox();
        gridPane.add(new Label("Select your Shipping Center:"), 0, 0);
        gridPane.add(shippingComboBox, 1, 0);
        
        TextField zipCodeField = createZipCodeField();
        gridPane.add(new Label("Enter Zip Code:"), 0, 1);
        gridPane.add(zipCodeField, 1, 1);

        // Importing the number field
        GridPane numPad = createNumberPad(zipCodeField);
        gridPane.add(numPad, 0, 2, 2, 1);

        // Creating the buttons (I should have created a method for this)
        Button calculateButton = new Button("Calculate");
        Button clearButton = new Button("Clear");
        Button quitButton = new Button("Quit");

        // Stylesheet for the buttons
        calculateButton.getStyleClass().add("calculate-button");
        clearButton.getStyleClass().add("clear-button");
        quitButton.getStyleClass().add("quit-button");

        // Lambda expressions for the buttons
        calculateButton.setOnAction(e -> calculateShippingCost(shippingComboBox, zipCodeField));
        clearButton.setOnAction(e -> clearFields(shippingComboBox, zipCodeField));
        quitButton.setOnAction(e -> System.exit(0));

        // hbox to store the buttons horizontally
        // align center to the gridPane
        HBox buttonBox = new HBox(10, calculateButton, clearButton, quitButton);
        buttonBox.setAlignment(Pos.CENTER);
        gridPane.add(buttonBox, 0, 3, 2, 1);

        return gridPane;
    }


    // Creating the ComboBox for the shipping centers
    // I used two examples
    // https://www.geeksforgeeks.org/javafx-combobox-with-examples/
    // https://jenkov.com/tutorials/javafx/combobox.html
    // The string array approach was working

    private ComboBox<String> createShippingCenterComboBox() {
        String[] shippingCenters = {
                "University Center 48710",
                "Mackinaw City 49701",
                "Grand Rapids 49501",
                "Marquette 49855",
                "Traverse City 49684"
        };

        ComboBox<String> comboBox = new ComboBox<>();
        // Adding the blank entry per requirement
        comboBox.getItems().add("");
        comboBox.getItems().addAll(shippingCenters);

        return comboBox;
    }

    private void calculateShippingCost(ComboBox<String> shippingComboBox, TextField zipCodeField) {
        // Validate the Shipping Center
        if (!isShippingCenterSelected(shippingComboBox)) {
            return;
        }

        // Validate the Zip Code Field
        String zipCode = zipCodeField.getText();
        if (!isValidZipCode(zipCode, shippingComboBox)) {
            return;
        }

        // Get the data required to calculate the shipping cost
        ZipCode selectedShippingCenter = getSelectedShippingCenter(shippingComboBox);
        ZipCode enteredZipCode = dataManager.searchZipCode(zipCode);

        // Silly Null Checker
        // I could move this check to my validation code but I ran into a few issues moving it back and forth
        if (selectedShippingCenter != null && enteredZipCode != null) {
            double distance = dataManager.calculateDistance(
                    selectedShippingCenter.getLatitude(),
                    selectedShippingCenter.getLongitude(),
                    enteredZipCode.getLatitude(),
                    enteredZipCode.getLongitude());

            // Using a method to output the message
            // To note: I used the string method I usually do but IntelliJ created a method for me
            String outputMessage = getString(distance, selectedShippingCenter, enteredZipCode);

            // Displaying the message with Alerts per requirements
            showAlert("Shipping Cost Estimate", outputMessage);
        } else {
            showAlert("Invalid Zip Code", "Zip Code or Shipping Center not found.");
        }
    }

    // IntelliJ automatically refactored this for me in the latest version

    private String getString(double distance, ZipCode selectedShippingCenter, ZipCode enteredZipCode) {
        double shippingCost = distance * COST_PER_MILE;

        // I needed to replace any _ with a space in the name because i wanted the output message to be clean
        String selectedCity = selectedShippingCenter.getCity().replace("_", " ");
        String enteredCity = enteredZipCode.getCity().replace("_", " ");

        // This outputs the following
        // Title
        // Shipping Center: START
        // Shipping Location: END
        // Shipping Distance: In Miles - xx.xx
        // Shipping Cost: In Dollars - $ xx.xx
        return String.format("Shipping Cost Estimate\n\n" +
                        "Shipping Center: %s - %s\n" +
                        "Shipping Location: %s - %s\n\n" +
                        "Shipping Distance: %.2f Miles\n\n" +
                        "Shipping Cost: $%.2f",
                selectedCity, selectedShippingCenter.getCode(),
                enteredCity, enteredZipCode.getCode(),
                distance, shippingCost);
    }

    // This will get the selected shipping center from the combobox and return the zip code
    private ZipCode getSelectedShippingCenter(ComboBox<String> shippingComboBox) {
        String shippingCenter = shippingComboBox.getValue();
        String zipCode = shippingCenter.substring(shippingCenter.lastIndexOf(" ") + 1);
        return dataManager.searchZipCode(zipCode);
    }

    // Validation of the zipcode
    // Length must be 5 digits
    // Cannot be the same as the selected shipping center
    // Cannot be null
    private boolean isValidZipCode(String zipCode, ComboBox<String> shippingComboBox) {
        if (zipCode.length() != 5) {
            showAlert("Invalid Zip Code", "Please enter a valid 5-digit Zip Code.");
            return false;
        }

        String selectedValue = shippingComboBox.getValue();
        if (selectedValue != null && zipCode.equals(selectedValue.split("\\s+")[1])) {
            showAlert("Invalid Zip Code", "Zip Code cannot be the same as the selected Shipping Center.");
            return false;
        }

        return true;
    }

    // Validation of the shipping center
    // Cannot be null
    private boolean isShippingCenterSelected(ComboBox<String> shippingComboBox) {
        String selectedValue = shippingComboBox.getValue();
        if (selectedValue == null || selectedValue.trim().isEmpty()) {
            showAlert("Invalid Shipping Center", "Please select a valid Shipping Center.");
            return false;
        }
        return true;
    }

    // Method to create the field for the zip code
    private TextField createZipCodeField() {
        TextField zipCodeField = new TextField();
        zipCodeField.setPromptText("Enter Zip Code");
        return zipCodeField;
    }

    // Alert Base
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Clear the fields using the clear button
    private void clearFields(ComboBox<String> shippingComboBox, TextField zipCodeField) {
        shippingComboBox.setValue(null);
        zipCodeField.clear();
    }
}