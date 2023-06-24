package com.recipetracker.recipe_tracker;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class HomeController {
    public TextField searchTextAllRecipes;
    public ComboBox categoryFilter;
    public ComboBox ratingFilter;
    public ComboBox timeFilter;
    public ComboBox caloriesFilter;
    public Pagination allRecipesPagination;

    public ScrollPane getPages(int pageIndex){
        VBox vBox = new VBox();
        for(int i = pageIndex; i < pageIndex + 100; i++){
            Label recipeTitle = new Label("Recipe " + i);
            VBox recipeBox = new VBox(recipeTitle);
            vBox.getChildren().add(recipeBox);
        }
        ScrollPane scrollPane = new ScrollPane(vBox);
        return scrollPane;
    }
    public void searchAllRecipes(ActionEvent actionEvent) {
        allRecipesPagination.setPageFactory(this::getPages);
    }
}