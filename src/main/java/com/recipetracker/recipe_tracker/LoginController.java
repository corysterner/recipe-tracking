package com.recipetracker.recipe_tracker;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.sql.rowset.CachedRowSet;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.SQLException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public class LoginController {
    public TextField emailText;
    public PasswordField passwordText;
    public VBox vbox;
    @FXML
    private Label welcomeText;

    /**
     * Takes user to the sign up screen.
     * @param actionEvent
     */
    public void onSignUpLinkClick(ActionEvent actionEvent) {

        Parent root;

        try {
            root = FXMLLoader.load(getClass().getResource("signup-view.fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Scene scene = new Scene(root, 1200, 800);
        Stage stage = (Stage) emailText.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Attempts to log the user in.
     * Displays errors as appropriate for incorrect email/password.
     * Upon successful log in, takes user to home view and passes the userId.
     * @param actionEvent
     */
    public void onLoginButtonClick(ActionEvent actionEvent) {
        Integer userId = -1;

        // Get the salt for the user from the DB. Convert to byte[] for hashing
        String saltString = User.getUserSaltStringFromEmail(emailText.getText());
        byte[] salt;
        try {
            salt = Hex.decodeHex(saltString);
        } catch (DecoderException e) {
            throw new RuntimeException(e);
        }

        // Hash the password provided by the user
        String passwordHash = User.hashPassword(passwordText.getText(), salt);

        // Query w/ email and password hash to receive userId (if email/pass correct)
        String queryString = "SELECT UserId FROM users\n" +
                "WHERE Email = '" + emailText.getText() + "' " +
                "AND PasswordHash = '" + passwordHash + "'";
        CachedRowSet result = DbConnector.getDbConnector().selectQuery(queryString);

        try {
            // If userId not returned, display error and prompt to try again
            if(!result.isBeforeFirst()){
                // Display error label
                Label incorrectEmailOrPass = new Label("Email or password is incorrect. Please try again.");
                String red = "EF4523";
                incorrectEmailOrPass.setTextFill(Color.web(red));
                vbox.getChildren().add(incorrectEmailOrPass);
                return;
            } else {
                // get the UserId and set it
                result.next();
                userId = result.getInt("UserId");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Make sure userId actually set
        // Then, take user to home view and pass the userId
        if(userId != -1) {

            FXMLLoader fxmlLoader = new FXMLLoader(RecipeTrackerApplication.class.getResource("home-view.fxml"));
            Parent root = null;

            try {
                root = fxmlLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            HomeController homeController = fxmlLoader.getController();
            homeController.setUserId(userId);

            Scene scene = new Scene(root, 1200, 800);
            Stage stage = (Stage) emailText.getScene().getWindow();
            stage.setTitle("Home");
            stage.setScene(scene);
            stage.show();
        }
    }
}