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

import java.io.IOException;

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
        String passwordHash = passwordText.getText().hashCode();

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
}