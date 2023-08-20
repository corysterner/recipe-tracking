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
     */
    public void performRecipeSearch(int numberRecipes, String sortBy, String searchString, int category,
                                    String minRating, String maxTime, String maxCalories, int userId, boolean isFavorite,
                                    boolean isUsers, int pageIndex){

        String queryString =" select recipeid,name,description from recipes where name like \"%"+searchString+"%\"";
        if (maxTime!=null && maxTime!="No Time Limit"){
            queryString = queryString+" AND TotalTime IS NOT NULL AND TotalTime <="+extractInteger(maxTime);
        }
        if (maxCalories!=null && maxCalories!="No Calorie Limit"){
            queryString = queryString+" AND Calories IS NOT NULL AND Calories <="+extractInteger(maxCalories);
        }
        if (category > 0) {
            queryString = queryString+"  AND recipeid IN (select recipeid from recipecategory where categoryid="+category+")";
        }
        if (minRating !=null && minRating!="No Rating Limit") {
            queryString = queryString + " AND recipeid IN (select recipeId from ratings GROUP BY recipeid " +
                    "having AVG(rating) >="+ extractInteger(minRating)+")";
        }
        if (userId != -1){
            if (isFavorite) {
                queryString = queryString + "AND recipeid IN " +
                        "(select recipeId from favorites where userId = " + userId + ")";
            }
            if (isUsers){
                queryString = queryString + "AND authorid = " + userId;
            }
        }
        if (sortBy!=null) {
            queryString= queryString+ " ORDER BY "+sort(sortBy);
        }
        int offset=pageIndex*5;
        queryString=queryString+ " LIMIT 5 OFFSET "+offset;
        ArrayList<Recipe> results = DbConnector.getDbConnector().selectQueryShort(queryString);
        // Populate this recipe list with the recipes from the results
        for (int i = 0; i < results.size();i++) {
            this.add(new Recipe(results.get(i).id,results.get(i).name,results.get(i).description,""));
        }
        if (results.isEmpty() &&!isFavorite &&!isUsers) {
            String noResult="Sorry, no recipes match your search criteria. Please try a different search term or explore other options.";
            this.add(new Recipe(-1,"No Recipe Found",noResult,""));
        }
    }

    /**
     * Sorts the recipe output by a given category value
     * @param order - order to search by
     * @return - sort string to add to query
     */
    public String sort(String order){
        if (order=="name") return order;
        return order+" DESC";
    }

    /**
     * Extract an integer from a string
     * @param input
     * @return
     */
    public static int extractInteger(String input) {
        int extractedValue = 100000; // Default value in case of failure

        String[] parts = input.split(" ");
        if (parts.length >= 3) {
            try {
                extractedValue = Integer.parseInt(parts[2]);
            } catch (NumberFormatException e) {
                // Handle parsing error
                e.printStackTrace();
            }
        }

        return extractedValue;
    }
}
