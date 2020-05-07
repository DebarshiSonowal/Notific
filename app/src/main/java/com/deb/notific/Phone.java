package com.deb.notific;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import com.deb.notific.helper.Contract;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.deb.notific.helper.BusStation;
import com.deb.notific.helper.DatabaseHelper;
import com.deb.notific.helper.message;
import com.deb.notific.helper.pnumber;
import com.google.gson.Gson;
import com.squareup.otto.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Phone extends BroadcastReceiver {
    List<String>pnum = new ArrayList<>();
    List<String>namelist= new ArrayList<>();
    List<String>time = new ArrayList<>();
    SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.getDefault());
    String number,nm;
    String sflag;
SQLiteDatabase mDatabase;
    @Override
    public void onReceive(final Context context, Intent intent) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        mDatabase =  databaseHelper.getWritableDatabase();


                            try {
                                String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                                if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING))
                                {
                                    Date mDate = new Date();
                                    time.add(sdf.format(mDate));
                                    number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                                    pnum.add(number);
                                    String phoneNr=  getContactName(context,number);
                                    if(phoneNr == null)
                                    {
                                        phoneNr = "Unknown";
                                    }
                                    Log.d("number",phoneNr);
                                    namelist.add(phoneNr);
                                    sflag = phoneNr;
                                    pnumber pnumber = new pnumber(number);
                                    number = pnumber.getPhone();
                                    send(number);
                                    saveData(context, time,pnum,namelist);
                                    Toast.makeText(context,"Ringing"+" "+sflag,Toast.LENGTH_SHORT).show();
                                    ContentValues cv = new ContentValues();
                                    cv.put(Contract.MissedCalls.COLUMN_NAME,sflag);
                                    cv.put(Contract.MissedCalls.COLUMN_NUMBER,number);
                                    cv.put(Contract.MissedCalls.COLUMN_TIME,sdf.format(mDate));
                                    mDatabase.insert(Contract.MissedCalls.TABLE_NAME,null,cv);
                                }
                                if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_OFFHOOK))
                                {
                                    Toast.makeText(context,"Received",Toast.LENGTH_SHORT).show();
                                }
                                if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_IDLE)) {
                                    Toast.makeText(context, "Idle", Toast.LENGTH_SHORT).show();
//                nm =  checa(number);
                                    Log.d("Message sfsffs",nm);
                                }
                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }

    private void saveData(Context context, List<String> time, List<String> number, List<String> name) {
        SharedPreferences pref = context.getSharedPreferences("shared pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor mEdit = pref.edit();
        Gson gson = new Gson();
        String timelist = gson.toJson(time);
        String numberlist = gson.toJson(number);
        String namelist = gson.toJson(name);
        mEdit.putString("time",timelist);
        mEdit.commit();
        mEdit.putString("number",numberlist);
        mEdit.commit();
        mEdit.putString("name",namelist);
        mEdit.commit();

    }


    private void send(String number) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(number, null, "I am busy please call me later ", null, null);
        Log.d("Message sfsffs",number);
    }

    public static String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }
}


