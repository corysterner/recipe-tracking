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

    /**
     * Constructor to build a simple recipe
     * @param recipeId
     * @param name
     * @param desc
     * @param instructions
     */
    public Recipe(int recipeId, String name, String desc,String instructions){
        this.id=recipeId;
        this.name = name;
        this.description = desc;
        this.instructions = instructions;
    }

    /**
     * Constructor to build a full recipe
     * @param recipeId
     * @param name
     * @param desc
     * @param date
     * @param ingredients
     * @param instructions
     * @param prepTime
     * @param cookTime
     * @param totalTime
     * @param calories
     * @param servings
     * @param servingSize
     * @param authorId
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

    /**
     * Convert an array received in database format into a string list
     * Assumed format is "[<Separator><Value><Separator>,<Separator><Value><Separator>...
     * @param databaseArray - string in database array format
     * @param separator - array separator
     * @return - list of string values
     */
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

    /**
     * Set the current recipe as a favorite
     * @param isFavorite
     */
    public void setFavorite(boolean isFavorite){
        this.isFavorite = isFavorite;
    }

    /**
     * Set comments for the recipe
     * @param comments - comments to set
     */
    public void setComments(String comments){
        this.comments = comments;
    }

    /**
     * Toggle whether the recipe is a favorite
     * @return
     */
    public boolean toggleFavorite(){
        this.isFavorite = !isFavorite;
        return this.isFavorite;
    }

    /**
     * Set the categories for a recipe
     * @param categories
     */
    public void setCategories(List<Recipe.Category> categories){
        this.categories = categories;
    }

    /**
     * Set the rating of a recipe
     * @param rating - rating object
     */
    public void setRating(Rating rating){
        this.rating = rating;
    }

    /**
     * Get the average rating for a recipe
     * @return
     */
    public float getRatingAvg(){
        if (this.rating != null){
            return this.rating.getAverageRating();
        }
        return 0;
    }

    /**
     * Get the count of ratings for a recipe
     * @return
     */
    public int getRatingCount(){
        if (this.rating != null){
            return this.rating.getCount();
        }
        return 0;
    }

    /**
     * Get the value of the user rating for recipe
     * @return
     */
    public int getUserRating() {
        if (this.rating != null) {
            return this.rating.getUserRating();
        }
        return 0;
    }

    /**
     * Set the user rating for a recipe
     * @param userRating
     */
    public void setUserRating(int userRating){
        if (this.rating != null) {
            this.rating.userRating = userRating;
        }
    }
    /**
     * Set the count of ratings for a recipe
     */
    public void setRatingCount(int count){
        if (this.rating != null) {
            this.rating.count = count;
        }
    }

    /**
     * Set the average ratings for a recipe
     * @param averageRating
     */
    public void setAverageRating(float averageRating){
        if (this.rating != null) {
            this.rating.averageRating = averageRating;
        }
    }

    /**
     * Sub recipe object for categories
     */
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

    /**
     * Sub recipe object for ratings
     */
    public static class Rating{
        int count;
        float averageRating;
        int userRating = 0;

        /**
         * Constructor when there is a user rating
         * @param averageRating
         * @param count
         * @param userRating
         */
        Rating (float averageRating, int count, int userRating){
            this (averageRating, count);
            this.userRating = userRating;
        }

        /**
         * Constructor for when there is only an average rating and count
         * @param averageRating
         * @param count
         */
        Rating (float averageRating, int count){
            this.averageRating = averageRating;
            this.count = count;
        }

        /**
         * Returns the count of ratings for a recipe
         * @return
         */
        public int getCount(){return count;}

        /**
         * Returns the average rating for a recipe
         * @return
         */
        public float getAverageRating(){return averageRating;}

        /**
         * Returns the user rating for a recipe
         * @return
         */
        public int getUserRating(){
            return this.userRating;
        };
    }

}
