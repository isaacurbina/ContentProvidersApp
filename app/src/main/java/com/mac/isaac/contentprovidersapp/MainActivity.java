package com.mac.isaac.contentprovidersapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import com.fastaccess.permission.base.PermissionHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final String READ_CALENDAR = Manifest.permission.READ_CALENDAR;
    final String WRITE_CALENDAR = Manifest.permission.WRITE_CALENDAR;
    final int REQUEST_PERMISSION_RESULT = 1;
    private PermissionHelper permissionHelper;
    private ListView listCalendarEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listCalendarEvents = (ListView) findViewById(R.id.list_calendar_events);
        loadCalendars();
    }

    private void loadCalendars() {
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String[] projection = {
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME
        };
        String selection = CalendarContract.Calendars.CALENDAR_DISPLAY_NAME+" = ?";
        String[] selectionArgs = {
                "holiday's"
        };
        String sortOrder = CalendarContract.Calendars.NAME + " ASC";

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,
                    "You need " + READ_CALENDAR + " permission to continue",
                    Toast.LENGTH_SHORT)
                    .show();
            checkPermission();
        } else {
            Cursor cursor = getContentResolver()
                    .query(uri, projection, null, null, sortOrder);
            CalendarCursorAdapter adapter = new CalendarCursorAdapter(this, cursor,0);
            listCalendarEvents.setAdapter(adapter);
        }
    }

    private void checkPermission() {

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
            loadCalendars();
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
        loadCalendars();
    }

}
