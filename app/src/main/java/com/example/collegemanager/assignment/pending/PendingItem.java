package com.example.collegemanager.assignment.pending;

public class PendingItem {
    int pendingID;
    int pendingImage;
    String pendingTitle; // Limit to 100 characters
    String pendingDueDate;
    String pendingProfessor;

    public PendingItem(int id, int image, String title, String dueDate, String professor) {
        pendingID = id;
        pendingImage = image;
        pendingTitle = title;
        pendingDueDate = "Due Date: " + dueDate;
        pendingProfessor = professor;
    }
}
