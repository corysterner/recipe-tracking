package com.recipetracker.recipe_tracker;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecipeList extends ArrayList<Recipe> {



    /**
     * Populates RecipeList with recipes given search parameters
     *
     */
    public void performRecipeSearch(int numberRecipes, String sortBy, String searchString, String category,
                                    int minRating, int maxTime, int maxCalories){

        for (int i = 0; i < 20; i++){
            this.add(new Recipe("recipe " + i));
        }

        // TODO: Uncomment/fill in below.
        String queryString = "<SELECT QUERY>";
        // TYPE? results = DbConnector.getDbConnector().selectQuery(queryString);
        // Populate this recipe list with the recipes from the results
    }
}
