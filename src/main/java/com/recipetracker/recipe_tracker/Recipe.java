package com.recipetracker.recipe_tracker;

import java.util.ArrayList;

public class Recipe {

    int id;
    String name;
    ArrayList<String> categories;
    int rating;
    int cookTimeMinutes;
    int prepTimeMinutes;
    int calories;

    String description;

    boolean isFavorite;
    boolean createUser;

    // TODO: Build out constructor(s)
    // We probably only need the constructor to
    // take a single object - the type of objects
    // returned from the sql select query
    public Recipe(int recipeId, String name, String desc){
        this.id=recipeId;
        this.name = name;
        this.description = desc;
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
}
