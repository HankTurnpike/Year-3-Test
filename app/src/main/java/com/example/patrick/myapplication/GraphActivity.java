package com.example.patrick.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;

public class GraphActivity extends AppCompatActivity {
    TextView dateTitle, notesTitle, notesSummary, goodThingsTitle, goodThingsSummary;
    ImageView imageView;
    Button button;

    DataBaseHelper dbh;
    LineChart lineChart;
    int height;
    ArrayList<String> labels;
    int year, month, day;

    private void setAlarmNotification() {
        Intent intent = new Intent(this, AlarmReceiver.class);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 3);
        calendar.set(Calendar.MINUTE, 8);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        //calendar.set(Calendar.AM_PM, Calendar.AM);
        Log.d("alarm", "");
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                1000 * 60, pendingIntent);
        makeToast("Alarm Set");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph_activity);

        //Setup Notifications
        SharedPreferences preferences   = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        int runs = preferences.getInt("NumberOfLaunches", 1);

        if(runs < 1000){
            setAlarmNotification();
            editor.putInt("NumberOfLaunches", ++runs).commit();
        }
        else {

        }


        //noinspection ConstantConditions
        getSupportActionBar().setTitle("Graph");
        dateTitle = (TextView) findViewById(R.id.textView_summary_graph);
        notesTitle = (TextView) findViewById(R.id.textView_notes_graph_title);
        notesSummary = (TextView) findViewById(R.id.textView_notes_graph);
        goodThingsTitle = (TextView) findViewById(R.id.textView_good_things_graph_title);
        goodThingsSummary = (TextView) findViewById(R.id.textView_good_things_graph);
        imageView = (ImageView) findViewById(R.id.imageView_graph);
        button = (Button) findViewById(R.id.button_graph);
        dbh = new DataBaseHelper(this);
        hideSummary();
        //========================Draw graph=====================================
        drawLineGraph();
        //Used to detect the height of the linechart
        //The linechart must be drawn for the linear gradient applied to the graph
        lineChart.post(new Runnable() {
            @Override
            public void run() {
                height = lineChart.getHeight();
                //Set up the gradient for the graph when the height of the linechart is detected
                setGradient();
            }
        });

        //Listen for clicks on the graph, launches a Date screen activity for the particular entry
        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int dataSetIndex, Highlight highlight) {
                //Get the label to calculate what page to display
                int labelIndex = highlight.getXIndex();
                String[] temp = labels.get(labelIndex).split("/");
                //Reverse the order of the date to year, month, day and parse
                // to integer
                year  = Integer.parseInt(temp[2]);
                month = Integer.parseInt(temp[1]) - 1;
                day   = Integer.parseInt(temp[0]);

                displaySummary(year, month, day);
            }

            @Override
            public void onNothingSelected() {
                hideSummary();
            }
        });
    }

    public void openEntry(View view) {
        Intent intent = new Intent(this, DateScreen.class);
        int[] date = {year, month, day};
        intent.putExtra(CalendarScreen.DATE, date);
        startActivity(intent);
    }

    private void hideSummary() {
        dateTitle.setVisibility(View.GONE);
        notesTitle.setVisibility(View.GONE);
        notesSummary.setVisibility(View.GONE);
        goodThingsTitle.setVisibility(View.GONE);
        goodThingsSummary.setVisibility(View.GONE);
        imageView.setImageDrawable(null);
        imageView.setVisibility(View.GONE);
        button.setVisibility(View.GONE);
    }

    private void displaySummary(int year, int month, int day) {
        Rating rating = dbh.getRow(year, month, day);
        if(rating == null)
            return;
        dateTitle.setVisibility(View.VISIBLE);
        dateTitle.setText(day + "/" + month + "/" + year);
        notesTitle.setVisibility(View.VISIBLE);
        notesSummary.setVisibility(View.VISIBLE);
        notesSummary.setText(rating.getNotes());
        imageView.setVisibility(View.VISIBLE);
        displayImage(rating.getImagePath());
        goodThingsTitle.setVisibility(View.VISIBLE);
        goodThingsSummary.setVisibility(View.VISIBLE);
        goodThingsSummary.setText("1. " + rating.getEntryOne() +
                "\n2. " + rating.getEntryTwo() +
                "\n3. " + rating.getEntryThree());
        button.setVisibility(View.VISIBLE);
    }

    private void displayImage(String imagePath){
        if(imagePath != null) {
            Uri uri = Uri.parse(imagePath);
            imageView.setImageURI(uri);
        }
    }

    //Sets up the appearance
    //Loads the data and draws the linechart
    private void drawLineGraph() {
        setupLineChart();
        setupLineChartData();
    }

    //Allows for vertical fade between colours across the graph
    private void setGradient() {
        // Get the paint renderer to create the line shading.
        Paint paint = lineChart.getRenderer().getPaintRender();

        //Array of colour to be applied in the gradient
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

    //Sets up the the styling for the linechart
    private void setupLineChart() {
        lineChart = (LineChart) findViewById(R.id.chart);
        // disable the description
        lineChart.setDescription("");
        //Disable background grid-lines
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
        //Set the min and max values for the graph
        yAxisLeft.setAxisMinValue(0);
        yAxisLeft.setAxisMaxValue(11);
        //Remove y-axis on the right
        lineChart.getAxisRight().setEnabled(false);
        lineChart.setScaleYEnabled(false);
        //Enable on tap highlight of graph
        lineChart.setHighlightPerTapEnabled(true);
    }

    private void setupLineChartData() {
        ArrayList<Entry> entries = new ArrayList<>();
        labels = new ArrayList<>();

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
        int i = 0; //Index to add a particular entry
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
            else if(hasData) {
                //Add a label for missing entries
                labels.add(date);
                i++;
            }
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        //Add the valid entries to the graph
        LineDataSet dataset = new LineDataSet(entries, "");
        dataset.setLineWidth(5f);

        dataset.setDrawHorizontalHighlightIndicator(false);
        dataset.setCircleRadius(7f);

        //Set background below the graph graph with a linear gradient, must be set on the dataset
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.graph_background);
        dataset.setFillDrawable(drawable);
        dataset.setDrawFilled(true);

        LineData data = new LineData(labels, dataset);
        lineChart.setData(data); // set the data and list of lables into chart

        // refresh the graph, ensures that the graph is drawn
        lineChart.invalidate();
    }

    private void makeToast(String text) {
        Context context  = getApplicationContext();
        int     duration = Toast.LENGTH_LONG;
        Toast   toast    = Toast.makeText(context, text, duration);
        toast.show();
    }
}