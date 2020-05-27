package com.example.collegemanager.notices;

import android.content.Context;
import android.graphics.Typeface;
import android.media.Image;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.collegemanager.R;

import java.util.ArrayList;

import static com.example.collegemanager.Helper.changeDateToIndianFormat;
import static com.example.collegemanager.Helper.monthsSince;

public class NoticesAdapter extends ArrayAdapter<NoticeItem> {

    private Context activityContext;
    public NoticesAdapter(Context context, ArrayList<NoticeItem> noticesList ) {
        super( context, 0, noticesList );

        activityContext = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // There is no fixed order or number of times an AdapterView calls getView(),
        // so computation that must be performed only once should be specified after this check.
        if ( convertView == null ) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.notice_list, parent, false);

            NoticeItem notice = getItem(position);

            TextView noticeTitle = convertView.findViewById(R.id.noticeTitleText);
            String title = notice.noticeTitle;
            if ( title.length() > 36) {
                title = title.substring(0, 37) + "...";
            }
            noticeTitle.setText(title);

            String givenDate = changeDateToIndianFormat( notice.noticeDate );
            int months = monthsSince( givenDate );

            TextView noticeDate = convertView.findViewById(R.id.noticeDate);
            noticeDate.setText(givenDate);

            // New notice, black and bold
            if ( months < 1 ) {
                noticeTitle.setTextColor(activityContext.getResources().getColor(R.color.profileNameColor));
                noticeTitle.setTypeface(null, Typeface.BOLD);

                noticeDate.setTextColor(activityContext.getResources().getColor(R.color.profileNameColor));
                noticeDate.setTypeface(null, Typeface.BOLD);
            }
        }
        return convertView;
    }
}
