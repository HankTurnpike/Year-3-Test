package com.example.patrick.myapplication;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DateScreen extends AppCompatActivity {
    RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_screen);
        layout=(RelativeLayout)findViewById(R.id.layout);
        final RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 0);
        TextView rating = new TextView(this);
        rating.setLayoutParams(params);
        rating.setTextSize(66);
        rating.setText("7");
        rating.setWidth(300);
        rating.setHeight(300);
        rating.setTextColor(Color.parseColor("#FFFFFF"));
        rating.setGravity(17);

        Drawable d = ContextCompat.getDrawable(this, R.drawable.rating_7);
        rating.setBackground(d);
        layout.addView(rating);

    }
}
