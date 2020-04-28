package com.example.collegemanager.assignment.pending;

public class PendingItem {
    int pendingID;
    int pendingImage;
    String pendingTitle; // Limit to 100 characters
    String pendingDueDate;
    String pendingProfessor;
    int pendingFileSize;
    String pendingFileName;
    int pendingClassID;

    public PendingItem(int id, int image, String title, String dueDate, String professor, int filesize, String filename, int classid ) {
        pendingID = id;
        pendingImage = image;
        pendingTitle = title;
        pendingDueDate = "Due Date: " + dueDate;
        pendingProfessor = professor;
        pendingFileSize = filesize;
        pendingFileName = filename;
        pendingClassID = classid;
    }
}
