package com.example.patrick.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Calendar;

public class GraphActivity extends AppCompatActivity {
    LineChart lineChart;
    int height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph_activity);
        //========================Draw graph=====================================
        drawLineGraph();
        lineChart.post(new Runnable() {
            @Override
            public void run() {
                height = lineChart.getHeight();
                setGradient();
                makeToast("" + height);
            }
        });
    }

    private void drawLineGraph() {
        setupLineChart();
        setupLineChartData();
        setGradient();
    }

    private void setGradient() {
        // Get the paint renderer to create the line shading.
        Paint paint = lineChart.getRenderer().getPaintRender();

        int [] colours = {Color.rgb(255, 255, 0),//Yellow
                          Color.rgb(255, 196, 0),
                          Color.rgb(255, 179, 0),
                          Color.rgb(255, 162, 0),
                          Color.rgb(255, 145, 0), //Orange
                          Color.rgb(0, 240, 255), //Blue-ish
                          Color.rgb(0, 208, 255),
                          Color.rgb(0, 144, 255),
                          Color.rgb(0, 72, 255),
                          Color.rgb(20, 74, 129)};
        //float [] positions = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        LinearGradient linGrad = new LinearGradient(0, 0, 0, height, colours,
                null, Shader.TileMode.REPEAT);
        //LinearGradient linGrad = new LinearGradient(0, 0, 0, 325, Color.rgb(20, 74, 129),
        //        Color.rgb(255, 145, 0), Shader.TileMode.MIRROR);
        paint.setShader(linGrad);

        // refresh the graph
        lineChart.invalidate();
    }

    private void setupLineChart() {
        lineChart = (LineChart) findViewById(R.id.chart);
        // disable the description
        lineChart.setDescription("");
        //Disable background grid
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getXAxis().setDrawGridLines(false);
        //lineChart.animateXY(1500, 1500);
        // disable the legend
        Legend legend = lineChart.getLegend();
        legend.setEnabled(false);
        //Set up x-axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setAvoidFirstLastClipping(true);
        //Set up y-axis on the left
        YAxis yAxisLeft = lineChart.getAxisLeft();
        yAxisLeft.setEnabled(true);
        yAxisLeft.setAxisMinValue(0);
        yAxisLeft.setAxisMaxValue(11);
        //Remove y-axis on the right
        lineChart.getAxisRight().setEnabled(false);
        lineChart.setScaleYEnabled(false);
    }

    private void setupLineChartData() {
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
        Calendar tomorrow = (Calendar) cal.clone();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);

        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.MONTH, 0);
        int i = 0;
        boolean hasData = false;
        while (!cal.equals(tomorrow)) {
            int rating = dbh.getRating(cal);
            String date = "" + cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) +
                    1) + "/" + cal.get(Calendar.YEAR);
            if (rating != -1) {
                entries.add(new Entry(rating, i));
                labels.add(date);
                hasData = true;
                i++;
            }
            //Only consider missing entries after first valid entry
            else if(hasData == true) {
                //Add a label for missing entries
                labels.add(date);
                i++;
            }
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        //Add the valid entries to the graph
        LineDataSet dataset = new LineDataSet(entries, "");
        dataset.setLineWidth(5f);
        dataset.setCubicIntensity(0.1f);

        dataset.setDrawHorizontalHighlightIndicator(false);
        dataset.setCircleRadius(5f);

        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.graph_background);
        dataset.setFillDrawable(drawable);
        //Both lines together are the problem
        dataset.setDrawFilled(true);
        //dataset.setDrawCubic(true);
        LineData data = new LineData(labels, dataset);
        lineChart.setData(data); // set the data and list of lables into chart
    }

    private void makeToast(String text) {
        Context context  = getApplicationContext();
        int     duration = Toast.LENGTH_LONG;
        Toast   toast    = Toast.makeText(context, text, duration);
        toast.show();
    }
}