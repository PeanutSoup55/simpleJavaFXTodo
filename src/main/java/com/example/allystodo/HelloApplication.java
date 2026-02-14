package com.example.allystodo;

import com.example.allystodo.objects.Note;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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

    private VBox createCenterPanel() {
        VBox centerPanel = new VBox(10);
        centerPanel.setPadding(new Insets(5));

        // File name input
        HBox fileNameBox = new HBox(10);
        Label fileLabel = new Label("File Name:");
        fileNameField = new TextField();
        fileNameField.setPromptText("Enter note name (without .txt)");
        HBox.setHgrow(fileNameField, Priority.ALWAYS);
        fileNameBox.getChildren().addAll(fileLabel, fileNameField);

        // Checkbox for completion status
        HBox checkboxBox = new HBox(10);
        checkedCheckBox = new CheckBox("Mark as completed");
        checkedCheckBox.setStyle("-fx-font-size: 13px;");
        checkboxBox.getChildren().add(checkedCheckBox);

        // Content area
        Label contentLabel = new Label("Content:");
        contentLabel.setStyle("-fx-font-weight: bold;");

        contentArea = new TextArea();
        contentArea.setPromptText("Enter your note content here...");
        contentArea.setWrapText(true);
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(5, 0, 0, 0));

        Button newButton = new Button("New Note");
        newButton.setOnAction(e -> createNewNote());

        Button saveButton = new Button("Save Note");
        saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        saveButton.setOnAction(e -> saveNote());

        Button deleteButton = new Button("Delete Note");
        deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        deleteButton.setOnAction(e -> deleteNote());

        Button clearButton = new Button("Clear");
        clearButton.setOnAction(e -> clearFields());

        buttonBox.getChildren().addAll(newButton, saveButton, deleteButton, clearButton);

        centerPanel.getChildren().addAll(fileNameBox, checkboxBox, contentLabel, contentArea, buttonBox);
        return centerPanel;
    }


    private void createNewNote() {
        clearFields();
        currentNote = null;
        updateStatus("Ready to create new note");
    }

    private void saveNote() {
        String fileName = fileNameField.getText().trim();
        String content = contentArea.getText();
        boolean checked = checkedCheckBox.isSelected();

        if (fileName.isEmpty()) {
            showAlert("Validation Error", "Please enter a file name");
            return;
        }

        currentNote = new Note(fileName, content, checked);
        if (currentNote.writeToFile()) {
            updateStatus("Saved: " + fileName);
            refreshNoteList();
            showInfo("Success", "Note saved successfully!");
        } else {
            showAlert("Error", "Failed to save note");
        }
    }

    private void deleteNote() {
        String fileName = fileNameField.getText().trim();

        if (fileName.isEmpty()) {
            showAlert("Validation Error", "Please select or enter a file name to delete");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Delete");
        confirmation.setHeaderText("Delete Note");
        confirmation.setContentText("Are you sure you want to delete '" + fileName + "'?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Note noteToDelete = new Note(fileName);
                if (noteToDelete.deleteFile()) {
                    updateStatus("Deleted: " + fileName);
                    clearFields();
                    refreshNoteList();
                    showInfo("Success", "Note deleted successfully!");
                } else {
                    showAlert("Error", "Failed to delete note");
                }
            }
        });
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
