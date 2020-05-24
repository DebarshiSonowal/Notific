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
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class MyService extends Service implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final String TAG = MyService.class.getSimpleName();
    DatabaseReference root = FirebaseDatabase.getInstance().getReference();
    LatLng mLng,mLatLng;
    List<LatLng>mLatLngs = new ArrayList<>();
    List<LatLng>mLatLngList = new ArrayList<>();
    Double latitude,longitude;
    AudioManager mAudioManager;
    GoogleApiClient mLocationClient;
    Boolean flag =false;
    String phoneNr;
    String result;
    Map<String, List<LatLng>> mDictionary = new Hashtable<>();
    Integer number;
    BroadcastReceiver receiver;
    Context mContext;
    ValueEventListener mValueEventListener;
    LocationRequest mLocationRequest = new LocationRequest();
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    public static final String ACTION_LOCATION_BROADCAST = MyService.class.getName() + "LocationBroadcast";
    public static final String EXTRA_LATITUDE = "extra_latitude";
    public static final String EXTRA_LONGITUDE = "extra_longitude";
    Phone broad;

    @Override
    public void onDestroy() {
        mLocationClient.disconnect();
        root.removeEventListener(mValueEventListener);
        if (broad != null) {
            try {
                unregisterReceiver(broad);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.gc();
        super.onDestroy();

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
       broad = new Phone();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        if(intent.hasExtra())
            if(intent.hasExtra("ACTION")){
                if(intent.getStringExtra("ACTION").equals("STOP"))
                {
//                    Intent intent1 = new Intent(this,MyService.class);
//                    stopService(intent1);
//                    System.exit(0);
                    mAudioManager.setMode(AudioManager.RINGER_MODE_NORMAL);
                    stopSelf();
                    return START_NOT_STICKY;

                }
            }
//        getData();
        createNotificationChannel();

        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);


        Intent notificationIntent = new Intent(this, Main2Activity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
//        Intent stopService = new Intent(this,MyService.class);
//        stopService.setAction("STOP");
//        PendingIntent stop = PendingIntent.getService(this,0,stopService,PendingIntent.FLAG_CANCEL_CURRENT);
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
        mLocationRequest.setFastestInterval(3000);
//        mLocationRequest.setSmallestDisplacement(10f);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationClient.connect();

        return START_REDELIVER_INTENT;
    }
    private void getData() {
        root.child("Marked Location").addValueEventListener( mValueEventListener =  new ValueEventListener() {
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
                            }
                            else
                                longitude =  Double.parseDouble(dataSnapshot3.getValue().toString()) ;
                        }
                        mLng = new LatLng(latitude,longitude);
                        Log.d("MyService",mLng.toString());
                        mLatLngs.add(mLng);
//                        if(mLatLngs.size() == 4)
//                        {
//                            mDictionary.put(dataSnapshot1.getKey(),mLatLngs);
//                            namelist.add(dataSnapshot1.getKey());
//
//                        }

                        Log.d("MyService","Got data");
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
            send(location);
//            check(location);
            mLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            Log.d("MyService",location.getLatitude()+ " " + location.getLongitude() +" A");

//            get2checked();
            getData();
            getchecked();
            sound(flag);
        }
    }

//    private void get2checked() {
//        for(int l=0;l<namelist.size();l++)
//        {
//
//            try {
//                mLatLngList.addAll(mDictionary.get(namelist.get(l)));
//            } catch (Exception e) {
//                Log.d("Check",e.getMessage());
//            }
//            try {
//                mLatLngList.add(mLatLngList.get(0));
//            } catch (IndexOutOfBoundsException e) {
//                Log.d("Check",e.getMessage());
//            }
//
//                flag = PolyUtil.containsLocation(mLatLng,mLatLngList,true);
//                sound(flag);
//                mLatLngList.clear();
//                if(flag)
//                {
//                    if (broad == null) {
//                        startBroadcast();
//                    }
//
//                }
//                else
//                {
//                    if (broad != null) {
//                        try {
//                            stopBroadcast();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//        }


    private void getchecked() {
        for(int i=0;i< number/4;i++)
        {
            Log.d("MyService", String.valueOf(i));
            Log.d("MyService", String.valueOf(number/4));
            for(int j =0;j<4;j++)
            {
                Log.d("MyService", String.valueOf(j)+"point");
                mLatLngList.add(mLatLngs.get(j));
                Log.d("MyService",mLatLngList.get(j).toString());
            }
            mLatLngList.add(mLatLngs.get(0));
            Log.d("MyService",mLatLngs.get(0).toString());
            flag = PolyUtil.containsLocation(mLatLng,mLatLngList,true);
            Log.d("MyService",flag.toString());
            sound(flag);
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
        filter.addAction("android.intent.action.PHONE_STATE");
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        filter.addAction("android.media.RINGER_MODE_CHANGED");
        registerReceiver(broad, filter);
    }

    private void sound(Boolean flag) {
        if(flag)
        {

            if(mAudioManager.getRingerMode() != AudioManager.RINGER_MODE_VIBRATE)
            {
                if(mAudioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT)
                {
                        mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

                }

            }
        }
        else
            if(mAudioManager.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
            {
                if (mAudioManager.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
                    mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
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
        sendnotific(result);
        sendMessageToUI(String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude()),result,flag);
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

//    public class mybroad extends BroadcastReceiver{
//    Context mContext;
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            mContext = context;
//        }
//
//        public Context getContext() {
//            return mContext;
//        }
//    }
}
