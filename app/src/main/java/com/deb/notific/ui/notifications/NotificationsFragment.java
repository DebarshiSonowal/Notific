package com.deb.notific.ui.notifications;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.deb.notific.Adapter1;
import com.deb.notific.MissedAdapter;
import com.deb.notific.R;
import com.deb.notific.helper.Contract;
import com.deb.notific.helper.DatabaseHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {
    List<String>namelist;
    List<String>numberlist;
    List<String>timelist;
    MissedAdapter mAdapter;
    SQLiteDatabase mDatabase;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        mDatabase = databaseHelper.getWritableDatabase();
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
//        LoadData();
        final RecyclerView recyclerView = root.findViewById(R.id.notificrecycler);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
//        mAdapter = new Adapter1(getContext(),namelist,timelist,namelist);
        mAdapter = new MissedAdapter(getContext(),getAllItems());
        recyclerView.setAdapter(mAdapter);
        return root;
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

    private void LoadData() {
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Gson gson = new Gson();
        String name = mPreferences.getString("name",null);
        String number = mPreferences.getString("number",null);
        String time = mPreferences.getString("time",null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        namelist = gson.fromJson(name, type);
        numberlist = gson.fromJson(number,type);
        timelist = gson.fromJson(time,type);
    }
}

