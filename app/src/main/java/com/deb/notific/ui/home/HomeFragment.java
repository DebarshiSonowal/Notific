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
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.deb.notific.MyService;
import com.deb.notific.R;
import com.deb.notific.call_sms;
import com.deb.notific.helper.Contract;
import com.deb.notific.helper.DatabaseHelper;
import com.deb.notific.login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.labo.kaji.fragmentanimations.CubeAnimation;
import com.labo.kaji.fragmentanimations.MoveAnimation;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.skydoves.balloon.ArrowConstraints;
import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;
import com.skydoves.balloon.OnBalloonClickListener;
import com.skydoves.elasticviews.ElasticImageView;
import com.wooplr.spotlight.utils.SpotlightSequence;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class HomeFragment extends Fragment {
    AudioManager mAudioManager;
    ElasticImageView logbtn;
    private static final String FIRST = "permission";
    private static final String SECOND = "settings";
    private static final String THIRD = "try";
    private static final String FOURTH = "tradad";
    private static final String FIFTH = "tragagaga";
    private static final String SIXTH = "tr14314adad";
    Handler mHandler = new Handler();
    Long  count;
    Balloon statusballon,locballon,ringmodebaloon,currentballon,userballon,callballon;
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
    TextView ringm, nomark, usenm, loc, totaluse, misscall, state,date;
    ValueEventListener mValueEventListener;
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
//        if(enter){
          return MoveAnimation.create(MoveAnimation.RIGHT, enter, 500);
//        }else
//            return MoveAnimation.create(MoveAnimation.LEFT, enter, 1000);
    }
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
         Date m = new Date();
         SimpleDateFormat sdf=  new SimpleDateFormat("dd,MMMM,yyyy", Locale.getDefault());
        mBoolean = preferences.getBoolean("first",true);
        fragMan = getChildFragmentManager();
        root = inflater.inflate(R.layout.fragment_home, container, false);
        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        logbtn=root.findViewById(R.id.logout);
        state = root.findViewById(R.id.statusview);
        usenm = root.findViewById(R.id.unameview);
        nomark = root.findViewById(R.id.nolocview);
        ringm = root.findViewById(R.id.ringmodview);
        loc = root.findViewById(R.id.locview);
        totaluse = root.findViewById(R.id.totlauview);
        misscall = root.findViewById(R.id.nocallview);
        date = root.findViewById(R.id.date);
        date.setText(sdf.format(m));
        logbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().stopService(new Intent(getContext(),MyService.class));
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), login.class));
                Animatoo.animateZoom(getContext());
            }
        });
        if (mBoolean) {
            try {
                showinfo();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        getCall();
        loadData();

        mUser = FirebaseAuth.getInstance().getCurrentUser();
//        FancyToast.makeText(getContext(),mUser.getUid(), FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,false).show();
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

        //Ballons
       statusballon = new Balloon.Builder(getContext())
                .setArrowSize(10)
               .setArrowOrientation(ArrowOrientation.BOTTOM)
                .setArrowConstraints(ArrowConstraints.ALIGN_ANCHOR)
                .setArrowPosition(0.5f)
                .setArrowVisible(true)
               .setWidth(300)
                .setHeight(65)
                .setTextSize(15f)
                .setCornerRadius(4f)
                .setAlpha(0.9f)
                .setText("Here you can see the status of your location")
                .setTextColor(Color.parseColor("#FFFFFF"))
                .setTextIsHtml(true)
                .setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary))
                .setOnBalloonClickListener(new OnBalloonClickListener() {
                    @Override
                    public void onBalloonClick(@NotNull View view) {
                        statusballon.dismissWithDelay(500);
                    }
                })
                .setBalloonAnimation(BalloonAnimation.FADE)
               .setAutoDismissDuration(1500)
               .setDismissWhenTouchOutside(true)
                .build();

        locballon = new Balloon.Builder(getContext())
                .setArrowSize(10)
                .setArrowOrientation(ArrowOrientation.BOTTOM)
                .setArrowConstraints(ArrowConstraints.ALIGN_ANCHOR)
                .setArrowPosition(0.5f)
                .setArrowVisible(true)
                .setWidth(300)
                .setHeight(65)
                .setTextSize(15f)
                .setCornerRadius(4f)
                .setAlpha(0.9f)
                .setText("Here you can see the no of location you set")
                .setTextIsHtml(true)
                .setTextColor(Color.parseColor("#FFFFFF"))
                .setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary))
                .setOnBalloonClickListener(new OnBalloonClickListener() {
                    @Override
                    public void onBalloonClick(@NotNull View view) {
                        locballon.dismissWithDelay(500);
                    }
                })
                .setDismissWhenTouchOutside(true)
                .setAutoDismissDuration(1500)
                .setBalloonAnimation(BalloonAnimation.FADE)
                .build();

        ringmodebaloon = new Balloon.Builder(getContext())
                .setArrowSize(10)
                .setArrowOrientation(ArrowOrientation.BOTTOM)
                .setArrowConstraints(ArrowConstraints.ALIGN_ANCHOR)
                .setArrowPosition(0.5f)
                .setArrowVisible(true)
                .setWidth(300)
                .setHeight(65)
                .setTextSize(15f)
                .setCornerRadius(4f)
                .setAlpha(0.9f)
                .setText("Here you can see the status of your phone")
                .setTextIsHtml(true)
                .setTextColor(Color.parseColor("#FFFFFF"))
                .setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary))
                .setOnBalloonClickListener(new OnBalloonClickListener() {
                    @Override
                    public void onBalloonClick(@NotNull View view) {
                        ringmodebaloon.dismissWithDelay(500);
                    }
                })
                .setDismissWhenTouchOutside(true)
                .setAutoDismissDuration(1500)
                .setBalloonAnimation(BalloonAnimation.FADE)
                .build();

        currentballon = new Balloon.Builder(getContext())
                .setArrowSize(10)
                .setArrowOrientation(ArrowOrientation.BOTTOM)
                .setArrowConstraints(ArrowConstraints.ALIGN_ANCHOR)
                .setArrowPosition(0.5f)
                .setArrowVisible(true)
                .setWidth(300)
                .setHeight(65)
                .setTextSize(15f)
                .setCornerRadius(4f)
                .setAlpha(0.9f)
                .setText("Here you can see your current status location")
                .setTextIsHtml(true)
                .setTextColor(Color.parseColor("#FFFFFF"))
                .setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary))
                .setOnBalloonClickListener(new OnBalloonClickListener() {
                    @Override
                    public void onBalloonClick(@NotNull View view) {
                        currentballon.dismissWithDelay(500);
                    }
                })
                .setDismissWhenTouchOutside(true)
                .setAutoDismissDuration(1500)
                .setBalloonAnimation(BalloonAnimation.FADE)
                .build();

        userballon = new Balloon.Builder(getContext())
                .setArrowSize(10)
                .setArrowOrientation(ArrowOrientation.BOTTOM)
                .setArrowConstraints(ArrowConstraints.ALIGN_ANCHOR)
                .setArrowPosition(0.5f)
                .setArrowVisible(true)
                .setWidth(300)
                .setHeight(65)
                .setTextSize(15f)
                .setCornerRadius(4f)
                .setAlpha(0.9f)
                .setText("Here you can see the no of users of this app")
                .setTextIsHtml(true)
                .setTextColor(Color.parseColor("#FFFFFF"))
                .setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary))
                .setOnBalloonClickListener(new OnBalloonClickListener() {
                    @Override
                    public void onBalloonClick(@NotNull View view) {
                        userballon.dismissWithDelay(500);
                    }
                })
                .setAutoDismissDuration(1500)
                .setDismissWhenTouchOutside(true)
                .setBalloonAnimation(BalloonAnimation.FADE)
                .build();

        callballon = new Balloon.Builder(getContext())
                .setArrowSize(10)
                .setArrowOrientation(ArrowOrientation.BOTTOM)
                .setArrowConstraints(ArrowConstraints.ALIGN_ANCHOR)
                .setArrowPosition(0.5f)
                .setArrowVisible(true)
                .setWidth(300)
                .setHeight(65)
                .setTextSize(15f)
                .setCornerRadius(4f)
                .setAlpha(0.9f)
                .setText("Here you can see the no of missed calls")
                .setTextIsHtml(true)
                .setTextColor(Color.parseColor("#FFFFFF"))
                .setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary))
                .setOnBalloonClickListener(new OnBalloonClickListener() {
                    @Override
                    public void onBalloonClick(@NotNull View view) {
                        callballon.dismissWithDelay(500);
                    }
                })
                .setDismissWhenTouchOutside(true)
                .setAutoDismissDuration(1500)
                .setBalloonAnimation(BalloonAnimation.FADE)
                .build();

//
        state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusballon.show(root.findViewById(R.id.statusview));
            }
        });
        loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locballon.show(root.findViewById(R.id.nolocview));
            }
        });
        ringm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ringmodebaloon.show(root.findViewById(R.id.ringmodview));
            }
        });
        loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentballon.show(root.findViewById(R.id.locview));
            }
        });
        totaluse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userballon.show(root.findViewById(R.id.totlauview));
            }
        });
        misscall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callballon.show(root.findViewById(R.id.nocallview));
            }
        });
        nomark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locballon.show(root.findViewById(R.id.nolocview));
            }
        });



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
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }

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
                        usenm.get().setText("Hello,"+name);
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

