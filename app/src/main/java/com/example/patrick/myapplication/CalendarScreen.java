package com.example.patrick.myapplication;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import butterknife.Bind;
import butterknife.ButterKnife;

import java.util.Calendar;
import java.util.Date;

public class CalendarScreen extends AppCompatActivity implements OnDateSelectedListener {
    public final static String DATE = "com.example.patrick.DATE";
    RelativeLayout layout;
    @Bind(R.id.calendarView)
    MaterialCalendarView calendarView;
    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();
    private DataBaseHelper dbh;
    //TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        ButterKnife.bind(this);

        dbh = new DataBaseHelper(this);
        calendarView.setOnDateChangedListener(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setTitle("Calendar");

        Intent intent = getIntent();
        TypedArray ar = this.getResources().obtainTypedArray(R.array.img_id_arr);
        int len = ar.length();
        int[] resIds = new int[len];
        for (int i = 0; i < len; i++)
            resIds[i] = ar.getResourceId(i, 0);
        ar.recycle();

        layout=(RelativeLayout)findViewById(R.id.second_row);
        final MaterialCalendarView calendarView = (MaterialCalendarView) layout.findViewById(R.id.calendarView);
        //Calendar calendar = Calendar.getInstance();
        //CalendarDay day2 = CalendarDay.from(calendar);

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

        CalendarDay day;
        while (!cal.equals(tomorrow)) {
            int rating = dbh.getRating(cal);
            if(rating != -1) {
                day = CalendarDay.from(cal);
                calendarView.addDecorators(new OneDayDecorator(day,
                        ContextCompat.getDrawable(this,resIds[rating - 1])));
            }
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }


}

    public class OneDayDecorator implements DayViewDecorator {

        private CalendarDay date;
        private Drawable d;

        public OneDayDecorator() {
            date = CalendarDay.today();
        }

        public OneDayDecorator(CalendarDay day, Drawable d) {
            date = day;
            this.d = d;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return date != null && day.equals(date);
        }

        @Override
        public void decorate(DayViewFacade view) {

            view.addSpan(new StyleSpan(Typeface.BOLD));
            view.addSpan(new RelativeSizeSpan(1.2f));
            view.setSelectionDrawable(d);
            view.addSpan(new ForegroundColorSpan(Color.WHITE));

        }
        public  void setDate(Date date) {
            this.date = CalendarDay.from(date);
        }
    }
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        CalendarDay current = CalendarDay.from(Calendar.getInstance());
        if((current.equals(date)||current.isAfter(date))&&dataExists(date)) {
            Intent intent = new Intent(this, DateScreen.class);
            int[] temp = {date.getYear(), date.getMonth(), date.getDay()};
            intent.putExtra(DATE, temp);
            startActivity(intent);
        }
    }
    public boolean dataExists(CalendarDay date){
        Calendar temp = Calendar.getInstance();
        temp.set(date.getYear(),date.getMonth(),date.getDay());
        return(dbh.getRating(temp)!=-1);
    }
    public void goToCalendar (MenuItem item) {}
    public void goToGraph(MenuItem item) {
        Intent intent = new Intent(this, GraphActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); // To clean up all activities
        startActivity(intent);
        finish();
    }
    public void goToMain(MenuItem item) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); // To clean up all activities
        startActivity(intent);
        finish();
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}