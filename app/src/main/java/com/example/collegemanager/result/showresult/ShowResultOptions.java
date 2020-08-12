package com.example.collegemanager.result.showresult;

public class ShowResultOptions {

    String subjectCode ;
    String extMarks;
    String sessionalMarks;
    String grade;
    String credit ;

    public ShowResultOptions(String subcode , String external , String sessional , String gradeObt, String creditObt){

        subjectCode = subcode ;
        extMarks = external ;
        sessionalMarks = sessional ;
        grade = gradeObt ;
        credit = creditObt ;
    }
}
