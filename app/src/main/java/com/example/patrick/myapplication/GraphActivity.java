package com.example.patrick.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.Calendar;

public class GraphActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph_activity);
        LineChart lineChart = (LineChart) findViewById(R.id.chart);
        DataBaseHelper dbh = new DataBaseHelper(this);
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        CalendarDay day;
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int myMonth=cal.get(Calendar.MONTH);
        int i = 0;
        while (myMonth == cal.get(Calendar.MONTH)) {
            Rating rating = dbh.getRow(cal);
            if (rating != null) {
                entries.add(new Entry(rating.getRating(), i));
                labels.add(rating.dateString());
                i++;
            }
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        LineDataSet dataset = new LineDataSet(entries, "# of Calls");
        LineData data = new LineData(labels, dataset);
        lineChart.setData(data); // set the data and list of lables into chart
        lineChart.setDescription("Description");  // set the description
        dataset.setDrawCubic(true);
        dataset.setDrawFilled(true);
        lineChart.invalidate();
    }
}
