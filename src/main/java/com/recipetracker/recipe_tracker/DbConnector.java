package com.recipetracker.recipe_tracker;

import java.sql.*;
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

    // TODO: This will return something, just not sure on the object type...

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
        } catch (SQLException e){
            System.out.println(e);
        }

        return crs;
    }
}
