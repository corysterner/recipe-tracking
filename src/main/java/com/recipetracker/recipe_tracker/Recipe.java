package com.recipetracker.recipe_tracker;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Recipe {

    int id;
    String name;
    List<String> ingredientsList;
    List<String> instructionList;
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
    int serving;

    String description;
    String instructions;
    String ingredients;
    String comments;
    boolean isFavorite = false;
    boolean createUser;

    // TODO: Build out constructor(s)
    // We probably only need the constructor to
    // take a single object - the type of objects
    // returned from the sql select query
    public Recipe(int recipeId, String name, String desc,String instructions){
        this.id=recipeId;
        this.name = name;
        this.description = desc;
        this.instructions = instructions;
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
        this.ingredientsList = convertDatabaseArray(ingredients, "\"");
        this.instructionList = convertDatabaseArray(instructions, "'");
        this.prepTimeMinutes = prepTime;
        this.cookTimeMinutes = cookTime;
        this.totalTimeMinutes = totalTime;
        this.calories = calories;
        this.servings = servings;
        this.servingSize = servingSize;
        this.authorId = authorId;
    }

    private List<String> convertDatabaseArray(String databaseArray, String separator){
        databaseArray = databaseArray.replace("[" + separator,"")
                .replace(separator + "]","")
                .replace(", " + separator, "," + separator)
                .replace("," + separator + separator + ",",",")
                .replaceAll("\\s+"," ");
        List<String> list = new LinkedList<String>(Arrays.asList(databaseArray.split(separator + "," + separator)));
        if (list.get(0).equals("")) list.remove(0);
        return list;
    }
    public void setFavorite(boolean isFavorite){
        this.isFavorite = isFavorite;
    }
    public void setComments(String comments){
        this.comments = comments;
    }
    public boolean toggleFavorite(){
        this.isFavorite = !isFavorite;
        return this.isFavorite;
    }

    public void setCategories(List<Recipe.Category> categories){
        this.categories = categories;
    }

    public void setRating(Rating rating){
        this.rating = rating;
    }
    public float getRatingAvg(){
        if (this.rating != null){
            return this.rating.getAverageRating();
        }
        return 0;
    }

    public int getRatingCount(){
        if (this.rating != null){
            return this.rating.getCount();
        }
        return 0;
    }
    public int getUserRating() {
        if (this.rating != null) {
            return this.rating.getUserRating();
        }
        return 0;
    }
    public void setUserRating(int userRating){
        if (this.rating != null) {
            this.rating.userRating = userRating;
        }
    }
    public void setRatingCount(int count){
        if (this.rating != null) {
            this.rating.count = count;
        }
    }
    public void setAverageRating(float averageRating){
        if (this.rating != null) {
            this.rating.averageRating = averageRating;
        }
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
        float averageRating;
        int userRating = 0;

        Rating (float averageRating, int count, int userRating){
            this (averageRating, count);
            this.userRating = userRating;
        }

        Rating (float averageRating, int count){
            this.averageRating = averageRating;
            this.count = count;
        }

        public int getCount(){return count;}

        public float getAverageRating(){return averageRating;}
        public int getUserRating(){
            return this.userRating;
        };
    }

}
