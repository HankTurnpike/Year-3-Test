package com.example.patrick.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

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

        //***********************************************//
        //                                               //
        //                                               //
        // Get start date of app from shared preferences //
        //                                               //
        //                                               //
        //***********************************************//

        Calendar cal = Calendar.getInstance();
        //Get the current day, days after should not have a rating
        //Clone is necessary, as times may be slightly different otherwise
        //   and therefore can never be equal, (for the while loop condition)
        Calendar currentDay = (Calendar) cal.clone();

        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.MONTH, 0);

        int i = 0;
        while (!cal.equals(currentDay)) {
            int rating = dbh.getRating(cal);
            if (rating != -1) {
                entries.add(new Entry(rating, i));
                labels.add(cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get
                        (Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR));
                i++;
            }
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        LineDataSet dataset = new LineDataSet(entries, "# of Calls");
        LineData data = new LineData(labels, dataset);
        lineChart.setData(data); // set the data and list of lables into chart
        lineChart.setDescription("Mood Graph");  // set the description
        dataset.setDrawCubic(true);
        dataset.setDrawFilled(true);
        lineChart.invalidate();
    }
}
