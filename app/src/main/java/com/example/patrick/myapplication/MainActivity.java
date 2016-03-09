package com.example.patrick.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.mycompany.myfirstapp.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);

    }
    public void goToDate (View view) {
        Intent intent = new Intent(this, DateScreen.class);
        int[] temp = {2016,2,3};
        intent.putExtra("com.example.patrick.DATE",temp);
        startActivity(intent);
    }
    public void goToCalendar (View view) {
        Intent intent = new Intent(this, CalendarScreen.class);
        startActivity(intent);
    }
    public void goToDatabaseScreen(View view) {
        Intent intent = new Intent(this, DataBaseActivity.class);
        startActivity(intent);
    }
    public void goToGraph(View view) {
        Intent intent = new Intent(this, GraphActivity.class);
        startActivity(intent);
    }
}