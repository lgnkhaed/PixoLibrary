module com.pixo.bib.pixolibrary {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires com.google.gson;
    requires javafx.swing;
    requires java.sql;
    exports com.pixo.bib.pixolibrary.Controllers;

    // Open the package for reflection so FXML can access controllers

    // Export the main package and controllers
    exports com.pixo.bib.pixolibrary;
    opens com.pixo.bib.pixolibrary.Model.metaData to com.google.gson;
    opens com.pixo.bib.pixolibrary.Controllers to com.google.gson, javafx.fxml;
}