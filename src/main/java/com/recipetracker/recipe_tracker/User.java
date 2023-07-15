package com.recipetracker.recipe_tracker;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;

public class User {
    int userId;

    public void User(int userId){
        this.userId = userId;
    }

    public static String getUserSaltStringFromEmail(String email){
        String queryString = "SELECT substring_index(PasswordHash, \":\", 1) saltString FROM users\n" +
                "WHERE Email = '" + email + "'";
        CachedRowSet result = DbConnector.getDbConnector().selectQuery(queryString);

        String salt;

        try {
            result.next();
            salt = result.getString("saltString");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return salt;
    }
}
