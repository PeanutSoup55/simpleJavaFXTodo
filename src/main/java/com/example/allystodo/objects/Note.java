package com.example.allystodo.objects;

import java.io.*;

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
        File directory = new File(NOTES_DIRECTORY);
        if (!directory.exists()){
            directory.mkdir();
        }
    }

    public boolean writeToFile(){
        try{
            String filePath = NOTES_DIRECTORY + File.separator + fileName;
            if (!fileName.endsWith(".txt")){
                filePath += ".txt";
            }
            FileWriter writer = new FileWriter(filePath);
            writer.write("CHECKED: " + checked + "\n");
            writer.write(content);
            writer.close();
            return true;
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean readFromFile(){
        try{
            String filePath = NOTES_DIRECTORY + File.separator + fileName;
            if (!fileName.endsWith(".txt")){
                filePath += ".txt";
            }
            File file = new File(filePath);
            if (!file.exists()){
                return false;
            }
            StringBuilder contentBuilder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine && line.startsWith("CHECKED:")) {
                    // Parse the checked status
                    this.checked = Boolean.parseBoolean(line.substring(8));
                    firstLine = false;
                } else {
                    contentBuilder.append(line).append("\n");
                    firstLine = false;
                }
            }
            reader.close();
            this.content = contentBuilder.toString();
            return true;
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteFile(){
        try{
            String filePath = NOTES_DIRECTORY + File.separator + fileName;
            if (!fileName.endsWith(".txt")){
                filePath += ".txt";
            }
            File file = new File(filePath);
            return file.delete();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

}
