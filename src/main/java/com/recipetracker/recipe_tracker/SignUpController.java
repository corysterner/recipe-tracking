package com.recipetracker.recipe_tracker;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class SignUpController {
    public TextField firstNameText;
    public TextField lastNameText;
    public TextField emailText;
    public PasswordField passwordText;
    public VBox vbox;

    private Label duplicateEmailErrorLabel = new Label("There is already an account with that email. Please try a different email or go back to login.");
    private Label missingEmailOrPassErrorLabel = new Label("Please enter an email and password.");

    public void onBackToLoginButtonClick(ActionEvent actionEvent) {
        // Go to login view
    }

    public void onSignUpButtonClick(ActionEvent actionEvent) {
        // Clear both error texts (need to figure out how to do this...) - Remove from vbox by object?

        // Check email and password are populated

        // If not, display error (missingEmailOrPass)

        // Hash password

        // Query to check if email already exists

        // If email exists, display error and prompt to use a different email
        // or, go back to login

        // If email does not exist, send query to insert new user
        // Redirect back to login screen
    }
}
