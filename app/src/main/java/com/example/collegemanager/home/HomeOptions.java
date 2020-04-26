package com.example.collegemanager.home;

// This is a class that defines each Option in the Home Window.
public class HomeOptions {
    int optionID;
    String optionTitle;
    int optionPicture;
    Home.ClickListener optionListener;

    public HomeOptions(int id, String title, int src, Home.ClickListener listener) {
        optionID = id;
        optionTitle = title;
        optionPicture = src;
        optionListener = listener;
    }

    public Home.ClickListener getOptionListener() {
        return optionListener;
    }
}