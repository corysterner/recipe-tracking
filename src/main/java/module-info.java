module com.recipetracker.recipe_tracker {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens com.recipetracker.recipe_tracker to javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;
    requires org.apache.commons.codec;
    requires java.sql.rowset;
    requires org.apache.commons.lang3;
    exports com.recipetracker.recipe_tracker;
}