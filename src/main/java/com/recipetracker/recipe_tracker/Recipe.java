package com.recipetracker.recipe_tracker;

import java.util.ArrayList;

public class Recipe {

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
    public Recipe(String name){
        this.name = name;
        this.description = "Description lorem ipsum....";
    }
}
