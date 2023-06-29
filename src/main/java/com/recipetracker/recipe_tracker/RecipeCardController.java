package com.recipetracker.recipe_tracker;

import javafx.scene.input.MouseEvent;

public class RecipeCardController {
    HomeController homeController;
    int recipeId = -1;
    public void updateSingleRecipe(MouseEvent mouseEvent) {
        if(homeController != null && recipeId != -1) {
            homeController.updateSingleRecipe(recipeId);
        }
    }

    public void setParentController(HomeController homeController) {
        this.homeController = homeController;
    }
}
