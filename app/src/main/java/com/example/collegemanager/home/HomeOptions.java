package com.example.collegemanager.home;

// This is a class that defines each Option in the Home Window.
public class HomeOptions {
    int optionID;
    String optionTitle;
    int optionPicture;
    public HomeOptions(int id, String title, int src) {
        optionID = id;
        optionTitle = title;
        optionPicture = src;
    }
}