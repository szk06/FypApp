package com.example.sami.fyp16;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.sami.fyp16.classes.BackLocationInsert;
import com.example.sami.fyp16.classes.Session;

public class UserUpdateLocation extends AppCompatActivity {



    private LocationManager locationManager;
    private LocationListener listener;
    private boolean got_location;
    private Session session;
    private SQLiteDatabase mydatabase;
    private String user_name_sec;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_update_location);
        session = new Session(this);//Create the session
        if(!session.loggedin()){
            logout();
        }
        mydatabase = openOrCreateDatabase("App_Users",MODE_PRIVATE,null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS users(Username VARCHAR);");
        Cursor resultSet = mydatabase.rawQuery("Select * from users", null);
        resultSet.moveToFirst();
        user_name_sec = resultSet.getString(0);
        Log.d("User_name_sec",user_name_sec);
    }
    private void logout(){
        session.setLoggedin(false);
        Intent myIntent = new Intent(UserUpdateLocation.this, MainActivity.class);
        UserUpdateLocation.this.startActivity(myIntent);
    }
    public void TrackLocation(View v){
        Log.d("Button_Click","TrackLocation");

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                got_location = true;

                Log.d("Longitude",String.valueOf(location.getLongitude()));
                Log.d("Latitude",String.valueOf(location.getLatitude()));
                String latitude = String.valueOf(location.getLongitude());
                String longitude = String.valueOf(location.getLatitude());
                sendlocation(latitude,longitude,location);
                //stop_search();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };
        configure_button();

    }
    public void sendlocation(String lat, String longt,Location location){
        //if the location was stored, the coordinates will be sent directly to the server
        if(got_location==true){
            BackLocationInsert bacloc = new BackLocationInsert(this);
            long time_stamp = location.getTime();
            String strTime = Long.toString(time_stamp);

            bacloc.execute(lat,longt,user_name_sec,strTime);
        }
    }
    public void stop_search(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET}
                        ,10);
            }
            return;
        }
        locationManager.removeUpdates(listener);
        locationManager = null;
    }

    void configure_button(){
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET}
                        ,10);
            }
            return;
        }
        //How many times I am checking for the location
        locationManager.requestLocationUpdates("gps", 5000, 0, listener);
    }
}
