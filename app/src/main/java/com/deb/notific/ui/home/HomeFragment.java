package com.deb.notific.ui.home;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import com.deb.notific.MyService;
import com.deb.notific.R;
import com.deb.notific.call_sms;
import com.deb.notific.helper.Contract;
import com.deb.notific.helper.DatabaseHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.wooplr.spotlight.utils.SpotlightSequence;

import java.lang.ref.WeakReference;



public class HomeFragment extends Fragment {
    AudioManager mAudioManager;

    private static final String FIRST = "permission";
    private static final String SECOND = "settings";
    private static final String THIRD = "try";
    private static final String FOURTH = "tradad";
    private static final String FIFTH = "tragagaga";
    private static final String SIXTH = "tr14314adad";
    Handler mHandler = new Handler();
    Long  count;
    FirebaseUser mUser;
    SharedPreferences preferences;
    Dataoperation mdata;
    BroadcastReceiver mBroadcastReceiver;
    LocalBroadcastManager mBroadcastManager;
    Thread mThread;
    String latitude,longitude,name,status1;
    View root;
    Boolean mBoolean;
    FragmentManager fragMan;
    TextView ringm, nomark, usenm, loc, totaluse, misscall, state;
    ValueEventListener mValueEventListener;

    @Override
    public void onStart() {
        super.onStart();
        getexecuted();
        mBroadcastManager.registerReceiver(mBroadcastReceiver =
                        new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {
                                latitude = intent.getStringExtra(MyService.EXTRA_LATITUDE);
                                longitude = intent.getStringExtra(MyService.EXTRA_LONGITUDE);
                                name = intent.getStringExtra("Name");
                                status1 = intent.getStringExtra("STATE");

                                if (name != null) {
                                    try {
                                        loc.setText(name);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                if (status1.equals("true")) {
                                    try {
                                        state.setText("Inside");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
//                            PackageManager pm  = getActivity().getPackageManager();
//                            ComponentName componentName = new ComponentName( getActivity(), call_sms.class);
//
//                            pm.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//                                    PackageManager.DONT_KILL_APP);
                                    getexecuted();
                                } else if (status1.equals("false")){
                                    try {
                                        state.setText("Outside");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

//                        PackageManager pm  = getActivity().getPackageManager();
//                        ComponentName componentName = new ComponentName( getActivity(), call_sms.class);
//
//                        pm.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//                                PackageManager.DONT_KILL_APP);
                                getexecuted();
                            }
                        }, new IntentFilter(MyService.ACTION_LOCATION_BROADCAST)
        );
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
         preferences = getActivity().getSharedPreferences("instruction",Context.MODE_PRIVATE);
        mBoolean = preferences.getBoolean("first",true);
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
        if (mBoolean) {
            showinfo();
        }
        getCall();
        loadData();

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        FancyToast.makeText(getContext(),mUser.getUid(), FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,false).show();
        mdata = new Dataoperation(usenm,totaluse,nomark);
        mThread = new Thread(mdata);
        mThread.start();
        mBroadcastManager = LocalBroadcastManager.getInstance(getContext());
      getexecuted();
        mBroadcastManager.registerReceiver(mBroadcastReceiver =
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                     latitude = intent.getStringExtra(MyService.EXTRA_LATITUDE);
                     longitude = intent.getStringExtra(MyService.EXTRA_LONGITUDE);
                     name = intent.getStringExtra("Name");
                     status1 = intent.getStringExtra("STATE");

                        if (name != null) {
                            try {
                                loc.setText(name);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (status1.equals("true")) {
                            try {
                                state.setText("Inside");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            getexecuted();
                        } else if (status1.equals("false")){
                            try {
                                state.setText("Outside");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                            getexecuted();
                    }
                }, new IntentFilter(MyService.ACTION_LOCATION_BROADCAST)
        );
        misscall.setText(count.toString());
        return root;

    }

    private long getCall() {
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
      count = DatabaseUtils.queryNumEntries(db, Contract.MissedCalls.TABLE_NAME);
        db.close();
        return count;
    }

    private void showinfo() {
        SpotlightSequence.getInstance(getActivity(),null)
                .addSpotlight(root.findViewById(R.id.Status1),"Status","Here you can see if you are inside of any marked location",FIRST)
                .addSpotlight(root.findViewById(R.id.Noloc1),"Marked Locations","Here you can see how many locations you have marked",SECOND)
                .addSpotlight(root.findViewById(R.id.Ring1),"Ringer mode","Here you can see the current ringing mode of your phone",THIRD)
                .addSpotlight(root.findViewById(R.id.Loc1),"Current Location","Here you can see your current location",FOURTH)
                .addSpotlight(root.findViewById(R.id.Total1),"No Users","Here you can see how many people are using our app",FIFTH)
                .addSpotlight(root.findViewById(R.id.Nocall1),"No calls","Here you can see how many calls you have missed when you are inside",SIXTH)
                .startSequence();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("first",false);
        editor.commit();

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
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Values",Context.MODE_PRIVATE);
       name = sharedPreferences.getString("location","Searching");
        status1 = sharedPreferences.getString("status","true");
        try {
            loc.setText(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (status1.equals("true")) {
            try {
                state.setText("Inside");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (status1.equals("false"))
            try {
                state.setText("Outside");
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    private void getexecuted() {
        try {
            mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
            Log.d("Pause", "It is resume");
            if (mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
                ringm.setText("Normal");
            } else if (mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
                ringm.setText("Vibrate");
            } else if (mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT)
                ringm.setText("Silent");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("Pause", "It is paused");
        Context context;
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Vallues",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putString("location",name);
        editor.apply();
        editor.putString("status",status1);
        editor.commit();

    }

    public  static class Dataoperation implements Runnable {
        DatabaseReference local;
        FirebaseUser mUser;
        String name;
        long a;
        private final WeakReference<TextView> usenm,totaluse,nomark;

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

                    try {
                        usenm.get().setText(name);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


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
                    try {
                        totaluse.get().setText(String.valueOf(a));
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

            local.child("Marked Location").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
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

