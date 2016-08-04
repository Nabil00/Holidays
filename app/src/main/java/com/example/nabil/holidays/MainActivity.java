package com.example.nabil.holidays;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {



    private String URL;
    private static List<Holiday> holidayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private HolidayAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //        set default values of all attr if it's never ever called before
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new HolidayAdapter(holidayList);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Holiday holiday = holidayList.get(position);
                Intent i = new Intent(MainActivity.this,Details.class);
                i.putExtra("position",position);
                startActivity(i);
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));

        prepareURL();
        Log.d("url",URL);
        prepareHolidaysData();

    }

    private void prepareHolidaysData() {

        Ion.with(this)
//                .load("https://holidayapi.com/v1/holidays?key=24f4c07f-fe2e-4828-98b0-cf7b6381d626&country=US&year=2016&month=1")
                .load(URL)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        String msg="";
                        try {
                            JsonArray jarray = result.getAsJsonArray("holidays");
                            if (e != null) {
                                msg = "Error occur";
                            } else if (jarray.size() == 0) {
                                msg = "Empty JSON";
                            } else {
                                for (int i = 0; i < jarray.size(); i++) {
                                    Holiday holiday = new Holiday();
                                    holiday.setName(jarray.get(i).getAsJsonObject().get("name").toString());
                                    holiday.setDate(jarray.get(i).getAsJsonObject().get("date").toString());
                                    holiday.setObserved(jarray.get(i).getAsJsonObject().get("observed").toString());
                                    holiday.setPub(jarray.get(i).getAsJsonObject().get("public").toString());

                                    holidayList.add(holiday);
                                }
                                msg = "done";
                                mAdapter.notifyDataSetChanged();
                            }
                        }catch (Exception e1){
                            msg="INTERNET ERROR";
                        }
                        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                });


    }

    public void setting(View view) {
        Intent i = new Intent(MainActivity.this,Sett.class);
        startActivity(i);
    }

    public void search(View view) {
        prepareURL();
        holidayList.clear();
        prepareHolidaysData();
    }

    private void prepareURL() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String country;
        int year,month,day;
        boolean publi,pre,up ;
        country = preferences.getString("country","IT");
        try {
            year = Integer.parseInt(preferences.getString("year", "2019"));
        }catch (Exception e) {
            year = 2016 ;
        }
        try {
            month = Integer.parseInt(preferences.getString("month","13"));
        }catch (Exception e) {
            month= 2 ;
        }
        try {
            day = Integer.parseInt(preferences.getString("day","32"));
        }catch (Exception e) {
            day = 0 ;
        }
        publi = preferences.getBoolean("public",false);
        pre = preferences.getBoolean("pre",false);
        up = preferences.getBoolean("up",true);

        boolean mon = false , d = false ;
        URL = "https://holidayapi.com/v1/holidays?key=24f4c07f-fe2e-4828-98b0-cf7b6381d626&country="+country+"&year="+year;
        if (month >=1 && month < 12) {
            URL = URL + "&month=" + month;
            mon = true;
        }
        if (day >=1 && day < 31) {
            URL = URL + "&day=" + day;
            d = true;
        }
        if (publi == true)
            URL = URL + "&public=true";
        if (mon==true && d == true && pre == true)
            URL = URL + "&previous=true";
        if (mon==true && d == true && up == true)
            URL = URL + "&upcoming=true";
    }

    public interface ClickListener {
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }
    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private MainActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final MainActivity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.pref:
            {
                Intent intent = new Intent(this, Sett.class);
                startActivity(intent);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public static List<Holiday> getHolidayList() {
        return holidayList;
    }

    private void testUpload () {
        int i = 1+1 ;
    }
}
