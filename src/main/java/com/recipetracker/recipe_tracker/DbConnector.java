package com.recipetracker.recipe_tracker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

public class DbConnector {

    // DB Constants
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_USER_ID = "recipe-tracker-mysql-user";
    private static final String DB_LOCATION = "jdbc:mysql://recipe-tracking.ddns.net:54/recipe-tracking";
    private static final String DB_PASSWORD = "1M^2POyWpIfDf@Bj45";

    // Private constructor so there's only one of these DbConnector objects
    // We only expect to need to connect to this one DB
    private DbConnector(){
        // TODO figure out if we need this call at all... not sure what it does
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    // External method for getting the singleton DbConnector
    public static DbConnector getDbConnector(){
        return dbConnector;
    }

    // Initialize the DbConnector
    static {
        dbConnector = new DbConnector();
    }

    // The main DbConnector
    public static DbConnector dbConnector;

    // TODO: Figure out if this should return something we check - a success code?
    public void createOrUpdateQuery(String queryString){
        try {
            Connection con = DriverManager.getConnection(DB_LOCATION, DB_USER_ID, DB_PASSWORD);
            Statement stmt = con.createStatement();
            stmt.execute(queryString);
            con.close();
        } catch (SQLException e){
            System.out.println(e);
        }
    }

    // TODO: This will return something, just not sure on the object type...
    public ArrayList<Object> selectQuery(String queryString){
        try {
            Connection con = DriverManager.getConnection(DB_LOCATION, DB_USER_ID, DB_PASSWORD);
            Statement stmt = con.createStatement();
            // TODO: This is probably a different call(s) that actually return a value
            ResultSet resultSet = stmt.executeQuery(queryString);
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columns = metaData.getColumnCount();
            ArrayList<Object> result = new ArrayList();
            int rows=0;
            while (resultSet.next() && rows < 200) {
               rows++;
                for (int ii=1; ii<= columns; ii++) {
                    result.add(resultSet.getObject(ii));
                }
            }
            resultSet.close();
            stmt.close();
            con.close();
            return result;
        } catch (SQLException e){
            System.out.println(e);
        }
       return null;
    }
    public ArrayList<Recipe> selectQueryShort(String queryString){
        try {
            Connection con = DriverManager.getConnection(DB_LOCATION, DB_USER_ID, DB_PASSWORD);
            Statement stmt = con.createStatement();
            ResultSet resultSet = stmt.executeQuery(queryString);
            ResultSetMetaData metaData = resultSet.getMetaData();
            ArrayList<Recipe> result = new ArrayList();
            int rows=0;
            Recipe rec = null;
            while (resultSet.next() && rows < 20) {
                rows++;
                rec = new Recipe(0,"","");
                rec.id=resultSet.getInt("recipeid");
                rec.name=resultSet.getString("name");
                rec.description=resultSet.getString("description");
                result.add(rec);
            }
            resultSet.close();
            stmt.close();
            con.close();
            return result;
        } catch (SQLException e){
            System.out.println(e);
        }
        return null;
    }

    public ArrayList<Recipe.Category> selectQueryCategory(){
        try {
            Connection con = DriverManager.getConnection(DB_LOCATION, DB_USER_ID, DB_PASSWORD);
            Statement stmt = con.createStatement();
            ResultSet resultSet = stmt.executeQuery("select id,value from categories");
            ResultSetMetaData metaData = resultSet.getMetaData();
            ArrayList<Recipe.Category> result = new ArrayList();
            Recipe.Category cat = null;
            while (resultSet.next() ) {

                cat = new Recipe.Category(0,"");
                cat.id=resultSet.getInt("id");
                cat.value=resultSet.getString("value");
                result.add(cat);
            }
            resultSet.close();
            stmt.close();
            con.close();
            return result;
        } catch (SQLException e){
            System.out.println(e);
        }
        return null;
    }

    public Recipe selectQueryFullRecipe(int recipeId){

        Recipe recipe = selectQueryRecipe(recipeId);
        recipe.setCategories(selectRecipeCategories(recipeId));
        recipe.setRating(selectRecipeRating(recipeId));

        return recipe;
    }

    public Recipe selectQueryRecipe(int recipeId){
        try {
            Connection con = DriverManager.getConnection(DB_LOCATION, DB_USER_ID, DB_PASSWORD);
            Statement stmt = con.createStatement();

            ResultSet resultSet = stmt.executeQuery("select * " +
                    "from recipes " +
                    "where recipeid = " + recipeId);

            resultSet.next();

            //Create a new recipe
            Recipe recipe = new Recipe(
                    recipeId,
                    resultSet.getString("name"),
                    resultSet.getString("description"),
                    resultSet.getString("DatePublished"),
                    resultSet.getString("IngredientAmount"),
                    resultSet.getString("Instructions"),
                    resultSet.getInt("PrepTime"),
                    resultSet.getInt("CookTime"),
                    resultSet.getInt("TotalTime"),
                    resultSet.getInt("Calories"),
                    resultSet.getInt("serving"),
                    resultSet.getString("size"),
                    resultSet.getInt("AuthorId"));

            //Cleanup
            resultSet.close();
            stmt.close();
            con.close();

            return recipe;

        } catch (SQLException e){
            System.out.println(e);
        }

        return null;
    }

    public List<Recipe.Category> selectRecipeCategories(int recipeId){
        try {
            Connection con = DriverManager.getConnection(DB_LOCATION, DB_USER_ID, DB_PASSWORD);
            Statement stmt = con.createStatement();

            //Categories query
            ResultSet resultSet = stmt.executeQuery("select value, id " +
                    "from recipecategory " +
                    "join categories " +
                    "on CategoryId = id " +
                    "where recipeid = " + recipeId);

            resultSet.next();

            List<Recipe.Category> recipeCategories = new ArrayList<Recipe.Category>();
            Recipe.Category cat;
            while (resultSet.next() ) {
                cat = new Recipe.Category(resultSet.getInt("id")
                        ,resultSet.getString("value"));
                recipeCategories.add(cat);
            }

            //Cleanup
            resultSet.close();
            stmt.close();
            con.close();

            return recipeCategories;

        } catch (SQLException e){
            System.out.println(e);
        }

        return null;

    }

    public Recipe.Rating selectRecipeRating(int recipeId){
        try {
            Connection con = DriverManager.getConnection(DB_LOCATION, DB_USER_ID, DB_PASSWORD);
            Statement stmt = con.createStatement();

           ResultSet resultSet = stmt.executeQuery("select AVG(Rating), COUNT(Rating) " +
                    "from ratings " +
                    "where recipeid = " + recipeId);

            resultSet.next();

            Recipe.Rating rating = new Recipe.Rating(
                    resultSet.getFloat("AVG(Rating)"),
                    resultSet.getInt("COUNT(Rating)"));

            //Cleanup
            resultSet.close();
            stmt.close();
            con.close();

            return rating;

        } catch (SQLException e){
            System.out.println(e);
        }
        return null;
    }
}

