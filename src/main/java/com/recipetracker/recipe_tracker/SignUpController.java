package com.recipetracker.recipe_tracker;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

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

    private String hashPassword(String password) {
        SecureRandom random = new SecureRandom();

        byte[] salt = new byte[16];
        random.nextBytes(salt);

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 10000, 512);
        SecretKeyFactory factory = null;
        try {
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        byte[] hash = new byte[0];

        try {
            hash = factory.generateSecret(spec).getEncoded();
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }

        String hashString = Hex.encodeHexString(hash);
        String saltString = Hex.encodeHexString(salt);

        // Store as salt:hash so the salt can be retrieved later
        return saltString + ":" + hashString;
    }
}
