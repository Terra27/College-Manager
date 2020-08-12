package com.example.collegemanager.assignment;

public class AssignmentItem {
    public int assignmentID;

    int assignmentImage;
    String assignmentTitle; // Limit to 100 characters
    String assignmentDueDate;
    public String assignmentProfessor;

    public int assignmentFileSize;
    public String assignmentFileName;

    int assignmentClassID;

    public AssignmentItem(int id, int image, String title, String dueDate, String professor, int filesize, String filename, int classid ) {

        assignmentID = id;

        assignmentImage = image;
        assignmentTitle = title;
        assignmentDueDate = dueDate;
        assignmentProfessor = professor;

        assignmentFileSize = filesize;
        assignmentFileName = filename;

        assignmentClassID = classid;
    }
}
