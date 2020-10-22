package com.deb.notific.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleService;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.deb.notific.About;
import com.deb.notific.Adapter;
import com.deb.notific.MapsActivity;
import com.deb.notific.MyService;
import com.deb.notific.R;
import com.deb.notific.helper.polylocation;
import com.deb.notific.login;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;
import com.labo.kaji.fragmentanimations.MoveAnimation;
import com.pd.chocobar.ChocoBar;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.skydoves.balloon.ArrowConstraints;
import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;
import com.skydoves.balloon.OnBalloonClickListener;
import com.skydoves.balloon.OnBalloonDismissListener;
import com.skydoves.elasticviews.ElasticImageView;
import com.vlonjatg.progressactivity.ProgressRelativeLayout;
import com.wooplr.spotlight.utils.SpotlightSequence;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {
DatabaseReference rootref = FirebaseDatabase.getInstance().getReference();
    private static final String FIRST = "permissioadasdn";
    private static final String SECOND = "sedfhedttings";
    private static final String THIRD = "trh4eyhy";
    private static final String FOURTH = "t2345radad";
    public static final String SWITCH = "onswitch";
    Adapter mAdapter;
    String getResult;
    AdView mAdView;
   Balloon mMark,mAbout,mlog;
    TextView mark,abouttext,log;
    LabeledSwitch labeledSwitch;
    LifecycleOwner mLifecycleOwner;
ValueEventListener mValueEventListener;
    List<String>mList,area;
    ProgressRelativeLayout mEmptyView;
    View root;
RecyclerView mRecyclerView;
    Boolean mBoolean;
    Switch onswitch;
    AudioManager mAudioManager;
    ElasticImageView logbtn,location,about;
    LinearLayoutManager layoutManager;
    private List<LatLng> mLatLngList;

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

    @Override
    public void onStart() {
        super.onStart();
        rootref.child("Marked Location").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(
                mValueEventListener= new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        area = new ArrayList<>();
                        if (dataSnapshot.exists()) {
                            try {
                                mList.clear();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            mLatLngList = new ArrayList<>();
                            try {
                                area.clear();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                                mList.add(dataSnapshot1.getKey());
                                Log.d("Recycler",dataSnapshot1.getKey());
                                for(DataSnapshot dataSnapshot2:dataSnapshot1.getChildren()){
                                   LatLng m = new LatLng(dataSnapshot2.getValue(polylocation.class).getLatitude(),dataSnapshot2.getValue(polylocation.class).getLongitude());
                                    Log.d("Experiment",m.toString());
                                   mLatLngList.add(m);
                                   if(mLatLngList.size() == 4){
                                       mLatLngList.add(mLatLngList.get(0));
                                       String a = String.valueOf((Math.round(SphericalUtil.computeArea(mLatLngList))));
                                       Log.d("Eala",a);
                                       mLatLngList.clear();
                                       area.add(a);
                                       break;
                                   }
                                }
                            }
                            if(mList.isEmpty()){
                                mEmptyView.showEmpty(R.drawable.ic_empty_box_open,"No items in the cart","Add items to the cart");
                            }else
                                mEmptyView.showContent();
//                            mLatLngList
                            Log.d("Experiment",area.get(0));
                            mAdapter = new Adapter(getContext(),mList,area);
                            mRecyclerView.setAdapter(mAdapter);
                            layoutManager = new LinearLayoutManager(getContext());
                            mRecyclerView.setLayoutManager(layoutManager);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
//        if(enter){
            return MoveAnimation.create(MoveAnimation.RIGHT, enter, 500);
//        }else
//            return MoveAnimation.create(MoveAnimation.LEFT, enter, 1000);
    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        mRecyclerView = root.findViewById(R.id.recyclerView);
        mEmptyView = root.findViewById(R.id.loadingLayout);
       mList= new ArrayList<>();
      location = root.findViewById(R.id.locbtn);
        MobileAds.initialize(getContext(),new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });
        mAdView = root.findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mark = root.findViewById(R.id.textView28);
        abouttext = root.findViewById(R.id.abouttext);
        log = root.findViewById(R.id.log);
        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
         logbtn = root.findViewById(R.id.logoutbtn);
//         onswitch = root.findViewById(R.id.onswitch);
         mLifecycleOwner = new LifecycleService();
         about =   root.findViewById(R.id.addbtn);
         layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new Adapter(getContext(),mList,area);
        mRecyclerView.setAdapter(mAdapter);
        labeledSwitch = root.findViewById(R.id.onswitch);
        labeledSwitch.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                savestate();
                if(!isOn){
                    mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                }
            }

        });
        mMark = new Balloon.Builder(getContext())
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
                .setText("Here you can mark new areas")
                .setTextColor(Color.parseColor("#FFFFFF"))
                .setTextIsHtml(true)
                .setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary))
                .setOnBalloonClickListener(new OnBalloonClickListener() {
                    @Override
                    public void onBalloonClick(@NotNull View view) {
                        mMark.dismissWithDelay(500);
                    }
                })
                .setBalloonAnimation(BalloonAnimation.FADE)
                .setAutoDismissDuration(1500)
                .setDismissWhenTouchOutside(true)
                .build();

        mAbout = new Balloon.Builder(getContext())
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
                .setText("Here you can see about the app")
                .setTextColor(Color.parseColor("#FFFFFF"))
                .setTextIsHtml(true)
                .setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary))
                .setOnBalloonClickListener(new OnBalloonClickListener() {
                    @Override
                    public void onBalloonClick(@NotNull View view) {
                        mAbout.dismissWithDelay(500);
                    }
                })
                .setBalloonAnimation(BalloonAnimation.FADE)
                .setAutoDismissDuration(1500)
                .setDismissWhenTouchOutside(true)
                .build();
        mlog = new Balloon.Builder(getContext())
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
                .setText("Logout")
                .setTextColor(Color.parseColor("#FFFFFF"))
                .setTextIsHtml(true)
                .setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary))
                .setOnBalloonClickListener(new OnBalloonClickListener() {
                    @Override
                    public void onBalloonClick(@NotNull View view) {
                        mlog.dismissWithDelay(500);
                    }
                })
                .setBalloonAnimation(BalloonAnimation.FADE)
                .setAutoDismissDuration(1500)
                .setDismissWhenTouchOutside(true)
                .build();

            mMark.show(mark);
            mMark.setOnBalloonDismissListener(new OnBalloonDismissListener() {
                @Override
                public void onBalloonDismiss() {
                    mAbout.show(abouttext);
                }
            });
            mAbout.setOnBalloonDismissListener(new OnBalloonDismissListener() {
                @Override
                public void onBalloonDismiss() {
                    mlog.show(log);
                }
            });
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), About.class));
                Animatoo.animateZoom(getContext());
            }
        });
        SharedPreferences preferences = getContext().getSharedPreferences("dash",Context.MODE_PRIVATE);
         mBoolean= preferences.getBoolean("first4",true);
