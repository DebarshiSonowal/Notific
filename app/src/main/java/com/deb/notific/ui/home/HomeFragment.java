package com.deb.notific.ui.home;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import com.deb.notific.MyService;
import com.deb.notific.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.WeakReference;

public class HomeFragment extends Fragment {
    AudioManager mAudioManager;
    Handler mHandler = new Handler();
    Long a, b;
    Dataoperation mdata;
    BroadcastReceiver mBroadcastReceiver;
    LocalBroadcastManager mBroadcastManager;
    Thread mThread;
    View root;
    FragmentManager fragMan;
    TextView ringm, nomark, usenm, loc, totaluse, misscall, state;
    ValueEventListener mValueEventListener;

    @Override
    public void onStart() {
        super.onStart();
        getexecuted();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        root = null;
        loc = null;
        mAudioManager = null;
        fragMan = null;
        misscall = null;
        mBroadcastManager.unregisterReceiver(mBroadcastReceiver);
//        mdata.local.child("user").removeEventListener(mValueEventListener);
//        mdata.local.child("user").child(mdata.mUser.getUid()).removeEventListener(mValueEventListener);
//        mdata.local.child("user").removeEventListener(mValueEventListener);
//        mHandler.removeCallbacks(mThread);
        mdata = null;
        nomark = null;
        ringm = null;
        totaluse = null;
        usenm = null;
        state = null;
        System.gc();

    }

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        fragMan = getChildFragmentManager();
        root = inflater.inflate(R.layout.fragment_home, container, false);
        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        state = root.findViewById(R.id.statusview);
        usenm = root.findViewById(R.id.unameview);
        nomark = root.findViewById(R.id.nolocview);
        ringm = root.findViewById(R.id.ringmodview);
        loc = root.findViewById(R.id.locview);
        totaluse = root.findViewById(R.id.totlauview);
        misscall = root.findViewById(R.id.nocallview);
        loadData();
        mdata = new Dataoperation(usenm,totaluse,nomark);
        mThread = new Thread(mdata);
        mThread.start();
        mBroadcastManager = LocalBroadcastManager.getInstance(getContext());
      getexecuted();
        mBroadcastManager.registerReceiver(mBroadcastReceiver =
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String latitude = intent.getStringExtra(MyService.EXTRA_LATITUDE);
                        String longitude = intent.getStringExtra(MyService.EXTRA_LONGITUDE);
                        String name = intent.getStringExtra("Name");
                        String status1 = intent.getStringExtra("STATE");

                        if (latitude != null && longitude != null) {
                            loc.setText(name);
                        }
                        if (status1.equals("true")) {
                            state.setText("Inside");
                        } else if (status1.equals("false"))
                            state.setText("Outside");

                    }
                }, new IntentFilter(MyService.ACTION_LOCATION_BROADCAST)
        );

        return root;

    }

    private void loadData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        state.setText(sharedPreferences.getString("State","Searching"));
        loc.setText(sharedPreferences.getString("Name","Searching"));
    }

    @Override
    public void onResume() {
        super.onResume();
        mThread = new Thread(mdata);
        mThread.start();
        getexecuted();

    }

    private void getexecuted() {
        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        Log.d("Pause", "It is resume");
        if (mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
            ringm.setText("Normal");
        } else if (mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
            ringm.setText("Vibrate");
        } else if (mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT)
            ringm.setText("Silent");

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("Pause", "It is paused");

    }

    public  static class Dataoperation implements Runnable {
        DatabaseReference local;
        FirebaseUser mUser;
        String name;
        long a;
        private final WeakReference<TextView> usenm,totaluse,nomark;
//        Handler mHandler = new Handler(Looper.getMainLooper());
        private long b;

        public Dataoperation(TextView textView,TextView mtext,TextView ntext) {
           this.usenm = new WeakReference<>(textView);
            this.totaluse = new WeakReference<>(mtext);
            this.nomark = new WeakReference<>(ntext);
        }

        @Override
        public void run() {
            Log.d("Thr", "Thread created");
            mUser = FirebaseAuth.getInstance().getCurrentUser();
            local = FirebaseDatabase.getInstance().getReference();
            local.child("user").child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (dataSnapshot1.getKey().equals("Username")) {
                            name = (String) dataSnapshot1.getValue();

                        }
                    }

                            usenm.get().setText(name);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            local.child("user").addValueEventListener( new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    a = dataSnapshot.getChildrenCount();
//                    mHandler.post(new Runnable() {
//                        @SuppressLint("SetTextI18n")
//                        @Override
//                        public void run() {
                            totaluse.get().setText(String.valueOf(a));
//                        }
//                    });


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            local.child("Marked Location").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d("Thr", "loc no");
                    b = dataSnapshot.getChildrenCount();
//                    mHandler.post(new Runnable() {
//                        @SuppressLint("SetTextI18n")
//                        @Override
//                        public void run() {
                            Log.d("Thr", "set loc no");
                    try {
                        nomark.get().setText(String.valueOf(b));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                        }
//                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
//            mHandler.removeCallbacks(this);
        }
    }
}

