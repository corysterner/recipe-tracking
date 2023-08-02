package com.recipetracker.recipe_tracker;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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
import java.util.List;
import java.util.ResourceBundle;

public class EditModalController implements Initializable {
    private int recipeId;
    public Button saveButton;
    public TextField nameText;
    public TextArea descriptionText;
    public TextArea instructionsText;
    public SearchableComboBox<Recipe.Category> categoryComboBox;
    public FlowPane selectedCatFlowPane;



    // Stores the full list of categories from the DB.
    List<Recipe.Category> allCatValues = new ArrayList<>();

    // Stores the list of categories currently selected.
    List<Recipe.Category> selectedCatValues = new ArrayList<>();

    // Used to populate the SearchableComboBox with the category items
    ObservableList<Recipe.Category> availableCategories;

    public EditModalController(int recipeId) {
        this.recipeId = recipeId;
    }
    public void initialize(URL fxmlFileLocation, ResourceBundle resources){

        // To give some spacing to the category buttons within the FlowPane
        selectedCatFlowPane.setHgap(10);
        selectedCatFlowPane.setVgap(5);

        // Get all category values and initialize the SearchableComboBox
        allCatValues = getAllCategoryValues();
        availableCategories = FXCollections.observableList(allCatValues);
        categoryComboBox.setConverter(new CategoryConverter());
        categoryComboBox.setItems(availableCategories);

        String queryString =" select name,description,REPLACE(REPLACE(instructions,',', '\\n'),\"'\",'') AS instructions from recipes where RecipeId="+recipeId;
        ArrayList<Recipe> results = DbConnector.getDbConnector().selectQueryShort(queryString);
        if (results.size()>0){
            nameText.setText(results.get(0).name);
            descriptionText.setText(results.get(0).description);
            instructionsText.setText(results.get(0).instructions);
        }

    }

    /**
     * Pulls all of the category values from the DB.
     * @return
     */
    public static List<Recipe.Category> getAllCategoryValues() {
        List<Recipe.Category> catValues = DbConnector.getDbConnector().selectQueryCategory();
        return catValues;
    }

    /**
     * Used by the SearchableComboBox to display the values in a readable manner
     */
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

        // TODO: Update to actually file all of the recipe info
        String queryString = "insert into test_table (string) values ('test')";
        DbConnector.getDbConnector().createOrUpdateQuery(queryString);

        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Adds the selected category:
     * - Adds a button for the category that, when clicked, removes the category
     * - Adds the category to the "selected" categories list to be referenced later
     * @param actionEvent
     */
    public void addCategoryToRecipe(ActionEvent actionEvent) {

        // Get value selected
        Recipe.Category cat = categoryComboBox.getValue();

        // If already selected, skip
        if(selectedCatValues.contains(cat)){
            return;
        }

        // Get/Add image for the "X" icon
        Image image;
        try {
            InputStream input= new FileInputStream("src/main/resources/images/path961.png");
            image = new Image(input);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        ImageView xIcon = new ImageView(image);
        xIcon.setFitHeight(10);
        xIcon.setFitWidth(10);

        // Create button for category, style, and add the "X" icon
        Button tag = new Button(cat.getCategory(), xIcon);
        tag.setStyle("-fx-min-height: 20px; " +
                "-fx-max-height: 20px;" +
                "-fx-background-color: f9eaffff;" +
                "-fx-background-radius: 50");
        tag.setTextFill(Paint.valueOf("#7800a8"));
        tag.setPadding(new Insets(-1, 8, 0, 8));
        tag.setContentDisplay(ContentDisplay.RIGHT);
        tag.setId("CatButton" + cat.id);

        // Add onAction handler to button that deletes the button
        // when clicked and removes the category from the selected categories list
        tag.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                selectedCatValues.remove(cat);
                selectedCatFlowPane.getChildren().remove(tag);
            }
        });

        // Add the category button and reset the box to be blank
        selectedCatFlowPane.getChildren().add(tag);
        selectedCatValues.add(cat);
        categoryComboBox.setValue(null);
    }
}
