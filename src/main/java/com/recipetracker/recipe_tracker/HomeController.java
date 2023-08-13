package com.recipetracker.recipe_tracker;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.controlsfx.control.Rating;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class HomeController implements Initializable {
    Integer userId = 0;
    Recipe currentRecipe;
    public TextField searchTextAllRecipes;
    public ComboBox<Recipe.Category> categoryFilter;
    public ComboBox<Integer> ratingFilter;
    public ComboBox<Integer> timeFilter;
    public ComboBox<Integer> caloriesFilter;
    public Pagination allRecipesPagination;
    public Pagination myRecipesPagination;
    public Pagination myFavoritesPagination;

    //Fields for right side card
    public Label singleRecipeTitle;
    public TextArea recipeDescription;
    public Label prepTimeLabel;
    public Label cookTimeLabel;
    public Label totalTimeLabel;
    public ListView<String> instructionList;
    public ListView<String> ingredientList;
    public Label servingsLabel;
    public Label servingSizeLabel;
    public Label caloriesLabel;
    public Label categoryListLabel;
    public Rating ratingBar;
    public Label ratingLabel;
    public ToggleButton toggleFavorite = new ToggleButton("Favorite");
    public TextArea comments;


    List<Recipe.Category> allCatValues = new ArrayList<>();
    ObservableList<Recipe.Category> availableCategories;
    List<Integer> timeRangeList = new ArrayList<>();
    ObservableList<Integer> availableTime;
    List<Integer> caloriesRangeList = new ArrayList<>();
    ObservableList<Integer> availableCalories;

    List<Integer> ratingList = new ArrayList<>();
    ObservableList<Integer> availableRatings;

    public void initialize(URL fxmlFileLocation, ResourceBundle resources){
        // Get all category values and initialize the SearchableComboBox
        allCatValues = DbConnector.getDbConnector().selectQueryCategory();
        allCatValues.add(new Recipe.Category(0,"All"));
        availableCategories = FXCollections.observableList(allCatValues);
        categoryFilter.setConverter(new CategoryConverter());
        categoryFilter.setItems(availableCategories);

        timeRangeList = getTimeRangeList();
        availableTime = FXCollections.observableList(timeRangeList);
        timeFilter.setItems(availableTime);

        caloriesRangeList = getCaloriesRangeList();
        availableCalories = FXCollections.observableList(caloriesRangeList);
        caloriesFilter.setItems(availableCalories);

        ratingList = getRatingList();
        availableRatings = FXCollections.observableList(ratingList);
        ratingFilter.setItems(availableRatings);

        toggleFavorite.setStyle("-fx-base: gold;");
    }

    private class CategoryConverter extends StringConverter<Recipe.Category> {

        @Override
        public String toString(Recipe.Category category) {
            // Converts category to string of just the category value
            if (category != null) {
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



    public ScrollPane getPages(int pageIndex){

        // Start a new recipe list
        RecipeList recipeList = new RecipeList();

        // Populate recipe list through from search parameters
        // TODO: sortBy needs to be bound to the component and updated here to get that value.
        // TODO: see if getValue() is the right way to get these values - if getValue() returns null, there
        //      seems to be an exception

        recipeList.performRecipeSearch(20, "name",
                searchTextAllRecipes.getText() == null ? "" : searchTextAllRecipes.getText(),
                categoryFilter.getValue() == null ? 0 : categoryFilter.getValue().getCategoryId(),
                ratingFilter.getValue() == null ? 0 : ratingFilter.getValue(),
                timeFilter.getValue() == null ? 0 : timeFilter.getValue(),
                caloriesFilter.getValue() == null ? 0 : caloriesFilter.getValue(),
                -1, false, false);

        // return the Scrollpane populated with all of the recipe cards
        return buildScrollPaneOfRecipes(recipeList);
    }
    public ScrollPane getFavoritesPages (int index){

        // Start a new recipe list
        RecipeList recipeList = new RecipeList();

        // Populate favorite list

        recipeList.performRecipeSearch(20, "name",
                "", 0, 0, 0, 0,
                this.userId, true, false);

        // return the Scrollpane populated with favorites
        return buildScrollPaneOfRecipes(recipeList);

    }
    public ScrollPane getUserPages(int index){

        // Start a new recipe list
        RecipeList recipeList = new RecipeList();

        // Populate recipe list with user recipes

        recipeList.performRecipeSearch(20, "name",
                "", 0, 0, 0, 0,
                this.userId, false, true);

        // return the Scrollpane populated with favorites
        return buildScrollPaneOfRecipes(recipeList);

    }

    public void searchAllRecipes(ActionEvent actionEvent) {
        allRecipesPagination.setPageFactory(this::getPages);
    }

    public void getMyRecipes(Event event) {myRecipesPagination.setPageFactory(this::getUserPages);}

    public void getMyFavorites(Event event) {myFavoritesPagination.setPageFactory(this::getFavoritesPages);}

    public void updateSingleRecipe(int recipeId) {
        //Clear Lists
        clearSingleRecipe();

        // Update the right side scroll pane with this recipe's info
        Recipe recipe = DbConnector.dbConnector.selectQueryFullRecipe(recipeId, this.userId);
        currentRecipe = recipe;

        singleRecipeTitle.setText(recipe.name);
        recipeDescription.setText(recipe.description);
        toggleFavorite.setSelected(recipe.isFavorite);
        Platform.runLater(() ->{
            ratingBar.setRating(recipe.getUserRating() == 0 ? recipe.getRatingAvg() : recipe.getUserRating());
        });
        ratingBar.setPartialRating(recipe.getUserRating() == 0);
        ratingBar.setUpdateOnHover(recipe.getUserRating() == 0);
        ratingLabel.setText(String.format("Avg. Rating of %.1f/5 Over %d Ratings",
                recipe.getRatingAvg(), recipe.getRatingCount()));

        //Quantities
        servingsLabel.setText(Integer.toString(recipe.servings));
        servingSizeLabel.setText(recipe.servingSize);
        caloriesLabel.setText(Integer.toString(recipe.calories));

        //Timing
        prepTimeLabel.setText(String.format("%d min", recipe.prepTimeMinutes));
        cookTimeLabel.setText(String.format("%d min", recipe.cookTimeMinutes));
        totalTimeLabel.setText(String.format("%d min", recipe.totalTimeMinutes));

        //Body
        ingredientList.getItems().addAll(recipe.ingredientsList);
        for (int i = 0; i < recipe.instructionList.size(); i++) {
            instructionList.getItems().add(
                    String.format("%d. %s", i + 1, recipe.instructionList.get(i)));
        }
        for (int i = 0; i < recipe.categories.size(); i++) {
            if (i == 0) {
                categoryListLabel.setText(categoryListLabel.getText() + " " +
                        recipe.categories.get(i).getCategory());
            } else {
                categoryListLabel.setText(categoryListLabel.getText() +
                        ", " +
                        recipe.categories.get(i).getCategory());
            }
        }
        comments.setText(recipe.comments);

    }
    private void clearSingleRecipe(){
        ingredientList.getItems().clear();
        instructionList.getItems().clear();
        categoryListLabel.setText("Categories: ");
    }
    public void updateUserRating(Event event){
        int rating = (int) Math.ceil(ratingBar.getRating());
        ratingBar.setUpdateOnHover(false);

        //Set recipe attributes
        DbConnector.dbConnector.updateRecipeRating(rating, currentRecipe, this.userId);
        Platform.runLater(() ->{
            ratingBar.setRating(rating);

        });

        //Update recipe view
        currentRecipe.setUserRating(rating);
        ratingLabel.setText(String.format("Avg. Rating of %.1f/5 Over %d Ratings",
                currentRecipe.getRatingAvg(), currentRecipe.getRatingCount()));

    }
    public void resetUserRating(Event event){
        float rating = currentRecipe.getUserRating() == 0 ? currentRecipe.getRatingAvg() : (float) currentRecipe.getUserRating();
        ratingBar.setRating(rating);
    }
    public void toggleFavorite(Event event){

        if (currentRecipe.toggleFavorite()){
            DbConnector.getDbConnector().setUserFavorite(currentRecipe, this.userId);
        }
        else{
            DbConnector.getDbConnector().deleteUserFavorite(currentRecipe, this.userId);
        }
    }
    public void saveComments(Event event){
        currentRecipe.setComments(comments.getText());
        DbConnector.getDbConnector().setComments(currentRecipe, this.userId);
    }

    public void openCreateModal(ActionEvent actionEvent) {

        Stage stage = new Stage();
        Parent root = null;
        try {
            FXMLLoader modalLoader = new FXMLLoader(CreateModalController.class.getResource("create-modal.fxml"));
            root = modalLoader.load();
            CreateModalController modalController = modalLoader.getController();
            modalController.setUser(userId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.setScene(new Scene(root));
        stage.setTitle("Create Recipe");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(allRecipesPagination.getScene().getWindow());
        stage.showAndWait();
    }

    public void openEditModal(ActionEvent actionEvent) {

        Stage stage = new Stage();
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(
                    EditModalController.class.getResource("edit-modal.fxml"));
            EditModalController editModalController = new EditModalController(400);
            loader.setController(editModalController);
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.setScene(new Scene(root));
        stage.setTitle("Edit Recipe");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(allRecipesPagination.getScene().getWindow());
        stage.showAndWait();
    }

    /**
     * Builds a Vbox of all the recipes in the list in the order
     * they're added to the list
     */
    public ScrollPane buildScrollPaneOfRecipes(RecipeList recipeList){
        VBox vBox = new VBox();
        for (int i = 0; i < recipeList.size(); i++) {
            try{
                FXMLLoader cardLoader = new FXMLLoader(getClass().getResource("recipe-card.fxml"));
                AnchorPane cardRoot = cardLoader.load();
                RecipeCardController cardController = cardLoader.getController();
                cardController.setRecipe(recipeList.get(i));

                cardController.setParentController(this);

                vBox.getChildren().add(cardRoot);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        ScrollPane scrollPane = new ScrollPane(vBox);
        scrollPane.setStyle("-fx-background-color: transparent");
        return scrollPane;
    }

    public ArrayList<Integer> getTimeRangeList(){
        ArrayList<Integer> result = new ArrayList<>();
        List<Integer> integerList = Arrays.asList(0, 15, 30, 45, 60, 120);
        result.addAll(integerList);
        return result;
    }
    public ArrayList<Integer> getCaloriesRangeList(){
        ArrayList<Integer> result = new ArrayList<>();
        List<Integer> integerList = Arrays.asList(0, 100, 250, 500, 1000, 1500);
        result.addAll(integerList);
        return result;
    }
    public ArrayList<Integer> getRatingList(){
        ArrayList<Integer> result = new ArrayList<>();
        List<Integer> integerList = Arrays.asList(0, 1, 2, 3, 4, 5);
        result.addAll(integerList);
        return result;
    }
    public void setUserId(Integer userId) {
        this.userId = userId;
        searchTextAllRecipes.setText(userId.toString());
    }
}