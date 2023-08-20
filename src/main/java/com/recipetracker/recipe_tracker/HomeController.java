package com.recipetracker.recipe_tracker;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.controlsfx.control.Rating;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class HomeController implements Initializable {
    Integer userId = 0;
    Recipe currentRecipe;
    public TextField searchTextAllRecipes;
    public Label userName;
    public ComboBox<Recipe.Category> categoryFilter;
    public ComboBox<String> ratingFilter;
    public ComboBox<String> timeFilter;
    public ComboBox<String> caloriesFilter;
    public ComboBox<String> orderByFilter;
    public Pagination allRecipesPagination;
    public Pagination myRecipesPagination;
    public Pagination myFavoritesPagination;

    //Fields for right side card
    public Label singleRecipeTitle;
    public TextArea recipeDescription;
    public Label prepTimeLabel;
    public Label cookTimeLabel;
    public Label totalTimeLabel;
    public ListView<String> instructionList;
    public ListView<String> ingredientList;
    public Label servingsLabel;
    public Label servingSizeLabel;
    public Label caloriesLabel;
    public Label categoryListLabel;
    public Rating ratingBar;
    public Label ratingLabel;
    public ToggleButton toggleFavorite = new ToggleButton("Favorite");
    public TextArea comments;

    public Button editRecipe;
    public Button deleteRecipe;

    List<Recipe.Category> allCatValues = new ArrayList<>();
    ObservableList<Recipe.Category> availableCategories;
    List<String> timeRangeList = new ArrayList<>();
    ObservableList<String> availableTime;
    List<String> caloriesRangeList = new ArrayList<>();
    ObservableList<String> availableCalories;

    List<String> ratingList = new ArrayList<>();
    ObservableList<String> availableRatings;
    List<String> orderList  = new ArrayList<>();
    ObservableList<String > availableOrderBy;

    /**
     * Initializes the home page
     * @param fxmlFileLocation - file location for the home fxml
     * @param resources - bundle of resources
     */
    public void initialize(URL fxmlFileLocation, ResourceBundle resources){
        // Get all category values and initialize the SearchableComboBox
        allCatValues = DbConnector.getDbConnector().selectQueryCategory();
        allCatValues.add(new Recipe.Category(0,"All"));
        availableCategories = FXCollections.observableList(allCatValues);
        categoryFilter.setConverter(new CategoryConverter());
        categoryFilter.setItems(availableCategories);

        editRecipe.setVisible(false);
        deleteRecipe.setVisible(false);

        timeRangeList = getTimeRangeList();
        availableTime = FXCollections.observableList(timeRangeList);
        timeFilter.setItems(availableTime);

        caloriesRangeList = getCaloriesRangeList();
        availableCalories = FXCollections.observableList(caloriesRangeList);
        caloriesFilter.setItems(availableCalories);

        ratingList = getRatingList();
        availableRatings = FXCollections.observableList(ratingList);
        ratingFilter.setItems(availableRatings);

        orderList =getOrderList();
        availableOrderBy = FXCollections.observableList(orderList);
        orderByFilter.setItems(availableOrderBy);

    }

    /**
     * Converts a recipe category to a string and vice versa
     */
    private class CategoryConverter extends StringConverter<Recipe.Category> {
        /**
         * Converts a recipe category to a string
         * @param category - category to convert
         * @return - string of the category
         */
        @Override
        public String toString(Recipe.Category category) {
            // Converts category to string of just the category value
            if (category != null) {
                return category.getCategory();
            } else {
                return "";
            }
        }

        /**
         * Converts a string to a category
         * @param s - string to convert
         * @return - null always
         */
        @Override
        public Recipe.Category fromString(String s) {
            return null;
        }
    }


    /**
     * gets all the pages for pagination on the all recipes search
     * @param pageIndex - pagination index
     * @return - scroll pane with the pages for the specified index
     */
    public ScrollPane getPages(int pageIndex){

        // Start a new recipe list
        RecipeList recipeList = new RecipeList();

        // Populate recipe list through from search parameters
        // TODO: sortBy needs to be bound to the component and updated here to get that value.
        // TODO: see if getValue() is the right way to get these values - if getValue() returns null, there
        //      seems to be an exception

        recipeList.performRecipeSearch(20, orderByFilter.getValue(),
                searchTextAllRecipes.getText() == null ? "" : searchTextAllRecipes.getText(),
                categoryFilter.getValue() == null ? 0 : categoryFilter.getValue().getCategoryId(),
                ratingFilter.getValue(),
                timeFilter.getValue(),
                caloriesFilter.getValue(),
                -1, false, false,pageIndex);

        // return the Scrollpane populated with all of the recipe cards
        return buildScrollPaneOfRecipes(recipeList);
    }

    /**
     * Create a paginated scroll pane for the favorites tab
     * @param index - pagination index
     * @return - scroll pane for the favorites page
     */
    public ScrollPane getFavoritesPages (int index){

        // Start a new recipe list
        RecipeList recipeList = new RecipeList();

        // Populate favorite list

        recipeList.performRecipeSearch(20, "name",
                "", 0, null, null, null,
                this.userId, true, false,0);

        // return the Scrollpane populated with favorites
        return buildScrollPaneOfRecipes(recipeList);

    }
    /**
     * Create a paginated scroll pane for the my recipes tab
     * @param index - pagination index
     * @return - scroll pane for the my recipes page
     */
    public ScrollPane getUserPages(int index){

        // Start a new recipe list
        RecipeList recipeList = new RecipeList();

        // Populate recipe list with user recipes

        recipeList.performRecipeSearch(20, "name",
                "", 0, null, null, null,
                this.userId, false, true,0);

        // return the Scrollpane populated with favorites
        return buildScrollPaneOfRecipes(recipeList);

    }

    /**
     * Event to search over all recipes
     * @param actionEvent - event trigger
     */
    public void searchAllRecipes(ActionEvent actionEvent) {
        allRecipesPagination.setPageFactory(this::getPages);
    }

    /**
     * Event to search over user recipes
     * @param event - event trigger
     */
    public void getMyRecipes(Event event) {myRecipesPagination.setPageFactory(this::getUserPages);}

    /**
     * Event to search over user favorites
     * @param event - event trigger
     */
    public void getMyFavorites(Event event) {myFavoritesPagination.setPageFactory(this::getFavoritesPages);}

    /**
     * Function to display a single recipe in the right pane
     * @param recipeId - recipe ID of recipe to display
     */
    public void updateSingleRecipe(int recipeId) {
        //Clear Lists
        clearSingleRecipe();
        if (recipeId==0) {return;}

        // Update the right side scroll pane with this recipe's info
        Recipe recipe = DbConnector.dbConnector.selectQueryFullRecipe(recipeId, this.userId);
        if (recipe==null) {return;}
        currentRecipe = recipe;
        //Recipe recipe = DbConnector.dbConnector.selectQueryFullRecipe(recipeId);
        if ((recipeId > 0) && (userId==recipe.authorId)){
            editRecipe.setVisible(true);
            deleteRecipe.setVisible(true);
        }else{
            editRecipe.setVisible(false);
            deleteRecipe.setVisible(false);
        }

        singleRecipeTitle.setText(recipe.name);
        recipeDescription.setText(recipe.description);
        toggleFavorite.setSelected(recipe.isFavorite);
        if (recipe.isFavorite) toggleFavorite.setStyle("-fx-base: gold;");
        else toggleFavorite.setStyle("");

        Platform.runLater(() ->{
            ratingBar.setRating(recipe.getUserRating() == 0 ? recipe.getRatingAvg() : recipe.getUserRating());
        });
        ratingBar.setPartialRating(recipe.getUserRating() == 0);
        ratingBar.setUpdateOnHover(recipe.getUserRating() == 0);
        ratingLabel.setText(String.format("Avg. Rating of %.1f/5 Over %d Ratings",
                recipe.getRatingAvg(), recipe.getRatingCount()));

        //Quantities
        servingsLabel.setText(Integer.toString(recipe.servings));
        servingSizeLabel.setText(recipe.servingSize);
        caloriesLabel.setText(Integer.toString(recipe.calories));

        //Timing
        prepTimeLabel.setText(String.format("%d min", recipe.prepTimeMinutes));
        cookTimeLabel.setText(String.format("%d min", recipe.cookTimeMinutes));
        totalTimeLabel.setText(String.format("%d min", recipe.totalTimeMinutes));

        //Body
        ingredientList.getItems().addAll(recipe.ingredientsList);
        for (int i = 0; i < recipe.instructionList.size(); i++) {
            instructionList.getItems().add(
                    String.format("%d. %s", i + 1, recipe.instructionList.get(i)));
        }
        for (int i = 0; i < recipe.categories.size(); i++) {
            if (i == 0) {
                categoryListLabel.setText(categoryListLabel.getText() + " " +
                        recipe.categories.get(i).getCategory());
            } else {
                categoryListLabel.setText(categoryListLabel.getText() +
                        ", " +
                        recipe.categories.get(i).getCategory());
            }
        }
        comments.setText(recipe.comments);

    }

    /**
     * Clear out all of the values from a single recipe
     */
    private void clearSingleRecipe(){
        ingredientList.getItems().clear();
        instructionList.getItems().clear();
        categoryListLabel.setText("Categories: ");
    }

    /**
     * Update the user rating for a single recipe
     * @param event - event
     */
    public void updateUserRating(Event event){
        int rating = (int) Math.ceil(ratingBar.getRating());
        ratingBar.setUpdateOnHover(false);

        //Set recipe attributes
        DbConnector.dbConnector.updateRecipeRating(rating, currentRecipe, this.userId);
        Platform.runLater(() ->{
            ratingBar.setRating(rating);

        });

        //Update recipe view
        currentRecipe.setUserRating(rating);
        ratingLabel.setText(String.format("Avg. Rating of %.1f/5 Over %d Ratings",
                currentRecipe.getRatingAvg(), currentRecipe.getRatingCount()));

    }

    /**
     * Reset the display rating for a single recipe
     * @param event - event
     */
    public void resetUserRating(Event event){
        float rating = currentRecipe.getUserRating() == 0 ? currentRecipe.getRatingAvg() : (float) currentRecipe.getUserRating();
        ratingBar.setRating(rating);
    }

    /**
     * Action to toggle whether a recipe is a user favorite
     * @param event - event
     */
    public void toggleFavorite(Event event){

        if (currentRecipe.toggleFavorite()){
            DbConnector.getDbConnector().setUserFavorite(currentRecipe, this.userId);
            toggleFavorite.setStyle("-fx-base: gold;");
        }
        else{
            DbConnector.getDbConnector().deleteUserFavorite(currentRecipe, this.userId);
            toggleFavorite.setStyle("");
        }
    }

    /**
     * Tag to save comments to a single recipe
     * @param event - trigger event
     */
    public void saveComments(Event event){
        currentRecipe.setComments(comments.getText());
        DbConnector.getDbConnector().setComments(currentRecipe, this.userId);
    }

    /**
     * Opens the create recipe modal
     * @param actionEvent - trigger event
     */
    public void openCreateModal(ActionEvent actionEvent) {

        Stage stage = new Stage();
        Parent root = null;
        try {
            FXMLLoader modalLoader = new FXMLLoader(CreateModalController.class.getResource("create-modal.fxml"));
            root = modalLoader.load();
            CreateModalController modalController = modalLoader.getController();
            modalController.setUser(userId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.setScene(new Scene(root));
        stage.setTitle("Create Recipe");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(allRecipesPagination.getScene().getWindow());
        stage.showAndWait();
    }

    /**
     * Opens the edit recipe modal
     * @param actionEvent - trigger event
     */
    public void openEditModal(ActionEvent actionEvent) {

        Stage stage = new Stage();
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(
                    EditModalController.class.getResource("edit-modal.fxml"));
            EditModalController editModalController = new EditModalController(this.currentRecipe.id);
            loader.setController(editModalController);
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.setScene(new Scene(root));
        stage.setTitle("Edit Recipe");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(allRecipesPagination.getScene().getWindow());
        stage.showAndWait();
        //update single recipe
        if (currentRecipe.id!=0) {updateSingleRecipe(currentRecipe.id);}
    }

    /**
     * Opens the delete modal for a single recipe
     * @param actionEvent - trigger event
     */
    public void openDeleteModal(ActionEvent actionEvent) {

        Stage stage = new Stage();
        Parent root = null;
        try {
            FXMLLoader loader2 = new FXMLLoader(
                    deleteModalController.class.getResource("Delete-view.fxml"));
            deleteModalController delete = new deleteModalController(this.currentRecipe.id);
            loader2.setController(delete);
            root = loader2.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.setScene(new Scene(root));
        stage.setTitle("Delete Recipe");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(allRecipesPagination.getScene().getWindow());
        stage.showAndWait();
    }
    /**
     * Builds a Vbox of all the recipes in the list in the order
     * they're added to the list
     */
    public ScrollPane buildScrollPaneOfRecipes(RecipeList recipeList){
        VBox vBox = new VBox();
        for (int i = 0; i < recipeList.size(); i++) {
            try{
                FXMLLoader cardLoader = new FXMLLoader(getClass().getResource("recipe-card.fxml"));
                AnchorPane cardRoot = cardLoader.load();
                RecipeCardController cardController = cardLoader.getController();
                cardController.setRecipe(recipeList.get(i));

                cardController.setParentController(this);

                vBox.getChildren().add(cardRoot);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        ScrollPane scrollPane = new ScrollPane(vBox);
        scrollPane.setStyle("-fx-background-color: transparent");
        return scrollPane;
    }

    /**
     * Returns a list of the possible time ranges to search over for the recipes
     * @return - an array list of time ranges
     */
    public ArrayList<String> getTimeRangeList(){
        ArrayList<String> result = new ArrayList<>();
        List<String> stringList = Arrays.asList("No Time Limit", "Less than 30 min", "Less than 45 min",
                "Less than 60 min","Less than 120 min", "Less than 180 min");
        result.addAll(stringList);
        return result;
    }

    /**
     * Returns an array list for the ranges of calories value that can be searched over
     * @return - an array list of maximum calories
     */
    public ArrayList<String> getCaloriesRangeList(){
        ArrayList<String> result = new ArrayList<>();
        List<String> integerList = Arrays.asList("No Calorie Limit", "Less than 100", "Less than 250",
                "Less than 500","Less than 1000", "Less than 1500");
        result.addAll(integerList);
        return result;
    }

    /**
     * Returns an array list for the ranges of ratings
     * @return - an array list of ratings
     */
    public ArrayList<String> getRatingList(){
        ArrayList<String> result = new ArrayList<>();
        List<String> integerList = Arrays.asList("No Rating Limit", "More Than 1 Star","More Than 2 Star","More Than 3 Star",
                "More Than 4 Star","More Than 5 Star");
        result.addAll(integerList);
        return result;
    }

    /**
     * Returns an array list of values to sort recipes by
     * @return - an array list of columsn to sort recipes by
     */
    public ArrayList<String> getOrderList(){
        ArrayList<String> result = new ArrayList<>();
        List<String> integerList = Arrays.asList("name","cookTime","totalTime", "calories", "serving");
        result.addAll(integerList);
        return result;
    }

    /**
     * Set the userId for the homepage
     * @param userId - userId of user logged in
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * Removes the userId, signing them out, and then redirects to the login screen
     * @param actionEvent - trigger event
     */
    public void signOut(ActionEvent actionEvent) {
        userId = -1;

        Parent root;

        try {
            root = FXMLLoader.load(getClass().getResource("login-view.fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Scene scene = new Scene(root, 1200, 800);
        Stage stage = (Stage) searchTextAllRecipes.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}