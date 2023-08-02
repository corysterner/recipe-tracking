package com.recipetracker.recipe_tracker;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;


public class HomeController implements Initializable {
    public TextField searchTextAllRecipes;
    public ComboBox<Recipe.Category> categoryFilter;
    public ComboBox<Integer> ratingFilter;
    public ComboBox<Integer> timeFilter;
    public ComboBox<Integer> caloriesFilter;
    public Pagination allRecipesPagination;


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
                caloriesFilter.getValue() == null ? 0 : caloriesFilter.getValue());

        // return the Scrollpane populated with all of the recipe cards
        return buildScrollPaneOfRecipes(recipeList);
    }
    public void searchAllRecipes(ActionEvent actionEvent) {
        allRecipesPagination.setPageFactory(this::getPages);
    }

    public void getMyRecipes(Event event) {
    }

    public void getMyFavorites(Event event) {
    }

    public void updateSingleRecipe(int recipeId) {
        // Update the right side scroll pane with this recipe's info
    }

    public void openCreateModal(ActionEvent actionEvent) {

        Stage stage = new Stage();
        Parent root = null;
        try {
            root = FXMLLoader.load(
                    CreateModalController.class.getResource("create-modal.fxml"));
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
}