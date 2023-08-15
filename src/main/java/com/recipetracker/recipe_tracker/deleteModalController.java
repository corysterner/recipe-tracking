package com.recipetracker.recipe_tracker;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.controlsfx.control.SearchableComboBox;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class deleteModalController implements Initializable {
    private int recipeId;
    Integer userId = 0;
    public Button deleteButton;
    public Button cancelButton;
    public Label recipeName;

    public deleteModalController(int recipeId) {
        this.recipeId = recipeId;
    }
    public void initialize(URL fxmlFileLocation, ResourceBundle resources){

        if (recipeId>0){
            String queryString =" select name from recipes where RecipeId="+recipeId;
            ArrayList<Recipe> results = DbConnector.getDbConnector().selectQueryShort(queryString);
            if (results.size()>0) {
                recipeName.setText(results.get(0).name);

            }
        }

    }
    public void deleteRecipe(ActionEvent actionEvent) {

        String queryString = "DELETE FROM favorites WHERE recipeId="+recipeId;
        DbConnector.getDbConnector().createOrUpdateQuery(queryString);

        queryString = "DELETE FROM RecipeCategory WHERE recipeId="+recipeId;
        DbConnector.getDbConnector().createOrUpdateQuery(queryString);

        queryString = "DELETE FROM ratings WHERE recipeId="+recipeId;
        DbConnector.getDbConnector().createOrUpdateQuery(queryString);

        queryString = "DELETE FROM comments WHERE recipeId="+recipeId;
        DbConnector.getDbConnector().createOrUpdateQuery(queryString);

        queryString = "DELETE FROM recipes WHERE recipeId="+recipeId;
        DbConnector.getDbConnector().createOrUpdateQuery(queryString);

        recipeId=-1;
        Stage stage = (Stage) deleteButton.getScene().getWindow();
        stage.close();

    }
    public void cancelDelete(ActionEvent actionEvent) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

}
