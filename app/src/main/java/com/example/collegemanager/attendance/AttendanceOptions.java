package com.example.collegemanager.attendance;

public class AttendanceOptions {

    String subCode;
    int totalClass;
    int present;

    public AttendanceOptions(String subjectCode , int attendedClass, int totalClassHeld){

        subCode = subjectCode;
        totalClass = totalClassHeld;
        present = attendedClass;
    }
}
