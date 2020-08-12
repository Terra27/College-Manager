package com.example.collegemanager;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Helper {
    public static String getFileNameFromUri(Uri uri, ContentResolver resolver) {
        String result = null;

        // File is managed by a Content Provider
        if (uri.getScheme().equals("content")) {
            // Query the content provider to get the value of the COLUMN_DISPLAY_NAME column
            Cursor cursor = resolver.query(uri, new String[] { DocumentsContract.Document.COLUMN_DISPLAY_NAME }, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(0);
                }
            } finally {
                cursor.close();
            }
        }

        // File not managed by a Content provider
        if (result == null) {
            result = uri.getPath();
            // The MIME type included at the end of the path includes file name after the last occurrence of /
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }

        return result;
    }

    // converts yyyy-MM-dd to dd-MM-yyyy
    public static String changeDateToIndianFormat( String givenDate ) {

        String givenYear = givenDate.substring( 0, givenDate.indexOf('-', 0) );
        String givenMonth = givenDate.substring( givenDate.indexOf('-', 0) + 1, givenDate.lastIndexOf('-') );
        String givenDay = givenDate.substring( givenDate.lastIndexOf('-') + 1 );

        return givenDay +"-"+ givenMonth +"-"+ givenYear;
    }

    // takes date in Indian format
    public static int monthsSince( String givenDate ) {

        String pattern = "dd-MM-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String currentDate = simpleDateFormat.format(new Date());

        int givenDay = Integer.parseInt( givenDate.substring( 0, givenDate.indexOf('-', 0) ) );
        int givenMonth = Integer.parseInt( givenDate.substring( givenDate.indexOf('-', 0) + 1, givenDate.lastIndexOf('-') ) );
        int givenYear = Integer.parseInt( givenDate.substring( givenDate.lastIndexOf('-') + 1 ) );

        int currentDay = Integer.parseInt( currentDate.substring( 0, currentDate.indexOf('-', 0) ) );
        int currentMonth = Integer.parseInt( currentDate.substring( currentDate.indexOf('-', 0) + 1, currentDate.lastIndexOf('-') ) );
        int currentYear = Integer.parseInt( currentDate.substring( givenDate.lastIndexOf('-') + 1 ) );

        int yearDiff = currentYear - givenYear; // >= 0
        int monthDiff = currentMonth - givenMonth;
        int dayDiff = currentDay - givenDay;
        int months;
        if ( yearDiff == 0 )
            months = monthDiff;
        else
            months = monthDiff + (yearDiff * 12);

        if ( dayDiff < 0 ) // If today is 26 May 2020, then it returns > 0 months for all dates on and before 26 April 2020, and 0 afterwards
            months--;

        return months;
    }
}
