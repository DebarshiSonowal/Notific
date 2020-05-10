package com.deb.notific.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deb.notific.Adapter;
import com.deb.notific.MapsActivity;
import com.deb.notific.MyService;
import com.deb.notific.R;
import com.deb.notific.login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

DatabaseReference rootref = FirebaseDatabase.getInstance().getReference();
    public static final String SWITCH = "onswitch";
    Adapter mAdapter;
ValueEventListener mValueEventListener;
    List<String>mList = new ArrayList<>();
    View root;
    Switch onswitch;
    Button logbtn,location;
    LinearLayoutManager layoutManager;
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rootref.child("Marked Location").removeEventListener(mValueEventListener);
        root = null;
        rootref = null;
        logbtn = null;
        location = null;
        layoutManager = null;
        System.gc();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_dashboard, container, false);
       final RecyclerView mRecyclerView = root.findViewById(R.id.recyclerView);
      location = root.findViewById(R.id.locbtn);
         logbtn = root.findViewById(R.id.logoutbtn);
         onswitch = root.findViewById(R.id.onswitch);
         layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new Adapter(getContext(),mList);
        mRecyclerView.setAdapter(mAdapter);
        onswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savestate();
            }
        });
        loadData();

        rootref.child("Marked Location").addValueEventListener(
               mValueEventListener= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                mList.add(dataSnapshot1.getKey());
                Log.d("Recycler",dataSnapshot1.getKey().toString());
            }
               mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
        logbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), login.class));
            }
        });
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MapsActivity.class);
                startActivity(intent);
            }
        });
        return root;
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        onswitch.setChecked(sharedPreferences.getBoolean(SWITCH,true));
    }

    private void savestate() {
        if (onswitch.isChecked()) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.putBoolean(SWITCH,true);
            editor.commit();
            onswitch.setChecked(true);
            Intent intent = new Intent(getContext(), MyService.class);
            getContext().startService(intent);
        } else {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.putBoolean(SWITCH,false);
            editor.commit();
            onswitch.setChecked(false);
            Intent intent = new Intent(getContext(), MyService.class);
            getContext().stopService(intent);
        }
    }
}
