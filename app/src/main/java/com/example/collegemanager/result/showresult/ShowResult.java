package com.example.collegemanager.result.showresult;

import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.collegemanager.R;

import java.util.ArrayList;

public class ShowResult extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_result);

        ArrayList<ShowResultOptions> resultOption = new ArrayList<ShowResultOptions>();

        ShowResultAdapter resultAdapter = new ShowResultAdapter(this, resultOption);

        resultAdapter.add(new ShowResultOptions("Subject Code", "External Marks","Sessional Marks","Grade","Obt Credit" ));
        resultAdapter.add(new ShowResultOptions("RCS-701", "60","28","A","4" ));
        resultAdapter.add(new ShowResultOptions("RCS-701", "60","28","A","4" ));
        resultAdapter.add(new ShowResultOptions("RCS-701", "60","28","A","4" ));
        resultAdapter.add(new ShowResultOptions("RCS-701", "60","28","A","4" ));
        resultAdapter.add(new ShowResultOptions("RCS-701", "60","28","A","4" ));
        resultAdapter.add(new ShowResultOptions("RCS-701", "60","28","A","4" ));
        resultAdapter.add(new ShowResultOptions("RCS-701", "60","28","A","4" ));
        resultAdapter.add(new ShowResultOptions("RCS-701", "60","28","A","4" ));
        resultAdapter.add(new ShowResultOptions("RCS-701", "60","28","A","4" ));
        resultAdapter.add(new ShowResultOptions("RCS-701", "60","28","A","4" ));

        ListView showResultList = (ListView)findViewById(R.id.showResultList);
        showResultList.setAdapter(resultAdapter);


    }
}
