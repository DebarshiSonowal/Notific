package com.deb.notific;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Spinner;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.labo.kaji.fragmentanimations.MoveAnimation;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.shreyaspatil.MaterialDialog.MaterialDialog;
import com.shreyaspatil.MaterialDialog.interfaces.DialogInterface;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
@SuppressWarnings("unchecked")
public class MarkedLocation extends Fragment {
    List<LatLng> mLatLngs = new ArrayList<>();
    List<LatLng> mLatLngList = new ArrayList<>();
    LatLng mLatLng;
    DatabaseReference local;
    Double lat, lon;
    Polygon mPolygon;
    SupportMapFragment mapFragment;
    ValueEventListener mValueEventListener;
    NiceSpinner mSpinner;
    int j = 0, k = 0;
    int a;
    List<String>mArraylist;
    ArrayAdapter<String> mAdapter;
    List<String> arrayList = new ArrayList<>();
    Dictionary mDictionary = new Hashtable<String, LatLng>();
    String uid;
    FirebaseAuth mAuth;
    PlaceAutocompleteFragment placeAutoComplete;
    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(final GoogleMap googleMap) {

            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            googleMap.setBuildingsEnabled(true);
            googleMap.setMinZoomPreference(1.0f);
            googleMap.setMaxZoomPreference(25.0f);
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setCompassEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);

            CameraUpdateFactory.scrollBy(6, 6);
//           networkop runnable = new networkop(googleMap);
//           new Thread(runnable).start();
            local = FirebaseDatabase.getInstance().getReference();
            local.child("Marked Location").child(uid).addValueEventListener(mValueEventListener =  new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                        {

                            for(DataSnapshot dataSnapshot2:dataSnapshot1.getChildren())
                            {
                                for(DataSnapshot dataSnapshot3:dataSnapshot2.getChildren())
                                {
                                    if(dataSnapshot3.getKey().equals("latitude")){
                                        lat = Double.parseDouble(dataSnapshot3.getValue().toString()) ;
                                        Log.d("Map",lat+"");
                                    }
                                    else if(dataSnapshot3.getKey().equals("longitude"))
                                    {
                                        lon = Double.parseDouble(dataSnapshot3.getValue().toString()) ;
                                        Log.d("Map",lon+"");
                                    }
                                }
                                mLatLng = new LatLng(lat, lon);
                                mLatLngs.add(mLatLng);

                            }
                            arrayList.add(dataSnapshot1.getKey().toString());
                            PolygonOptions polygonOptions = new PolygonOptions().addAll(mLatLngs).clickable(true);
                            mPolygon = googleMap.addPolygon(polygonOptions);
                            mPolygon.setTag(dataSnapshot1.getKey());
                            mPolygon.setStrokeColor(Color.BLACK);
                            mPolygon.setFillColor(Color.BLACK);;
                            mLatLngList.addAll(mLatLngs);
                            mLatLngs.clear();
                        }
                        mArraylist = new LinkedList<>(arrayList);
                        mSpinner.attachDataSource(mArraylist);
                        mAdapter.notifyDataSetChanged();
                    }else {

                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            for (j=0;j<arrayList.size();j++) {
                mDictionary.put(arrayList.get(j),mLatLngs.get(k));
                k=j+4;
            }
            mSpinner.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
                @Override
                public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                        a = 4*(position+1) - 1;
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(mLatLngList.get(a)).zoom(18).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            });
//            mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//                        a = 4*(position+1) - 1;
//                        CameraPosition cameraPosition = new CameraPosition.Builder().target(mLatLngList.get(a)).zoom(18).build();
//                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//
//
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> parent) {
//                }
//            });

        }
    };

    @Override
    public void onDetach() {
        super.onDetach();
    }
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
//        if(enter){
            return MoveAnimation.create(MoveAnimation.RIGHT, enter, 500);
//        }else
//            return MoveAnimation.create(MoveAnimation.LEFT, enter, 1000);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        local.child("Marked Location").removeEventListener(mValueEventListener);
        mapFragment = null;
        System.gc();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root =inflater.inflate(R.layout.fragment_marked_location, container, false);

        mSpinner = root.findViewById(R.id.nice_spinner);
        mAuth = FirebaseAuth.getInstance();
        mAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item,arrayList);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        uid = mAuth.getCurrentUser().getUid();

//        placeAutoComplete = (PlaceAutocompleteFragment) getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete);
//        placeAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(Place place) {
//
//                Log.d("Maps", "Place selected: " + place.getName());
//            }
//
//            @Override
//            public void onError(Status status) {
//                Log.d("Maps", "An error occurred: " + status);
//            }
//        });


        // Show Dialog

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }


    }


}
