package com.deb.notific;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.deb.notific.helper.BusStation;
import com.deb.notific.helper.message;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.snatik.polygon.Point;
import com.snatik.polygon.Polygon;
import com.squareup.otto.Bus;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final String CHANNEL_1_ID = "channel1";
    private static final int ON_DO_NOT_DISTURB_CALLBACK_CODE = 0;
    ArrayList<LatLng>mLatLngs = new ArrayList<>();
    LocationListener locationListener;
    TextView ringm;
    Double latitude,longitude;
    LocationManager locationManager;
    LatLng mLng;
    Boolean flag = false;
    String mString;
    BusStation mBusStation;
  public AudioManager am;

    DatabaseReference root = FirebaseDatabase.getInstance().getReference();
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        getWindow().setStatusBarColor(Color.parseColor("#000000"));
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // if user granted access else ask for permission
        if (notificationManager.isNotificationPolicyAccessGranted()) {
            am = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        } else {
            // Open Setting screen to ask for permisssion
            Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivityForResult(intent, Main2Activity.ON_DO_NOT_DISTURB_CALLBACK_CODE);
        }
            locationManager =
                    (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            root = FirebaseDatabase.getInstance().getReference();
            root.child("Marked Location").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                            for (DataSnapshot dataSnapshot3 : dataSnapshot2.getChildren()) {
                                if (dataSnapshot3.getKey().equals("latitude")) {
                                    latitude = (Double) dataSnapshot3.getValue();

                                } else
                                    longitude = (Double) dataSnapshot3.getValue();
                            }
                            mLng = new LatLng(latitude, longitude);
                            mLatLngs.add(mLng);
                        }
                    }
                    getloc();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


    }
    private void getloc() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
        else{
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    mLng = new LatLng(location.getLatitude(), location.getLongitude());
                    check();
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10f, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10f, locationListener);

        }




    }

        private void check() {
            if (mLatLngs != null)
            {
                Toast.makeText(this,"Got data",Toast.LENGTH_SHORT).show();
                getData();
            }

        else
            Toast.makeText(this,"Got no data",Toast.LENGTH_SHORT).show();
    }

    private void getData() {
        Polygon m = Polygon.Builder().addVertex(new Point(mLatLngs.get(0).latitude,mLatLngs.get(0).longitude))
                .addVertex(new Point(mLatLngs.get(1).latitude,mLatLngs.get(1).longitude))
                .addVertex(new Point(mLatLngs.get(2).latitude,mLatLngs.get(2).longitude))
                .addVertex(new Point(mLatLngs.get(3).latitude,mLatLngs.get(3).longitude))
                .addVertex(new Point(mLatLngs.get(4).latitude,mLatLngs.get(4).longitude)).build();
        Point mp = new Point(mLng.latitude,mLng.longitude);
        flag = m.contains(mp);
        checkStat();
    }


    private void checkStat(){
        if(flag)
        {
//            am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//            am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
//            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//                NotificationChannel channel = new NotificationChannel(CHANNEL_1_ID,"Channel 1",
//                        NotificationManager.IMPORTANCE_HIGH
//                );
//                channel.setDescription("You are at that location");
//                NotificationManager manager = getSystemService(NotificationManager.class);
//                manager.createNotificationChannel(channel);
//            }
        }
        else
        {
            Toast.makeText(this,"Not in the location",Toast.LENGTH_SHORT).show();
            BusStation.getBus().post(new message("Outside marked location"));
            int currentMode = am.getRingerMode();
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                NotificationChannel channel = new NotificationChannel(CHANNEL_1_ID,"Channel 1",
                        NotificationManager.IMPORTANCE_HIGH
                );
                channel.setDescription("You are at that location");
                NotificationManager manager = getSystemService(NotificationManager.class);
                manager.createNotificationChannel(channel);
            }
            if(currentMode == AudioManager.RINGER_MODE_VIBRATE){
                am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                mString = "Ringing";
//                BusStation.getBus().post(new message(mString));
            }
            else
            {
                am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                mString = "Vibrate";
//                BusStation.getBus().post(new message(mString));

            }
            }
        }

    public void startService(View v){
        Intent serviceIntent = new Intent(this,LocationService.class);
        startService(serviceIntent);
    }
    }


