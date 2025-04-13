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
    requires java.desktop;

    // Open the package for reflection so FXML can access controllers
    opens com.pixo.bib.pixolibrary.Controllers to javafx.fxml;

    // Export the main package and controllers
    exports com.pixo.bib.pixolibrary;
    exports com.pixo.bib.pixolibrary.Controllers;
}