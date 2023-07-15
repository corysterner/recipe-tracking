package com.recipetracker.recipe_tracker;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.sql.rowset.CachedRowSet;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.SQLException;

public class User {
    int userId;

    public void User(int userId){
        this.userId = userId;
    }

    public static String getUserSaltStringFromEmail(String email){
        String queryString = "SELECT Salt FROM users\n" +
                "WHERE Email = '" + email + "'";
        CachedRowSet result = DbConnector.getDbConnector().selectQuery(queryString);

        String salt;

        try {
            result.next();
            salt = result.getString("Salt");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return salt;
    }

    public static String hashPassword(String password, byte[] salt) {

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 10000, 512);
        SecretKeyFactory factory = null;
        try {
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        byte[] hash = new byte[0];

        try {
            hash = factory.generateSecret(spec).getEncoded();
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }

        String hashString = Hex.encodeHexString(hash);

        // Return string
        return hashString;
    }
}
