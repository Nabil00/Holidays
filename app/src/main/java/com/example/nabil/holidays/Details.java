package com.example.nabil.holidays;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Details extends AppCompatActivity {


    TextView name , date ,obs , publ ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);

        Intent i = getIntent();
        int pos = i.getIntExtra("position" , 0);

        List<Holiday> holidayList = MainActivity.getHolidayList();
        name = (TextView) findViewById(R.id.name);
        date = (TextView) findViewById(R.id.date);
        obs = (TextView) findViewById(R.id.obs);
        publ = (TextView) findViewById(R.id.pub);

        name.setText(holidayList.get(pos).getName());
        date.setText(holidayList.get(pos).getDate());
        obs.setText(holidayList.get(pos).getObserved());
        publ.setText(holidayList.get(pos).getPub());

    }
}
