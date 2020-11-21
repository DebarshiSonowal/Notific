package com.deb.notific.ui.notifications;

import android.Manifest;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deb.notific.MapsActivity;
import com.deb.notific.MissedAdapter;
import com.deb.notific.R;
import com.deb.notific.helper.Contract;
import com.deb.notific.helper.DatabaseHelper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.labo.kaji.fragmentanimations.CubeAnimation;
import com.labo.kaji.fragmentanimations.MoveAnimation;
import com.pd.chocobar.ChocoBar;
import com.vlonjatg.progressactivity.ProgressRelativeLayout;

import java.util.List;

public class NotificationsFragment extends Fragment {
    MissedAdapter mAdapter;
    SQLiteDatabase mDatabase;
    ProgressRelativeLayout mEmptyView;
    AdView mAdView;
    @Override
    public void onDestroy() {
        System.gc();
        super.onDestroy();
    }
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
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Dexter.withContext(container.getContext())
                .withPermissions(
                        Manifest.permission_group.LOCATION,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.READ_CALL_LOG,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.MODIFY_PHONE_STATE,
                        Manifest.permission_group.SMS,
                        Manifest.permission_group.PHONE,
                        Manifest.permission_group.STORAGE,
                        Manifest.permission.ACCESS_NOTIFICATION_POLICY,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.INTERNET
                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */}
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        }).check();
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        mDatabase = databaseHelper.getWritableDatabase();
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        mEmptyView = root.findViewById(R.id.loadingLayout);
        MobileAds.initialize(getContext(),new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });
        mAdView = root.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
//        LoadData();
        final RecyclerView recyclerView = root.findViewById(R.id.notificrecycler);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
//        mAdapter = new Adapter1(getContext(),namelist,timelist,namelist);
        mAdapter = new MissedAdapter(getContext(),getAllItems());
        recyclerView.setAdapter(mAdapter);
        if(mAdapter.getItemCount() == 0){
            mEmptyView.showEmpty(R.drawable.ic_empty_box_open,"No items in the cart","Add items to the cart");
        }else
            mEmptyView.showContent();
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerview, RecyclerView.ViewHolder viewholder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewholder, int direction) {
                removeItem((long) viewholder.itemView.getTag());
            }
        }).attachToRecyclerView(recyclerView);
        if(mAdapter.getItemCount() == 0){
            mEmptyView.showEmpty(R.drawable.ic_notify,"No notifications","No missed calls");
        }else   mEmptyView.showContent();
        return root;
    }

    private void removeItem(long id) {
        mDatabase.delete(Contract.MissedCalls.TABLE_NAME,
                Contract.MissedCalls._ID + "=" + id, null);
        mAdapter.swapCursor(getAllItems());
        ChocoBar.builder().setActivity(getActivity())
                .setText("Deleted")
                .setDuration(ChocoBar.LENGTH_SHORT)
                .setActionText(android.R.string.ok)
                .red()   // in built red ChocoBar
                .show();
        if(mAdapter.getItemCount() == 0){
            mEmptyView.showEmpty(R.drawable.ic_notify,"No Notifications","No Missed Calls");
        }else   mEmptyView.showContent();

    }

    private Cursor getAllItems() {
        return mDatabase.query(
                Contract.MissedCalls.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);
    }

}

