package com.mac.isaac.contentprovidersapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity {

    final String READ_CALENDAR = Manifest.permission.READ_CALENDAR;
    final String WRITE_CALENDAR = Manifest.permission.WRITE_CALENDAR;
    final int REQUEST_PERMISSION_RESULT = 1;
    private ListView listEvents;
    private long calendar_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        if (getIntent().hasExtra("CALENDAR_ID"))
            calendar_id = getIntent().getExtras().getLong("CALENDAR_ID");
        Log.d("MYAPP", "MainActivity2 Calendar_ID is "+calendar_id);
        listEvents = (ListView) findViewById(R.id.list_events);
        loadEvents();
    }

    private void loadEvents() {
        Uri uri = CalendarContract.Events.CONTENT_URI;
        String[] projection = {
                CalendarContract.Events._ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Calendars._ID
        };
        String selection = CalendarContract.Events.CALENDAR_ID+" = ?";
        String[] selectionArgs = {
                String.valueOf(calendar_id)
        };
        String sortOrder = CalendarContract.Events.TITLE + " ASC";

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,
                    "You need " + READ_CALENDAR + " permission to continue",
                    Toast.LENGTH_SHORT)
                    .show();
            checkPermission();
        } else {
            Cursor cursor = getContentResolver()
                    .query(uri, projection, selection, selectionArgs, sortOrder);
            EventCursorAdapter adapter = new EventCursorAdapter(this, cursor,0);
            listEvents.setAdapter(adapter);
        }
    }

    public void checkPermission() {

        List<String> permissionsToRequest = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_DENIED) {
            permissionsToRequest.add(Manifest.permission.READ_CALENDAR);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_DENIED) {
            permissionsToRequest.add(Manifest.permission.WRITE_CALENDAR);;
        }

        String[] permissionsList = new String[permissionsToRequest.size()];
        permissionsList = permissionsToRequest.toArray(permissionsList);

        if (permissionsToRequest.size() != 0) {
            ActivityCompat.requestPermissions(this, permissionsList, REQUEST_PERMISSION_RESULT);
        } else {
            loadEvents();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_RESULT) {
            for (int i = 0; i < permissions.length; i++) {
                if (Manifest.permission.READ_CALENDAR.equals(permissions[i])) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        throw new RuntimeException();
                    }
                } else if (Manifest.permission.WRITE_CALENDAR.equals(permissions[i])) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        throw new RuntimeException();
                    }
                }
            }
        }
        loadEvents();
    }
}
