package com.recipetracker.recipe_tracker;

import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

// Recipe card to display the recipe name and description
public class RecipeCardController {
    public Label recipeName;
    public Label recipeDescription;
    HomeController homeController;
    private Recipe recipe;
    int recipeId = -1;

    /**
    * Updates the right recipe pane with a single recipe
    * When the recipe card is clicked
     * @param mouseEvent
     */
    public void updateSingleRecipe(MouseEvent mouseEvent) {
        if(homeController != null && recipeId != -1) {
            homeController.updateSingleRecipe(recipeId);
        }
    }
    /**
    * Sets the parent controller, so updates can be passed
    * up to the parent (e.g. to update right recipe pane)
     * @param recipeId
     */
    public void setParentController(HomeController homeController) {
        this.homeController = homeController;
    }
    /**
    * Sets the recipe for this controller
    * Also updates the recipeName and recipeDescription fields
    * on the controller from the recipe.
    * @param recipe
    */
    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
        recipeName.setText(recipe.name);
        recipeDescription.setText(recipe.description);
        this.recipeId = recipe.id;
    }
}
