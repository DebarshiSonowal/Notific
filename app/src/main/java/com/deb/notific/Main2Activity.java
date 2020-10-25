package com.deb.notific;

import android.Manifest;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.vlonjatg.progressactivity.ProgressRelativeLayout;

public class Main2Activity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_READ_PHONE_STATE =1 ;
    BottomNavigationView navView;
    NavController navController;
    Boolean serv;
    ProgressRelativeLayout mEmptyView;
    LocalBroadcastManager mBroadcastManager;
    NotificationManager notificationManager;
    BroadcastReceiver mBroadcastReceiver;
    ConnectivityManager cm;
    IntentFilter intentFilter;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        notificationManager = null;
        navController = null;
        navView = null;
        System.gc();
    }
    @Override
    protected void onStart() {
        super.onStart();
//        if(activeNetwork != null &&
//                activeNetwork.isConnectedOrConnecting()){
//            Toast.makeText(this,"No internet",Toast.LENGTH_SHORT).show();
//            mEmptyView.showContent();
//        }else
//            mEmptyView.showEmpty(R.drawable.no_internet,"No Internet","No Internet Connection");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        getWindow().setStatusBarColor(Color.parseColor("#000000"));

        //Coonecting views
        mEmptyView = findViewById(R.id.loadingLayout);
        navView = findViewById(R.id.nav_view);

        //creating objects
        SharedPreferences sharedPreferences = this.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
         cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final ConnectivityManager connMgr = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);

                final android.net.NetworkInfo wifi = connMgr
                        .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                final android.net.NetworkInfo mobile = connMgr
                        .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

                if (wifi.isConnected() || mobile.isConnected()) {
                    mEmptyView.showContent();
                }else
                    mEmptyView.showEmpty(R.drawable.no_internet,"No Internet","No Internet Connection");


            }
        };
         //checking whether the service was on or off previously
        serv = sharedPreferences.getBoolean("onswitch",true);
        intentFilter = new IntentFilter((ConnectivityManager.CONNECTIVITY_ACTION));

        //Doing stuff
        if(serv)
        {
            Intent intent = new Intent(this,MyService.class);
            startService(intent);
        }
        registerReceiver(mBroadcastReceiver,intentFilter);

        //Default

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications,R.id.markedLocation)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !notificationManager.isNotificationPolicyAccessGranted()) {

            Intent intent = new Intent(
                    android.provider.Settings
                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

            startActivity(intent);
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {
                String[] permissions = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE};
                requestPermissions(permissions, PERMISSION_REQUEST_READ_PHONE_STATE);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBroadcastReceiver);
    }

    }

