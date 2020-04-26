package com.deb.notific.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.deb.notific.Main2Activity;
import com.deb.notific.R;
import com.deb.notific.helper.BusStation;
import com.deb.notific.helper.message;
import com.squareup.otto.Subscribe;

public class HomeFragment extends Fragment {

Main2Activity mMain2Activity;
    TextView ringm,nomark,usenm,loc,totaluse,misscall,state ;

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
//        mMain2Activity = (Main2Activity) getActivity();

        state = root.findViewById(R.id.statusview);
        usenm = root.findViewById(R.id.unameview);
         nomark = root.findViewById(R.id.nolocview);
        ringm = root.findViewById(R.id.ringmodview);
        loc = root.findViewById(R.id.locview);
        totaluse  = root.findViewById(R.id.totlauview);
       misscall = root.findViewById(R.id.nocallview);

//        if(mMain2Activity.am.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE)
//        {
//            ringm.setText("Vibrate");
//        }
//        else if(mMain2Activity.am.getRingerMode() == AudioManager.RINGER_MODE_NORMAL)
//        {
//            ringm.setText("Ringing");
//        }
//        else {
//            ringm.setText("Silent");
//        }
        return root;

    }
    @Override
    public void onResume() {
        super.onResume();
        BusStation.getBus().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusStation.getBus().unregister(this);
    }
    @Subscribe
    public  void receivedMessage(message message){
        state.setText(message.getMessage());
    }


}
