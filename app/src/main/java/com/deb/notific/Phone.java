package com.deb.notific;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.deb.notific.helper.BusStation;
import com.deb.notific.helper.message;
import com.deb.notific.helper.pnumber;
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
    @Override
    public void onReceive(final Context context, Intent intent) {

                            try {
                                String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                                if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING))
                                {
                                    Date mDate = new Date();
                                    time.add(sdf.format(mDate));
                                    number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                                    pnum.add(number);
                                    String phoneNr=  getContactName(context,number);
                                    if(phoneNr.equals(null))
                                    {
                                        namelist.add("Unknown number");
                                    }
                                    else
                                        namelist.add(phoneNr);

                                    pnumber pnumber = new pnumber(number);
                                    number = pnumber.getPhone();
                                    send(number);
                                    Toast.makeText(context,"Ringing"+" "+phoneNr,Toast.LENGTH_SHORT).show();
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


