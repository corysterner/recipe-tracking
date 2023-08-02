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
    public void performRecipeSearch(int numberRecipes, String sortBy, String searchString, int category,
                                    int minRating, int maxTime, int maxCalories){

        String queryString = "select recipeid,name,description from recipes where name like \"%"+searchString+"%\"";
        if (maxTime>0){
            queryString = queryString+" AND TotalTime <="+maxTime;
        }
        if (maxTime>0){
            queryString = queryString+" AND Calories <="+maxCalories;
        }
        if (category > 0) {
            queryString = queryString+"  AND recipeid IN (select recipeid from recipecategory where categoryid="+category+")";
        }
        ArrayList<Recipe> results = DbConnector.getDbConnector().selectQueryShort(queryString);
        // Populate this recipe list with the recipes from the results
        for (int i = 0; i < results.size();i++) {
            this.add(new Recipe(results.get(i).id,results.get(i).name,results.get(i).description));
        }
    }
}
