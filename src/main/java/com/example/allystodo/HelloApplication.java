package com.example.allystodo;

import com.example.allystodo.objects.Note;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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

    private VBox createLeftPanel() {
        HBox leftPanel = new HBox(10);
        leftPanel.setPadding(new Insets(5));
        leftPanel.setPrefWidth(450);

        VBox activeBox = new VBox(10);
        activeBox.setPadding(new Insets(5));
        activeBox.setPrefWidth(220);

        Label activeLabel = new Label("Active Todos");
        activeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        activeNotesListView = new ListView<>();
        activeNotesListView.setPrefHeight(400);

        activeNotesListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        completedNotesListView.getSelectionModel().clearSelection();
                        loadNote(newValue);
                    }
                }
        );

        activeBox.getChildren().addAll(activeLabel, activeNotesListView);

        VBox completedBox = new VBox(10);
        completedBox.setPadding(new Insets(5));
        completedBox.setPrefWidth(220);

        Label completedLabel = new Label("Completed Todos");
        completedLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #4CAF50;");

        completedNotesListView = new ListView<>();
        completedNotesListView.setPrefHeight(400);

        completedNotesListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        activeNotesListView.getSelectionModel().clearSelection();
                        loadNote(newValue);
                    }
                }
        );

        completedBox.getChildren().addAll(completedLabel, completedNotesListView);
        Button refreshButton = new Button("Refresh Lists");
        refreshButton.setMaxWidth(Double.MAX_VALUE);
        refreshButton.setOnAction(e -> refreshNoteList());

        VBox leftContainer = new VBox(10);
        HBox listsContainer = new HBox(10);
        listsContainer.getChildren().addAll(activeBox, completedBox);
        leftContainer.getChildren().addAll(listsContainer, refreshButton);

        return leftContainer;
    }



    private void refreshNoteList() {
        activeNotesListView.getItems().clear();
        completedNotesListView.getItems().clear();

        for (String fileName : Note.getAllNotes()) {
            Note note = new Note(fileName);
            if (note.readFromFile()) {
                if (note.isChecked()) {
                    completedNotesListView.getItems().add(fileName);
                } else {
                    activeNotesListView.getItems().add(fileName);
                }
            }
        }

        updateStatus("Note lists refreshed - " +
                activeNotesListView.getItems().size() + " active, " +
                completedNotesListView.getItems().size() + " completed");
    }

    private void loadNote(String fileName) {
        currentNote = new Note(fileName);
        if (currentNote.readFromFile()) {
            fileNameField.setText(fileName);
            contentArea.setText(currentNote.getContent());
            checkedCheckBox.setSelected(currentNote.isChecked());
            updateStatus("Loaded: " + fileName);
        } else {
            showAlert("Error", "Failed to load note: " + fileName);
        }
    }

    private void clearFields() {
        fileNameField.clear();
        contentArea.clear();
        checkedCheckBox.setSelected(false);
        activeNotesListView.getSelectionModel().clearSelection();
        completedNotesListView.getSelectionModel().clearSelection();
    }
    private void updateStatus(String message) {
        statusLabel.setText(message);
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



}
