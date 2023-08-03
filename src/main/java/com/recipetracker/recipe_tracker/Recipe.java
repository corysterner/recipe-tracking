package com.recipetracker.recipe_tracker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Recipe {

    int id;
    String name;
    List<String> ingredients;
    List<String> instructions;
    List<Recipe.Category> categories;
    Recipe.Rating rating;
    int cookTimeMinutes;
    int prepTimeMinutes;
    int totalTimeMinutes;
    int calories;
    int servings;
    String servingSize;
    int authorId;
    String date;

    String description;

    boolean isFavorite;
    boolean createUser;

    // TODO: Build out constructor(s)
    // We probably only need the constructor to
    // take a single object - the type of objects
    // returned from the sql select query
    public Recipe(int recipeId, String name, String desc){
        this.id = recipeId;
        this.name = name;
        this.description = desc;
    }

    /**
     * Build out a full recipe for detailed view in right side
     */
    public Recipe(int recipeId,
                  String name,
                  String desc,
                  String date,
                  String ingredients,
                  String instructions,
                  int prepTime,
                  int cookTime,
                  int totalTime,
                  int calories,
                  int servings,
                  String servingSize,
                  int authorId) {

        //TODO add in user information
        this.id = recipeId;
        this.name = name;
        this.description = desc;
        this.date = date;
        this.ingredients = convertDatabaseArray(ingredients);
        this.instructions = convertDatabaseArray(instructions);
        this.prepTimeMinutes = prepTime;
        this.cookTimeMinutes = cookTime;
        this.totalTimeMinutes = totalTime;
        this.calories = calories;
        this.servings = servings;
        this.servingSize = servingSize;
        this.authorId = authorId;
    }

    private List<String> convertDatabaseArray(String databaseArray){
        databaseArray = databaseArray.replace("[","")
                .replace("'","")
                .replace("]","")
                .replace(", ", ",")
                .replaceAll("\\s+"," ")
                .replace("\"","");
        return Arrays.asList(databaseArray.split(","));
    }

    public void setCategories(List<Recipe.Category> categories){
        this.categories = categories;
    }

    public void setRating(Rating rating){
        this.rating = rating;
    }
    public float getRatingAvg(){
        if (this.rating != null){
            return this.rating.getRating();
        }
        return 0;
    }

    public int getRatingCount(){
        if (this.rating != null){
            return this.rating.getCount();
        }
        return 0;
    }


    public static class Category{
        int id;
        String value;

        Category(int id, String value) {
            this.id = id;
            this.value = value;
        }

        public int getCategoryId(){
            return id;
        }
        public String getCategory(){
            return value;
        }
    }

    public static class Rating{
        int count;
        float rating;

        Rating (float rating, int count){
            this.rating = rating;
            this.count = count;
        }

        public int getCount(){return count;}

        public float getRating(){return rating;}
    }

}
