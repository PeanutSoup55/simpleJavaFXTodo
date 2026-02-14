package com.example.allystodo.objects;

public class Note {
    private static final String NOTES_DIRECTORY = "notes";
    private String fileName;
    private String content;
    private boolean checked;

    public Note(String fileName, String content){
        this.fileName = fileName;
        this.content = content;
        this.checked = false;
        ensureNotesDirectoryExists();
    }

    public Note(String fileName, String content, boolean checked){
        this.fileName = fileName;
        this.content = content;
        this.checked = checked;
        ensureNotesDirectoryExists();
    }

    public Note(String fileName){
        this.fileName = fileName;
        this.content = "";
        this.checked = false;
        ensureNotesDirectoryExists();
    }

    public void ensureNotesDirectoryExists(){

    }

}
