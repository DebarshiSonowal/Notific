package com.deb.notific.ui.home;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Geocoder;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.deb.notific.Main2Activity;
import com.deb.notific.MyService;
import com.deb.notific.R;
import com.deb.notific.helper.BusStation;
import com.deb.notific.helper.State;
import com.deb.notific.helper.message;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.otto.Subscribe;

import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {
AudioManager mAudioManager;
Main2Activity mMain2Activity;
    TextView ringm,nomark,usenm,loc,totaluse,misscall,state ;

    @Override
    public void onStart() {
        super.onStart();
        getexecuted();
    }

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
//        mMain2Activity = (Main2Activity) getActivity();
        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        state = root.findViewById(R.id.statusview);
        usenm = root.findViewById(R.id.unameview);
         nomark = root.findViewById(R.id.nolocview);
        ringm = root.findViewById(R.id.ringmodview);
        loc = root.findViewById(R.id.locview);
        totaluse  = root.findViewById(R.id.totlauview);
       misscall = root.findViewById(R.id.nocallview);

        if(mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL)
        {
            ringm.setText("Normal");
        }
        else if(mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE)
        {
            ringm.setText("Vibrate");
        }
        else if(mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT)
            ringm.setText("Silent");

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
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
                        if(status1.equals("true"))
                        {
                            state.setText("Inside");
                        }else if(status1.equals("false"))
                            state.setText("Outside");

                    }
                }, new IntentFilter(MyService.ACTION_LOCATION_BROADCAST)
        );
//        Timer timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//                                      @Override
//                                      public void run() {
//                                          if(mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL)
//                                          {
//                                              ringm.setText("Normal");
//                                          }
//                                          else if(mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE)
//                                          {
//                                              ringm.setText("Vibrate");
//                                          }
//                                          else if(mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT)
//                                              ringm.setText("Silent");
//                                          LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
//                                                  new BroadcastReceiver() {
//                                                      @Override
//                                                      public void onReceive(Context context, Intent intent) {
//                                                          String latitude = intent.getStringExtra(MyService.EXTRA_LATITUDE);
//                                                          String longitude = intent.getStringExtra(MyService.EXTRA_LONGITUDE);
//                                                          String name = intent.getStringExtra("Name");
//                                                          String status1 = intent.getStringExtra("STATE");
//
//                                                          if (latitude != null && longitude != null) {
//                                                              loc.setText(name);
//                                                          }
//                                                          if(status1.equals("true"))
//                                                          {
//                                                              state.setText("Inside");
//                                                          }else if(status1.equals("false"))
//                                                              state.setText("Outside");
//
//                                                      }
//                                                  }, new IntentFilter(MyService.ACTION_LOCATION_BROADCAST)
//                                          );
//                                      }
//                                  },
//                0, 1000);   // 1000 Millisecond  = 1 second




        return root;

    }
    @Override
    public void onResume() {
        super.onResume();

        getexecuted();

    }

    private void getexecuted() {
        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        Log.d("Pause","It is resume");
        if(mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL)
        {
            ringm.setText("Normal");
        }
        else if(mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE)
        {
            ringm.setText("Vibrate");
        }
        else if(mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT)
            ringm.setText("Silent");

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Log.d("Pause","Received");
                        String latitude = intent.getStringExtra(MyService.EXTRA_LATITUDE);
                        String longitude = intent.getStringExtra(MyService.EXTRA_LONGITUDE);
                        String name = intent.getStringExtra("Name");
                        String status1 = intent.getStringExtra("STATE");

                        if (latitude != null && longitude != null) {
                            loc.setText(name);
                        }
                        if(status1.equals("true"))
                        {
                            state.setText("Inside");
                        }else if(status1.equals("false"))
                            state.setText("Outside");

                    }
                }, new IntentFilter(MyService.ACTION_LOCATION_BROADCAST)
        );
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("Pause","It is paused");
//        BusStation.getBus().unregister(this);
    }
//    @Subscribe
//    public  void receivedMessage(message message){
//        loc.setText(message.getMessage());
//    }
//
//    @Subscribe
//    public  void receivedState(State status){
//        if(status.getState()){
//            state.setText("Inside");
//        }
//        else
//            state.setText("Outside");
//    }

}
