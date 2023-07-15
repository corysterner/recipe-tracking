package com.recipetracker.recipe_tracker;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeController {
    Integer userId = 0;
    public TextField searchTextAllRecipes;
    public ComboBox<String> categoryFilter;
    public ComboBox<Integer> ratingFilter;
    public ComboBox<Integer> timeFilter;
    public ComboBox<Integer> caloriesFilter;
    public Pagination allRecipesPagination;

    public ScrollPane getPages(int pageIndex){

        // Start a new recipe list
        RecipeList recipeList = new RecipeList();

        // Populate recipe list through from search parameters
        // TODO: sortBy needs to be bound to the component and updated here to get that value.
        // TODO: see if getValue() is the right way to get these values - if getValue() returns null, there
        //      seems to be an exception
        recipeList.performRecipeSearch(20, "name",
                searchTextAllRecipes.getText() == null ? "" : searchTextAllRecipes.getText(),
                categoryFilter.getValue() == null ? "" : categoryFilter.getValue(),
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

    public void setUserId(Integer userId) {
        this.userId = userId;
        searchTextAllRecipes.setText(userId.toString());
    }
}