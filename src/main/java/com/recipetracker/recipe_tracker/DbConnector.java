package com.recipetracker.recipe_tracker;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.rowset.*;

public class DbConnector {

    // DB Constants
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_USER_ID = "recipe-tracker-mysql-user";
    private static final String DB_LOCATION = "jdbc:mysql://recipe-tracking.ddns.net:54/recipe-tracking";
    private static final String DB_PASSWORD = "1M^2POyWpIfDf@Bj45";

    // Private constructor so there's only one of these DbConnector objects
    // We only expect to need to connect to this one DB
    private DbConnector(){
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
  
    public ArrayList<Recipe> selectQueryShort(String queryString){
        try {
            Connection con = DriverManager.getConnection(DB_LOCATION, DB_USER_ID, DB_PASSWORD);
            Statement stmt = con.createStatement();
            ResultSet resultSet = stmt.executeQuery(queryString);
            ResultSetMetaData metaData = resultSet.getMetaData();
            ArrayList<Recipe> result = new ArrayList();
            int rows=0;
            Recipe rec = null;
            while (resultSet.next() && rows < 100) {
                rows++;
                rec = new Recipe(0,"","","");
               try {rec.id=resultSet.getInt("recipeid");} catch (SQLException e){}
                try {rec.name=resultSet.getString("name");} catch (SQLException e){}
                try {rec.prepTimeMinutes=resultSet.getInt("prepTime");} catch (SQLException e){}
                try {rec.cookTimeMinutes=resultSet.getInt("cookTime");} catch (SQLException e){}
                try {rec.calories=resultSet.getInt("calories");} catch (SQLException e){}
                try {rec.description=resultSet.getString("description");} catch (SQLException e){}
                try {rec.instructions=resultSet.getString("instructions");} catch (SQLException e){}
                try {rec.ingredients=resultSet.getString("ingredients");} catch (SQLException e){}
                try {rec.serving=resultSet.getInt("serving");} catch (SQLException e){}
                try {rec.servingSize=resultSet.getString("size");} catch (SQLException e){}
                result.add(rec);
            }
          
            //Cleanup
            resultSet.close();
            stmt.close();
            con.close();
            return result;
          
        } catch (SQLException e){
            System.out.println(e);
        }
      
        return null;
    }

    /**
     * Select query.
     *
     * Returns a CachedRowSet (since the original ResultSet will be lost after the
     * connection is closed).
     *
     * @param queryString
     * @return
     */
    public CachedRowSet selectQuery(String queryString){
        ResultSet result = null;
        CachedRowSet crs = null;
  
        try {
            Connection con = DriverManager.getConnection(DB_LOCATION, DB_USER_ID, DB_PASSWORD);
            Statement stmt = con.createStatement();
            // TODO: This is probably a different call(s) that actually return a value
            result = stmt.executeQuery(queryString);

            crs = RowSetProvider.newFactory().createCachedRowSet();
            crs.populate(result);
            
            //Cleanup
            con.close();
          
        } catch (SQLException e){
            System.out.println(e);
        }

        /*
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        Map<String, Object> row = null;

        try {
            ResultSetMetaData metaData = result.getMetaData();
            Integer columnCount = metaData.getColumnCount();

            while (result.next()) {
                row = new HashMap<String, Object>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), result.getObject(i));
                }
                resultList.add(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        */

        return crs;
    }

    /**
     * Similar to createOrUpdateQuery, but returns the Id(s) of the records inserted.
     * @param queryString
     * @return CachedRowSet of the generated keys returned from the query
     */
    public CachedRowSet createQuery(String queryString) {

        ResultSet result;
        CachedRowSet crs = null;

        try {
            Connection con = DriverManager.getConnection(DB_LOCATION, DB_USER_ID, DB_PASSWORD);
            Statement stmt = con.createStatement();

            // Actually return the generated keys with the 2nd parameter set
            stmt.execute(queryString, Statement.RETURN_GENERATED_KEYS);

            result = stmt.getGeneratedKeys();

            crs = RowSetProvider.newFactory().createCachedRowSet();
            crs.populate(result);

            con.close();
        } catch (SQLException e) {
            System.out.println(e);
        }

        return crs;
    }
    public ArrayList<Recipe.Category> selectQueryCategory(){
        try {
            Connection con = DriverManager.getConnection(DB_LOCATION, DB_USER_ID, DB_PASSWORD);
            Statement stmt = con.createStatement();
            ResultSet resultSet = stmt.executeQuery("select id,value from categories");
            ResultSetMetaData metaData = resultSet.getMetaData();
            ArrayList<Recipe.Category> result1 = new ArrayList();
            Recipe.Category cat = null;
            while (resultSet.next() ) {

                cat = new Recipe.Category(0,"");
                cat.id=resultSet.getInt("id");
                cat.value=resultSet.getString("value");
                result1.add(cat);
            }
            resultSet.close();
            stmt.close();
            con.close();
            return result1;
        } catch (SQLException e){
            System.out.println(e);
        }
        return null;
    }
    public ArrayList<Recipe.Category> selectQueryCategory(int recipeId){
        try {
            Connection con = DriverManager.getConnection(DB_LOCATION, DB_USER_ID, DB_PASSWORD);
            Statement stmt = con.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT c.id, c.value FROM recipeCategory rc JOIN categories c ON rc.categoryId = c.id WHERE rc.recipeId ="+recipeId);
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

    public Recipe selectQueryFullRecipe(int recipeId, int userId){
        try {
            Connection con = DriverManager.getConnection(DB_LOCATION, DB_USER_ID, DB_PASSWORD);
            Statement stmt = con.createStatement();

            ResultSet resultSet = stmt.executeQuery(
                    "call getFullRecipe(" + recipeId + ", " + userId + ");"
            );

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

            //Add favorite
            recipe.setFavorite(resultSet.getInt("FavoriteId") > 0);

            //Add rating
            Recipe.Rating rating = new Recipe.Rating(
                    resultSet.getFloat("averageRating"),
                    resultSet.getInt("ratingCount"),
                    resultSet.getInt("userRating"));
            recipe.setRating(rating);

            //Add comments
            recipe.setComments(resultSet.getString("Comment"));

            //Add categories
            List<Recipe.Category> recipeCategories = new ArrayList<Recipe.Category>();
            Recipe.Category cat;

            // Need to add the first category from the current row
            cat = new Recipe.Category(resultSet.getInt("id")
                    ,resultSet.getString("value"));
            recipeCategories.add(cat);

            // Add any remaining categories
            while (resultSet.next() ) {
                cat = new Recipe.Category(resultSet.getInt("id")
                        ,resultSet.getString("value"));
                recipeCategories.add(cat);
            }
            recipe.setCategories(recipeCategories);

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
    public void updateRecipeRating(int rating, Recipe recipe, int userId){
        String queryString = "call setUserRating(" + rating + "," + recipe.id + "," + userId + ");";
        CachedRowSet result = selectQuery(queryString);

        if (result == null) return;

        try {
            result.next();
            recipe.setAverageRating(result.getFloat("averageRating"));
            recipe.setRatingCount(result.getInt("ratingCount"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setUserFavorite(Recipe recipe, int userId){
        String queryString = "call setUserFavorite(" + recipe.id + "," + userId + ");";
        createOrUpdateQuery(queryString);

        recipe.isFavorite = true;
    }

    public void deleteUserFavorite(Recipe recipe, int userId){
        String queryString = "call deleteUserFavorite(" + recipe.id + "," + userId + ");";
        createOrUpdateQuery(queryString);

        recipe.isFavorite = false;
    }
    public void setComments(Recipe recipe, int userId){
        String queryString = "call setComments(\"" + recipe.comments + "\"," + recipe.id + "," + userId + ");";
        createOrUpdateQuery(queryString);
    }

}

