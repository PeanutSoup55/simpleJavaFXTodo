package com.example.allystodo;

import com.example.allystodo.objects.Note;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    private ListView<String> activeNotesListView;
    private ListView<String> completedNotesListView;
    private TextArea contentArea;
    private TextField fileNameField;
    private CheckBox checkedCheckBox;
    private Note currentNote;
    private Label statusLabel;

    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("Todo Manager");
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(10));
        VBox leftPanel = createLeftPanel();

        VBox centerPanel = createCenterPanel();

        statusLabel = new Label("Ready");
        statusLabel.setStyle("-fx-padding: 5px; -fx-background-color: #f0f0f0;");

        mainLayout.setLeft(leftPanel);
        mainLayout.setCenter(centerPanel);
        mainLayout.setBottom(statusLabel);

        Scene scene = new Scene(mainLayout, 1000, 600);
        stage.setScene(scene);
        stage.show();
        refreshNoteList();
    }
}
