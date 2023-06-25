package com.recipetracker.recipe_tracker;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeController {
    public TextField searchTextAllRecipes;
    public ComboBox categoryFilter;
    public ComboBox ratingFilter;
    public ComboBox timeFilter;
    public ComboBox caloriesFilter;
    public Pagination allRecipesPagination;

    public ScrollPane getPages(int pageIndex){
        VBox vBox = new VBox();
        for(int i = pageIndex; i < pageIndex + 100; i++){

            try{
                FXMLLoader cardLoader = new FXMLLoader(getClass().getResource("recipe-card.fxml"));
                AnchorPane cardRoot = cardLoader.load();
                RecipeCardController cardController = cardLoader.getController();

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
    public void searchAllRecipes(ActionEvent actionEvent) {
        allRecipesPagination.setPageFactory(this::getPages);
    }

    public void getMyRecipes(Event event) {
    }

    public void getMyFavorites(Event event) {
    }

    public void updateSingleRecipe(int recipeId) {
    }

    public void openCreateModal(ActionEvent actionEvent) {

        Stage stage = new Stage();
        Parent root = null;
        try {
            root = FXMLLoader.load(
                    CreateModalController.class.getResource("create-modal.fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.setScene(new Scene(root));
        stage.setTitle("Create Recipe");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(allRecipesPagination.getScene().getWindow());
        stage.showAndWait();
    }
}