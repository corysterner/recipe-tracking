package com.recipetracker.recipe_tracker;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.sql.*;

public class CreateModalController {
    public Button saveButton;

    public void saveAndCloseModal(ActionEvent actionEvent) {

        class MysqlCon{
            public static void query(){
                try{
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con=DriverManager.getConnection(
                            "jdbc:mysql://recipe-tracking.ddns.net:54/recipe-tracking","recipe-tracker-mysql-user","1M^2POyWpIfDf@Bj45");
                    System.out.println("Got here");
                    Statement stmt=con.createStatement();
                    stmt.execute("insert into test_table (string) values ('test')");
                    con.close();
                }catch(Exception e){ System.out.println(e);}
            }
        }

        MysqlCon.query();

        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}
