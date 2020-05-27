package com.example.collegemanager.notices;

public class NoticeItem {

    String noticeTitle;
    String noticeDate;
    int noticeBranch;
    String noticeURL;
    int noticeSize;
    int sizeUnit;

    public NoticeItem( String title, String date, int branch, String url, int size, int unit ) {
        noticeTitle = title;
        noticeDate = date;
        noticeBranch = branch;
        noticeURL = url;
        noticeSize = size;
        sizeUnit = unit;
    }
}
