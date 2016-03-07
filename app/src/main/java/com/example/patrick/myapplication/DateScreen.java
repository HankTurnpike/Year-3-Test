package com.example.patrick.myapplication;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;

public class DateScreen extends AppCompatActivity {
    private RelativeLayout layout;
    private DataBaseHelper dbh;
    private String imagePath ="";
    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_screen);
        dbh            = new DataBaseHelper(this);
        TypedArray ar = this.getResources().obtainTypedArray(R.array.img_id_arr);
        int len = ar.length();
        int[] resIds = new int[len];
        for (int i = 0; i < len; i++)
            resIds[i] = ar.getResourceId(i, 0);
        ar.recycle();
        Rating data = dbh.getRow(Calendar.getInstance());
        layout=(RelativeLayout)findViewById(R.id.layout);
        if(data != null) {
            final RelativeLayout.LayoutParams params =
                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
            TextView notes= (TextView) findViewById(R.id.notes);
            notes.setText(data.getNotes());
            TextView goodThings= (TextView) findViewById(R.id.good_things);
            goodThings.setText("1. "+ data.getEntryOne()+"\n2. "+data.getEntryTwo()+"\n3. "+data.getEntryThree());
            imageView = (ImageView) findViewById(R.id.image);
            imagePath = data.getImagePath();
            displayImage();

            params.setMargins(0, 0, 0, 0);
            TextView rating = new TextView(this);
            rating.setLayoutParams(params);
            rating.setTextSize(66);
            rating.setText("" + data.getRating());
            rating.setWidth(300);
            rating.setHeight(300);
            rating.setTextColor(Color.parseColor("#FFFFFF"));
            rating.setGravity(17);

            Drawable d = ContextCompat.getDrawable(this, resIds[data.getRating() - 1]);
            rating.setBackground(d);
            layout.addView(rating);
        }

    }
    private void displayImage(){
        if(imagePath != null) {
            Uri uri = Uri.parse(imagePath);
            imageView.setImageURI(uri);
        }
    }
}
