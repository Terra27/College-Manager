package com.example.collegemanager;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;

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
}
