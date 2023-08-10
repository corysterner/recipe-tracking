package com.recipetracker.recipe_tracker;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.sql.rowset.CachedRowSet;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.SQLException;

public class SignUpController {
    public TextField firstNameText;
    public TextField lastNameText;
    public TextField emailText;
    public PasswordField passwordText;
    public VBox vbox;

    private Label duplicateEmailErrorLabel = new Label("There is already an account with that email. " +
            "Please try a different email or go back to login.");
    private Label missingEmailOrPassErrorLabel = new Label("Please enter an email and password.");

    /**
     * Takes user back to the login screen
     * @param actionEvent
     */
    public void onBackToLoginButtonClick(ActionEvent actionEvent) {
        Parent root;

        try {
            root = FXMLLoader.load(getClass().getResource("login-view.fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Scene scene = new Scene(root, 1200, 800);
        Stage stage = (Stage) emailText.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Attempts to sign the user up.
     * Displays errors appropriate for missing/invalid inputs.
     * @param actionEvent
     */
    public void onSignUpButtonClick(ActionEvent actionEvent) {

        // Initialize error label colors
        String red = "EF4523";
        duplicateEmailErrorLabel.setTextFill(Color.web(red));
        missingEmailOrPassErrorLabel.setTextFill(Color.web(red));

        boolean badInput = false;

        // Clear both error texts if they are present
        vbox.getChildren().remove(duplicateEmailErrorLabel);
        vbox.getChildren().remove(missingEmailOrPassErrorLabel);

        // Check email and password are populated
        if(emailText.getText().isBlank() || passwordText.getText().isBlank()){
            badInput = true;
            vbox.getChildren().add(missingEmailOrPassErrorLabel);
        }

        // Check if email already exists
        if(emailExists(emailText.getText())){
            badInput = true;
            vbox.getChildren().add(duplicateEmailErrorLabel);
        }

        // Return early if input is bad
        if(badInput){
            return;
        }

        // Generate salt for hash
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        // Hash password
        String passwordHash = User.hashPassword(passwordText.getText(), salt);

        // Generate string from byte[] for storing in DB
        String saltString = Hex.encodeHexString(salt);

        // Send query to insert new user
        boolean success = false;

        String queryString = "INSERT INTO users (FirstName, LastName, Email, PasswordHash, Salt)"+
                "VALUES ('"+
                firstNameText.getText() + "','" +
                lastNameText.getText() + "','" +
                emailText.getText() + "','" +
                passwordHash + "','" +
                saltString + "')";

        DbConnector.getDbConnector().createOrUpdateQuery(queryString);

        // Redirect back to login screen
        Parent root;

        try {
            root = FXMLLoader.load(getClass().getResource("login-view.fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Scene scene = new Scene(root, 1200, 800);
        Stage stage = (Stage) emailText.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Checks if the provided email already exists in the database.
     * Returns true if it does.
     *
     * @param email
     * @return
     */
    private boolean emailExists(String email) {
        String queryString = "SELECT count(email) emailCount FROM users\n" +
                "WHERE Email = '" + email + "'";
        CachedRowSet result = DbConnector.getDbConnector().selectQuery(queryString);

        try {
            result.next();
            if(result.getInt("emailCount") > 0){
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

}
