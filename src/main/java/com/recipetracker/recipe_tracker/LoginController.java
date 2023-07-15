package com.recipetracker.recipe_tracker;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import org.apache.commons.codec.binary.Hex;

public class LoginController {
    public TextField emailText;
    public PasswordField passwordText;
    @FXML
    private Label welcomeText;

    public void onSignUpLinkClick(ActionEvent actionEvent) {
        // Go to sign up view
    }

    public void onLoginButtonClick(ActionEvent actionEvent) {
        Integer userId = -1;

        // TODO: Hash password - is this the correct hash function? Probably not...
        String saltString = User.getUserSaltStringFromEmail("test@gmail.com" /*emailText.getText()*/);
        System.out.println(saltString);
        String passwordHash = hashPassword(passwordText.getText());

        // TODO: Query w/ email and password hash to receive userId

        // TODO: If userId not returned (is null?), display error and prompt to try again

        // Else if userId returned successfully, go to home view and pass userId
        userId = 2011;

        if(userId != -1) {

            FXMLLoader fxmlLoader = new FXMLLoader(RecipeTrackerApplication.class.getResource("home-view.fxml"));

            AnchorPane root = null;

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