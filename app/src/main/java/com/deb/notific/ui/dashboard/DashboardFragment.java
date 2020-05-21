package com.deb.notific.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleService;
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
import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;
import com.skydoves.balloon.OnBalloonClickListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

DatabaseReference rootref = FirebaseDatabase.getInstance().getReference();
    public static final String SWITCH = "onswitch";
    Adapter mAdapter;
    LifecycleOwner mLifecycleOwner;
ValueEventListener mValueEventListener;
    List<String>mList = new ArrayList<>();
    View root;
    Switch onswitch;
    ImageButton logbtn,location;
    LinearLayoutManager layoutManager;
    Balloon balloon;
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
         mLifecycleOwner = new LifecycleService();
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
        balloon = new Balloon.Builder(getContext())
                .setArrowSize(10)
                .setArrowOrientation(ArrowOrientation.TOP)
                .setArrowVisible(true)
                .setWidthRatio(1.0f)
                .setHeight(65)
                .setTextSize(15f)
                .setArrowPosition(0.62f)
                .setCornerRadius(4f)
                .setAlpha(0.9f)
                .setText("You can access your profile from now on.")
                .setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent))
                .setIconDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_home_black_24dp))
                .setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary))
                .setLifecycleOwner(mLifecycleOwner)
                .setOnBalloonClickListener(new OnBalloonClickListener() {
                    @Override
                    public void onBalloonClick(@NotNull View view) {
                        balloon.dismiss();
                    }
                })
                .setBalloonAnimation(BalloonAnimation.FADE)
                .build();

        balloon.dismissWithDelay(100L);
        onswitch.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
        balloon.show(onswitch);
                return false;
            }
        });
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
