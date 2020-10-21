package com.deb.notific;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.airbnb.lottie.L;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class MyService extends Service implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, LocationListener{
    private static final String TAG = MyService.class.getSimpleName();
    DatabaseReference root = FirebaseDatabase.getInstance().getReference();
    LatLng mLng,mLatLng;
    List<LatLng>mLatLngs = new ArrayList<>();
    List<LatLng>mLatLngList = new ArrayList<>();
    List<Double>area = new ArrayList<>();
    Double latitude,longitude;
    AudioManager mAudioManager;
    GoogleApiClient mLocationClient;
    Boolean flag =false;
    String result,result1,getResult;
    Map<String, List<LatLng>> mDictionary = new Hashtable<>();
    Integer number;
    Context mContext;
    ValueEventListener mValueEventListener;
    LocationRequest mLocationRequest = new LocationRequest();
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    public static final String ACTION_LOCATION_BROADCAST = MyService.class.getName() + "LocationBroadcast";
    public static final String EXTRA_LATITUDE = "extra_latitude";
    public static final String EXTRA_LONGITUDE = "extra_longitude";
    Phone broad;
    call_sms mCallsms;

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mLocationClient.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            root.removeEventListener(mValueEventListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (broad != null) {
            try {
                unregisterReceiver(broad);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (mCallsms != null) {
            try {
                unregisterReceiver(mCallsms);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.gc();


    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
       broad = new Phone();
       mCallsms = new call_sms();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            if(intent.hasExtra("ACTION")){
                if(intent.getStringExtra("ACTION").equals("STOP"))
                {
                    mAudioManager.setMode(AudioManager.RINGER_MODE_NORMAL);
                    try {
                        stopSelf();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return START_NOT_STICKY;
                }
            }

        createNotificationChannel();




        Intent notificationIntent = new Intent(this, Main2Activity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        CharSequence input = "Checking";
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service")
                .setContentText(input)
                .setContentIntent(pendingIntent)
//                .addAction(R.drawable.address,"Stop",stop)
                .build();

        startForeground(1, notification);
               mLocationClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationClient.connect();

        return START_REDELIVER_INTENT;
    }
    private void getData() {
        root.child("Marked Location").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener( mValueEventListener =  new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                    {
                        for(DataSnapshot dataSnapshot2:dataSnapshot1.getChildren())
                        {
                            for(DataSnapshot dataSnapshot3: dataSnapshot2.getChildren())
                            {
                                if(dataSnapshot3.getKey().equals("latitude"))
                                {
                                    latitude = Double.parseDouble(dataSnapshot3.getValue().toString()) ;
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        number = mLatLngs.size();

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
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient,mLocationRequest,this);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            result1 = result;
            Log.d("Loc",result1+"");
            send(location);
        }
    }

    private void getchecked() throws IOException {
        Geocoder geocoder = new Geocoder(this);
        area.clear();
        for(int i=0;i< number/4;i++)
        {
//            Log.d("MyService", String.valueOf(i));
//            Log.d("MyService", String.valueOf(number/4));
            for(int j =0;j<4;j++)
            {
                Log.d("MyService", String.valueOf(j)+"point");
                mLatLngList.add(mLatLngs.get(j));
                Log.d("MyService",mLatLngList.get(j).toString());
            }
            mLatLngList.add(mLatLngs.get(0));
            Log.d("MyService",mLatLngs.get(0).toString());
            flag = PolyUtil.containsLocation(mLatLng,mLatLngList,true);
            area.add(SphericalUtil.computeArea(mLatLngList));
            Log.d("Area",SphericalUtil.computeArea(mLatLngList)+"");
            Log.d("MyService",flag.toString());
            if(flag)
            {
                startBroadcast();
            }
            else
            {
                try {
                        stopBroadcast();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
            if(flag){
                break;
            }
            mLatLngList.clear();
            mLatLngs.subList(0,3).clear();
        }

    }
    private void stopBroadcast() {
        try {
            unregisterReceiver(broad);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.setPriority(1000);
        filter.addAction("android.intent.action.PHONE_STATE");
        filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        filter.addAction("android.media.RINGER_MODE_CHANGED");
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        filter.addAction(android.telephony.TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        registerReceiver(broad, filter);
    }

    private void sound(Boolean flag) {
        if(flag)
        {
            try {
                mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        else{
            try {
                mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

                } catch (Exception e) {
                e.printStackTrace();
                }
            }
    }





    private void send(Location location) {
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addresses =
                    geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            result = addresses.get(0).getLocality() + ",";
            result += addresses.get(0).getSubLocality()+ ",";
            result += addresses.get(0).getAdminArea();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (result1 != null) {
            if (result1.equals(result)) {
                mLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                Log.d("MyService",location.getLatitude()+ " " + location.getLongitude() +" A");
                getData();
                try {
                    getchecked();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sound(flag);
                sendMessageToUI(String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude()),result,flag);
                sendnotific(result);
            }
        } else {
            mLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            Log.d("MyService",location.getLatitude()+ " " + location.getLongitude() +" A");
            getData();
            try {
                getchecked();
            } catch (IOException e) {
                e.printStackTrace();
            }
            sound(flag);
            sendMessageToUI(String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude()),result,flag);
            sendnotific(result);
        }
    }


    private void sendnotific(String result) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Intent stopService = new Intent(this,MyService.class);
        stopService.putExtra("ACTION","STOP");
        PendingIntent stop = PendingIntent.getService(this,0,stopService,PendingIntent.FLAG_CANCEL_CURRENT);
        Notification notification =  new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("The locaton service is running")
                .setContentText(result)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setSmallIcon(R.drawable.health)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.health,"Stop",stop)
                .build();

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, notification);
    }




    private void sendMessageToUI(String lat ,String lng,String nm, Boolean state) {
        saveData(nm);
        Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
        intent.putExtra(EXTRA_LATITUDE,lat);
        intent.putExtra(EXTRA_LONGITUDE,lng);
        intent.putExtra("Name",nm);
        intent.putExtra("STATE",state.toString());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void saveData( String nm) {
        if (flag) {
            SharedPreferences sharedPreferences = getSharedPreferences("Service", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.putString("Status","Inside");
            editor.commit();
            editor.putString("Name",nm);
            editor.commit();
        } else {
            SharedPreferences sharedPreferences = getSharedPreferences("Service", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.putString("Status","Outside");
            editor.commit();
            editor.putString("Name",nm);
            editor.commit();
        }
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
