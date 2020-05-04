package com.deb.notific;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.deb.notific.R;
import com.deb.notific.helper.LocationHelper;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, Dialog.DialogListener {
    private Button addloc, clear, loca;
    private GoogleMap mMap;
    private Marker marker;
    private LatLng latLng,mLng;
    private String result;
    private Polygon mPolygon;
    private LocationListener locationListener;
    private LocationManager locationManager;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private  List<Marker> markerList = new ArrayList<>();
    private DatabaseReference root, local;
    private Boolean flag = false,incr = false;
    private String key;
    private final static int polypoint = 5;
    private List<LatLng> mLatLngs = new ArrayList<>();
    private List<polylocation> nLatLngs = new ArrayList<>();
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);
        locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);

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
//            FetchLocation fetchLocation = new FetchLocation();
//            new Thread(fetchLocation).start();

            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                                          @Override
                                          public void run() {
                                              LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                                                      new BroadcastReceiver() {
                                                          @Override
                                                          public void onReceive(Context context, Intent intent) {
                                                              String lat = intent.getStringExtra(MyService.EXTRA_LATITUDE);
                                                              String lon = intent.getStringExtra(MyService.EXTRA_LONGITUDE);
                                                              String name = intent.getStringExtra("Name");
                                                              latLng = new LatLng(Double.parseDouble(lat) , Double.parseDouble(lon));
                                                              updateLoc(latLng,name);

                                                          }
                                                      }, new IntentFilter(MyService.ACTION_LOCATION_BROADCAST)
                                              );
                                          }
                                      },
                    0, 1000);   // 1000 Millisecond  = 1 second


        }
    }

    private void updateLoc(LatLng latLng,String result) {
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
//            FetchLocation fetchLocation = new FetchLocation();
//            new Thread(fetchLocation).start();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setBuildingsEnabled(true);
        mMap.setMinZoomPreference(1.0f);
        mMap.setMaxZoomPreference(25.0f);
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
                Log.d("MyService",latLng.toString());
                markerList.add(marker);
                if(mLatLngs.size() == 4)
                {
                    opendialog();
//                    String key = UUID.randomUUID().toString();
//                    for(int j=0;j<4;j++)
//                    {
//                        String key1 = UUID.randomUUID().toString();
//                        polylocation pol1 = new polylocation(mLatLngs.get(j).latitude,mLatLngs.get(j).longitude);
//                        local.child(key).child(key1).setValue(pol1);
//
//                    }
//                    mLatLngs.clear();
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
//                FetchLocation fetchLocation = new FetchLocation();
//                new Thread(fetchLocation).start();
            }
        });

    }

    private void opendialog() {
        Dialog mdialog = new Dialog();
        mdialog.show(getSupportFragmentManager(),"Example Dialog");
    }

    @Override
    public void applyTexts(String name) {
        for(int j=0;j<4;j++)
        {
            String key1 = UUID.randomUUID().toString();
            polylocation pol1 = new polylocation(mLatLngs.get(j).latitude,mLatLngs.get(j).longitude);
            local.child(name).child(key1).setValue(pol1);
        }
    }

//    class FetchLocation implements Runnable {
//        @Override
//        public void run() {
//            Looper.prepare();
//            Log.d("Thread","Started");
//            Toast.makeText( MapsActivity.this,"Started",Toast.LENGTH_SHORT).show();
//
//            locationListener = new LocationListener() {
//                @Override
//                public void onLocationChanged(Location location) {
//                    double latitude = location.getLatitude();
//                    double longitude = location.getLongitude();
//                    //get the location name from latitude and longitude
//                    Geocoder geocoder = new Geocoder(MapsActivity.this);
//                    try {
//                        List<Address> addresses =
//                                geocoder.getFromLocation(latitude, longitude, 1);
//                        result = addresses.get(0).getLocality() + ":";
//                        result += addresses.get(0).getCountryName();
//                        latLng = new LatLng(latitude, longitude);
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Log.d("Thread","Handler");
////                                if (marker != null) {
////                                    if (flag) {
////                                        marker.remove();
////                                        marker = mMap.addMarker(new MarkerOptions().position(latLng).title(result).icon(BitmapDescriptorFactory.fromResource(R.drawable.purple)));
////
////                                        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(20).build();
////                                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
////                                        flag = false;
////                                    }
////                                } else {
////                                    marker = mMap.addMarker(new MarkerOptions().position(latLng).title(result).icon(BitmapDescriptorFactory.fromResource(R.drawable.purple)));
////
////
////                                    CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(20).build();
////                                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
////
////                                }
//                            }
//                        });
////                        mHandler.post(new Runnable() {
////                            @Override
////                            public void run() {
////                                Log.d("Thread","Handler");
////                                if (marker != null) {
////                                    if (flag) {
////                                        marker.remove();
////                                        marker = mMap.addMarker(new MarkerOptions().position(latLng).title(result).icon(BitmapDescriptorFactory.fromResource(R.drawable.purple)));
////
////                                        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(20).build();
////                                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
////                                        flag = false;
////                                    }
////                                } else {
////                                    marker = mMap.addMarker(new MarkerOptions().position(latLng).title(result).icon(BitmapDescriptorFactory.fromResource(R.drawable.purple)));
////
////
////                                    CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(20).build();
////                                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
////
////                                }
////                            }
////                        });
//
//
//                    } catch (IOException e) {
//                        Log.d("Thread",e.toString());
//                    }
//                }
//
//                @Override
//                public void onStatusChanged(String provider, int status, Bundle extras) {
//
//                }
//
//                @Override
//                public void onProviderEnabled(String provider) {
//
//                }
//
//                @Override
//                public void onProviderDisabled(String provider) {
//
//                }
//            };
//            if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
//
//        }
//    }
}
