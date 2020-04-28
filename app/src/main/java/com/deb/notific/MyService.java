package com.deb.notific;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.deb.notific.helper.Check;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.Builder;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.PolyUtil;
import com.snatik.polygon.Point;
import com.snatik.polygon.Polygon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyService extends Service implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final String TAG = MyService.class.getSimpleName();
    DatabaseReference root = FirebaseDatabase.getInstance().getReference();
    LatLng mLng,mLatLng;
    List<LatLng>mLatLngs = new ArrayList<>();
    List<LatLng>mLatLngList = new ArrayList<>();
    Double latitude,longitude;
    GoogleApiClient mLocationClient;
    Boolean flag =false;
    String result;
    LocationRequest mLocationRequest = new LocationRequest();
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    public static final String ACTION_LOCATION_BROADCAST = MyService.class.getName() + "LocationBroadcast";
    public static final String EXTRA_LATITUDE = "extra_latitude";
    public static final String EXTRA_LONGITUDE = "extra_longitude";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getData();
        createNotificationChannel();
//        startForeground(1,getNotification("Starting"));
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        CharSequence input = "Checking";
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service")
                .setContentText(input)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
        mLocationClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(2000);
//        mLocationRequest.setSmallestDisplacement(10f);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationClient.connect();

        return START_REDELIVER_INTENT;
    }

//    private Notification getNotification(String s) {
//        Intent notificationIntent = new Intent(this, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
//        return new NotificationCompat.Builder(this)
//                .setContentTitle("Notific running in background")
//                .setContentText(s)
//                .setContentIntent(pendingIntent).getNotification();
//    }

    private void getData() {
        root.child("Marked Location").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    for(DataSnapshot dataSnapshot2:dataSnapshot1.getChildren())
                    {
                        for(DataSnapshot dataSnapshot3: dataSnapshot2.getChildren())
                        {
                            if(dataSnapshot3.getKey().equals("latitude"))
                            {
                                latitude = Double.parseDouble(dataSnapshot3.getValue().toString()) ;
                                Log.d("MyService",dataSnapshot3.getValue().toString());
                            }
                            else
                                longitude =  Double.parseDouble(dataSnapshot3.getValue().toString()) ;
                        }
                        mLng = new LatLng(latitude,longitude);
                        Log.d("MyService",mLng.toString());
                        mLatLngs.add(mLng);
                        Log.d("MyService","Got data");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "== Error On onConnected() Permission not granted");
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient,mLocationRequest,this);
        Log.d(TAG,"Connected to Google API");

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection suspended");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Failed to connect to Google API");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location changed");
        if(location != null)
        {
//            check(location);
            mLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            Log.d("MyService",mLatLng.toString()+" A");
            for(int i=0;i<mLatLngs.size()/5;i++)
            {
                Log.d("MyService", String.valueOf(i));
                for(int j =0;j<5;j++)
                {
                    Log.d("MyService",mLatLngs.get(j).toString());
                    mLatLngList.add(mLatLngs.get(j));
                }
                flag = PolyUtil.containsLocation(mLatLng,mLatLngList,false);
                mLatLngList.clear();
            }



//            for(int i=0;i<mLatLngs.size()/5;i++)
//            {
//                String ma = String.valueOf(i);
//                Log.d("MyService",ma);
//
//                Polygon m = Polygon.Builder().addVertex(new Point(mLatLngs.get(i).latitude,mLatLngs.get(i).longitude))
//                        .addVertex(new Point(mLatLngs.get(i+1).latitude,mLatLngs.get(i+1).longitude))
//                        .addVertex(new Point(mLatLngs.get(i+2).latitude,mLatLngs.get(i+2).longitude))
//                        .addVertex(new Point(mLatLngs.get(i+3).latitude,mLatLngs.get(i+3).longitude))
//                        .addVertex(new Point(mLatLngs.get(i+4).latitude,mLatLngs.get(i+4).longitude)).build();
//                Point mp = new Point(location.getLatitude(),location.getLongitude());
//                if(m.contains(mp))
//                {
//                    flag = true;
//                    break;
//                }
//
//            }
//            Toast.makeText(this,location.getLongitude()+"/"+location.getLatitude(),Toast.LENGTH_SHORT).show();
            Log.d("MyService",flag.toString());
           send(location);
        }
    }

    private void send(Location location) {
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addresses =
                    geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            result = addresses.get(0).getLocality() + ":";
            result += addresses.get(0).getCountryName();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sendnotific(result);
        sendMessageToUI(String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude()),result,flag);
    }


    private void sendnotific(String result) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification =  new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("The locaton service is running")
                .setContentText(result)
                .setSmallIcon(R.drawable.address)
                .setContentIntent(pendingIntent)
                .build();

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, notification);
    }




    private void sendMessageToUI(String lat ,String lng,String nm, Boolean state) {
        Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
        intent.putExtra(EXTRA_LATITUDE,lat);
        intent.putExtra(EXTRA_LONGITUDE,lng);
        intent.putExtra("Name",nm);
        intent.putExtra("STATE",state.toString());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}
