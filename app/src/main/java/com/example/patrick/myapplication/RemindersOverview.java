package com.example.patrick.myapplication;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class RemindersOverview extends AppCompatActivity {
    private String [] versions = {"Cupcake", "Donut", "Eclair", "Froyo", "Gingerbread", "Honey",
            "Ice Cream", "Jerry Been", "KatKit", "Lolliotpop", "Nutella"};
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        ListView listView = (ListView) findViewById(R.id.listview_Reminder);
        //populateListView();

        //adapter = new ArrayAdapter<String>(this, R.layout.reminder_overview, R.id
        //        .reminder_list_item_notes ,versions);
        //listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                makeToast("" + parent.getItemAtPosition(position));
            }
        });
    }

    public void goToCreateReminder(View view) {
        Intent intent = new Intent(this, CreateReminder.class);
        startActivity(intent);
    }

    private void makeToast(String text) {
        Context context  = getApplicationContext();
        int     duration = Toast.LENGTH_LONG;
        Toast   toast    = Toast.makeText(context, text, duration);
        toast.show();
    }

/*    private void populateListView() {
        //DataBaseHelper dbh = new DataBaseHelper(this);
       // Cursor cursor = dbh.getAllReminders();
        //String [] fieldNames = {dbh.REMINDER_TITLE, dbh.REMINDER_DATE, dbh.REMINDER_TIME,
        //                        dbh.REMINDER_NOTES, dbh.REMINDER_ROW_ID};
        int [] viewIDs = {R.id.reminder_list_item_title, R.id.reminder_list_item_date,
                          R.id.reminder_list_item_time, R.id.reminder_list_item_notes,
                          R.id.reminder_key};
        SimpleCursorAdapter simpleCursorAdapter =
                new SimpleCursorAdapter(getBaseContext(), R.layout.reminder_overview,
                cursor);
        listView.setAdapter(simpleCursorAdapter);
    }*/
}