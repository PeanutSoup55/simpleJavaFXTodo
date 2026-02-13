module com.example.allystodo {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.allystodo to javafx.fxml;
    exports com.example.allystodo;
}