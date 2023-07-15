package com.recipetracker.recipe_tracker;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.controlsfx.control.SearchableComboBox;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CreateModalController implements Initializable {
    public Button saveButton;
    public SearchableComboBox<Recipe.Category> categoryComboBox;
    public FlowPane selectedCatFlowPane;

    List<Recipe.Category> allCatValues = new ArrayList<Recipe.Category>();
    ObservableList<Recipe.Category> availableCategories = FXCollections.observableList(allCatValues);

    public void initialize(URL fxmlFileLocation, ResourceBundle resources){
        availableCategories.add(new Recipe.Category(1, "cat1"));
        availableCategories.add(new Recipe.Category(2, "cat2"));

        categoryComboBox.setConverter(new CategoryConverter());
        categoryComboBox.setItems(availableCategories);

        availableCategories.add(new Recipe.Category(3, "cat3"));
    }

    public class CategoryConverter extends StringConverter<Recipe.Category> {

        @Override
        public String toString(Recipe.Category category) {
            // Converts category to string of just the category value
            if(category != null) {
                return category.getCategory();
            } else {
                return "";
            }
        }

        @Override
        public Recipe.Category fromString(String s) {
            return null;
        }

    }

    public void saveAndCloseModal(ActionEvent actionEvent) {

        String queryString = "insert into test_table (string) values ('test')";
        DbConnector.getDbConnector().createOrUpdateQuery(queryString);

        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();

        //TODO: DELETE THIS SECTION ONCE DbConnector Working
        /*
        class MysqlCon{
            public static void query(){
                try{
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con=DriverManager.getConnection(
                            "jdbc:mysql://recipe-tracking.ddns.net:54/recipe-tracking","recipe-tracker-mysql-user","1M^2POyWpIfDf@Bj45");
                    System.out.println("Got here");
                    Statement stmt=con.createStatement();
                    stmt.execute("insert into test_table (string) values ('test')");
                    con.close();
                }catch(Exception e){ System.out.println(e);}
            }
        }

        MysqlCon.query();
        */
    }

    public void addCategoryToRecipe(ActionEvent actionEvent) {
        Recipe.Category cat = categoryComboBox.getValue();

        Label label = new Label(cat.getCategory());
        selectedCatFlowPane.getChildren().add(label);
    }
}
