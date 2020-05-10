package com.deb.notific;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.deb.notific.helper.polylocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, Dialog.DialogListener {
    private Button addloc, clear, loca;
    private GoogleMap mMap;
    private Marker marker;
    private LatLng latLng;
    private String result;
    private Polygon mPolygon,nPolygon;
    private LocationManager locationManager;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private List<Marker> markerList = new ArrayList<>();
    private DatabaseReference root, local;
    private Boolean flag = false, incr = false;
    LocalBroadcastManager mBroadcastManager;
    BroadcastReceiver mBroadcastReceiver;
    private List<LatLng> mLatLngs = new ArrayList<>();
    private List<polylocation> nLatLngs = new ArrayList<>();
    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment= (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);
        locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mBroadcastManager = LocalBroadcastManager.getInstance(this);
        //Connecting with view
        addloc = findViewById(R.id.marklocbtn);
        clear = findViewById(R.id.clrbtn);
        loca = findViewById(R.id.locbtn);

        //Database Operations
        root = FirebaseDatabase.getInstance().getReference();
        local = root.child("Marked Location");

        //Check for permissions
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            mBroadcastManager.registerReceiver( mBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String lat = intent.getStringExtra(MyService.EXTRA_LATITUDE);
                    String lon = intent.getStringExtra(MyService.EXTRA_LONGITUDE);
                    String name = intent.getStringExtra("Name");
                    latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
                    updateLoc(latLng, name);
                }
                }, new IntentFilter(MyService.ACTION_LOCATION_BROADCAST)
            );
        }
    }
// 1000 Millisecond  = 1 secon


    private void updateLoc(LatLng latLng, String result) {
        if (marker != null) {
            if (flag) {
                marker.remove();
                marker = mMap.addMarker(new MarkerOptions().position(latLng).title(result).icon(BitmapDescriptorFactory.fromResource(R.drawable.purple)));
                CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(20).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                flag = false;
            }
        } else {
            marker = mMap.addMarker(new MarkerOptions().position(latLng).title(result).icon(BitmapDescriptorFactory.fromResource(R.drawable.purple)));


            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(20).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (ActivityCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager=null;
        mapFragment = null;
        root = null;
        mBroadcastManager.unregisterReceiver(mBroadcastReceiver);
        mMap=null;
        System.gc();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setBuildingsEnabled(true);
        mMap.setMinZoomPreference(1.0f);
        mMap.setMaxZoomPreference(25.0f);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        CameraUpdateFactory.scrollBy(6, 6);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (flag) {
                    for (Marker marker : markerList) marker.remove();
                }
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).draggable(true);
                Marker marker = mMap.addMarker(markerOptions);
                mLatLngs.add(latLng);
                Log.d("MyService", latLng.toString());
                markerList.add(marker);
                if (mLatLngs.size() == 4) {
                    opendialog();

                    PolygonOptions polygonOptions = new PolygonOptions().addAll(mLatLngs).clickable(true);
                    nPolygon = mMap.addPolygon(polygonOptions);
                    if (result != null) {
                        nPolygon.setTag(result);
                    }
                    nPolygon.setStrokeColor(Color.BLACK);
                    nPolygon.setFillColor(Color.BLACK);
                    nLatLngs.clear();
                    for (Marker marker1 : markerList) marker1.remove();
                    incr = true;
                }
            }
        });
        addloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PolygonOptions polygonOptions = new PolygonOptions().addAll(mLatLngs).clickable(true);
//                boolean inside = PolyUtil.containsLocation(new LatLng(...), poly, true);
                mPolygon = mMap.addPolygon(polygonOptions);
                mPolygon.setTag("First Location");
                mPolygon.setStrokeColor(Color.BLACK);
                mPolygon.setFillColor(Color.BLACK);
                mLatLngs.clear();
                for (Marker marker : markerList) marker.remove();
                incr = true;
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPolygon != null) mPolygon.remove();
                for (Marker marker : markerList) marker.remove();
                mLatLngs.clear();
            }
        });
        loca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = true;

            }
        });

    }

    private void opendialog() {
        Dialog mdialog = new Dialog();
        mdialog.show(getSupportFragmentManager(),"Example Dialog");
    }

    @Override
    public void applyTexts(String name) {
        for (int j = 0; j < 4; j++) {
            String key1 = j + "point";
            polylocation pol1 = new polylocation(mLatLngs.get(j).latitude, mLatLngs.get(j).longitude);
            local.child(name).child(key1).setValue(pol1);
            result = name;
        }
    }
}