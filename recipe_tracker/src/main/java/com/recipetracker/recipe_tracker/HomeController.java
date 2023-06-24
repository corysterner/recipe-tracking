package com.recipetracker.recipe_tracker;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

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

            try{
                FXMLLoader cardLoader = new FXMLLoader(getClass().getResource("recipe-card.fxml"));
                AnchorPane cardRoot = cardLoader.load();
                vBox.getChildren().add(cardRoot);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            //Label recipeTitle = new Label("Recipe " + i);
            //VBox recipeBox = new VBox(recipeTitle);

        }
        ScrollPane scrollPane = new ScrollPane(vBox);
        return scrollPane;
    }
    public void searchAllRecipes(ActionEvent actionEvent) {
        allRecipesPagination.setPageFactory(this::getPages);
    }

    public void getMyRecipes(Event event) {
    }

    public void getMyFavorites(Event event) {
    }
}