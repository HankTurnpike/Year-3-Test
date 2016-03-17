package com.example.patrick.myapplication;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

/**
 * This class decorates a single day of the calendar that is supplied
 * with whatever drawable is given along with it.
 */
public class OneDayDecorator implements DayViewDecorator {
    private final CalendarDay date;
    private Drawable d;

    public OneDayDecorator() {
        date = CalendarDay.today();
    }

    public OneDayDecorator(CalendarDay day, Drawable d) {
        date = day;
        this.d = d;
    }

    @Override
    //If the date is not null and it equals date
    //then return true.
    public boolean shouldDecorate(CalendarDay day) {
        return date != null && day.equals(date);
    }

    @Override
    //Decorates the day on the calendar with the drawable
    //provided and changes text font and colour.
    public void decorate(DayViewFacade view) {

        view.addSpan(new StyleSpan(Typeface.BOLD));
        view.addSpan(new RelativeSizeSpan(1.2f));
        view.setSelectionDrawable(d);
        view.addSpan(new ForegroundColorSpan(Color.WHITE));

    }
}