//        FancyToast.makeText(getContext(),mBoolean.toString(), FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,true).show();
        if (mBoolean) {
            try {
                startinfo();
            } catch (Exception e) {
                e.printStackTrace();
            }
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("first4",false);
            editor.commit();
        }
//        onswitch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        loadData();
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerview, RecyclerView.ViewHolder viewholder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewholder, int direction) {
                removeItem((String) viewholder.itemView.getTag());
            }
        }).attachToRecyclerView(mRecyclerView);

        logbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().stopService(new Intent(getContext(),MyService.class));
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), login.class));
                Animatoo.animateZoom(getContext());
            }
        });
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MapsActivity.class);
                startActivity(intent);
                Animatoo.animateZoom(getContext());
            }
        });
        return root;
    }

    private void removeItem(String tag) {
        rootref.child("Marked Location").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(tag).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                FancyToast.makeText(getContext(),"Successfully deleated",FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,true);
                mAdapter.notifyDataSetChanged();
                mRecyclerView.invalidate();
            }
        });


    }

    private void startinfo() {
        SpotlightSequence.getInstance(getActivity(),null)
                .addSpotlight(root.findViewById(R.id.locbtn),"Mark Location","Here you can mark new locations",FIRST)
                .addSpotlight(root.findViewById(R.id.onswitch),"Service","Here you can turn the service on or off",SECOND)
                .addSpotlight(root.findViewById(R.id.logoutbtn),"Log out","Here you can log out from your current account",THIRD)
                .addSpotlight(root.findViewById(R.id.recyclerView),"Marked Locations","You can view all of your marked locations here",FOURTH)
                .startSequence();

    }

    private void loadData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
//        onswitch.setChecked();
        labeledSwitch.setOn(sharedPreferences.getBoolean(SWITCH,true));
    }

    private void savestate() {
        if (labeledSwitch.isOn()) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.putBoolean(SWITCH,true);
            editor.commit();
//            onswitch.setChecked(true);
            labeledSwitch.setOn(true);
            Intent intent = new Intent(getContext(), MyService.class);
            getContext().startService(intent);
            ChocoBar.builder().setActivity(getActivity())
                    .setText("Service Started")
                    .setDuration(ChocoBar.LENGTH_SHORT)
                    .setActionText(android.R.string.ok)
                    .green()   // in built red ChocoBar
                    .show();
        } else {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.putBoolean(SWITCH,false);
            editor.commit();
//            onswitch.setChecked(false);
            labeledSwitch.setOn(false);
            Intent intent = new Intent(getContext(), MyService.class);
            getContext().stopService(intent);
            ChocoBar.builder().setActivity(getActivity())
                    .setText("Stopped the service")
                    .setDuration(ChocoBar.LENGTH_SHORT)
                    .setActionText(android.R.string.ok)
                    .red()   // in built red ChocoBar
                    .show();

        }
    }
}
