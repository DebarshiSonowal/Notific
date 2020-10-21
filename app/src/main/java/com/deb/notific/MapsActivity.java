package com.deb.notific;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.deb.notific.helper.polylocation;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.pd.chocobar.ChocoBar;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.shreyaspatil.MaterialDialog.MaterialDialog;
import com.shreyaspatil.MaterialDialog.interfaces.DialogInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, Dialog.DialogListener {
    //    private FitButton addloc, clear, loca;
    private GoogleMap mMap;
    private Marker marker;
    private LatLng latLng;
    private String result;
    private Polygon mPolygon, nPolygon;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private List<Marker> markerList = new ArrayList<>();
    private DatabaseReference root, local;
    LocationManager locationManager;
    private Boolean flag = false, incr = false;
    LocalBroadcastManager mBroadcastManager;
    BroadcastReceiver mBroadcastReceiver;
    private List<LatLng> mLatLngs = new ArrayList<>();
    private List<polylocation> nLatLngs = new ArrayList<>();
    SupportMapFragment mapFragment;
    AutocompleteSupportFragment placeAutoComplete;
    String uid;
    EditText mSearchView;
    FirebaseAuth mFirebaseAuth;
    ImageView searchbtn;
    InterstitialAd mInterstitialAd;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateZoom(MapsActivity.this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        MobileAds.initialize(this,
                "ca-app-pub-4889618202360309~3326007238");
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-4889618202360309/3281132281");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.

                getFragmentManager().popBackStackImmediate();
            }

        });
//        placeAutoComplete =  (AutocompleteSupportFragment)
//                getSupportFragmentManager().findFragmentById(R.id.placee);
//        Places.initialize(this, "AIzaSyDqBKeeXyrhK10zVr6z9bejxSf4a0h1z-k");
//        PlacesClient placesClient = Places.createClient(this);
        mFirebaseAuth = FirebaseAuth.getInstance();
        uid = mFirebaseAuth.getCurrentUser().getUid();
        searchbtn = findViewById(R.id.searchbtn);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mSearchView = findViewById(R.id.search);
        locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mBroadcastManager = LocalBroadcastManager.getInstance(this);
        init();
        //Connecting with view
//        addloc = findViewById(R.id.marklocbtn);
//        clear = findViewById(R.id.clrbtn);
//        placeAutoComplete.setPlaceFields(Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ID, com.google.android.libraries.places.api.model.Place.Field.NAME));
//        placeAutoComplete.setOnPlaceSelectedListener(new com.google.android.libraries.places.widget.listener.PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(@NonNull com.google.android.libraries.places.api.model.Place place) {
//                CameraPosition cameraPosition = new CameraPosition.Builder().target(place.getLatLng()).zoom(18).build();
//                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//                Log.d("Maps", "Place selected: " + place.getName());
//            }
//
//            @Override
//            public void onError(@NonNull Status status) {
//
//            }
//        });

//        mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                geoLocate(query);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });
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
            mBroadcastManager.registerReceiver(mBroadcastReceiver = new BroadcastReceiver() {
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
        locationManager = null;
        mapFragment = null;
        root = null;
        mBroadcastManager.unregisterReceiver(mBroadcastReceiver);
        mMap = null;
        System.gc();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setBuildingsEnabled(true);
        mMap.setMinZoomPreference(1.0f);
        mMap.setMaxZoomPreference(25.0f);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
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

    }

    private void opendialog() {
        Dialog mdialog = new Dialog();
        mdialog.show(getSupportFragmentManager(), "Example Dialog");
    }

    @Override
    public void applyTexts(String name) {
        for (int j = 0; j < 4; j++) {
            String key1 = j + "point";
            polylocation pol1 = new polylocation(mLatLngs.get(j).latitude, mLatLngs.get(j).longitude);
            local.child(uid).child(name).child(key1).setValue(pol1).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    try {
                        mInterstitialAd.loadAd(new AdRequest.Builder().build());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ChocoBar.builder().setActivity(MapsActivity.this)
                            .setText("New location added")
                            .setDuration(ChocoBar.LENGTH_SHORT)
                            .setActionText(android.R.string.ok)
                            .green()   // in built red ChocoBar
                            .show();

                    mInterstitialAd.show();
                }
            });
            result = name;
        }
    }

    private void geoLocate(String search1) {
        Log.d("TAG123", "geoLocate: geolocating");

        String searchString = search1;

        Geocoder geocoder = new Geocoder(this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.e("TAG123", "geoLocate: IOException: " + e.getMessage());
        }
        if (list.size() > 0) {
            Address address = list.get(0);
            Log.d("TAG123", "geoLocate: found a location: " + address.toString());
            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(address.getLatitude(),address.getLongitude())).zoom(18).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//                mMap.addMarker(new MarkerOptions()).setPosition(new LatLng(address.getLatitude(),address.getLongitude()));
            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(address.getLatitude(),address.getLongitude())).draggable(true);
            Marker marker = mMap.addMarker(markerOptions);
//            marker = mMap.addMarker(new MarkerOptions().position(latLng).title(result).icon(BitmapDescriptorFactory.fromResource(R.drawable.purple)));
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();

        }else
        {
            FancyToast.makeText(this,"Not found",FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show();
            mSearchView.setText("");
        }

    }

    private void init() {


        mSearchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {

                    //execute our method for searching
                    geoLocate(mSearchView.getText().toString());
                }


                return false;
            }
        });
        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                geoLocate(mSearchView.getText().toString());
                Log.d("TAG123", "init: initializing");
            }
        });
    }
}