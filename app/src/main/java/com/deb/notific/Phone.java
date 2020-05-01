package com.deb.notific;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.deb.notific.helper.pnumber;

public class Phone extends BroadcastReceiver {
AudioManager am;
    String number,nm;
    @Override
    public void onReceive(final Context context, Intent intent) {
        try {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING))
            {
                 number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                String phoneNr=  getContactName(context,number);
               pnumber pnumber = new pnumber(number);
                  number = pnumber.getPhone();
                  send(number);
                Toast.makeText(context,"Ringing"+" "+number,Toast.LENGTH_SHORT).show();

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

//        TelephonyManager tm = (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);
//
//        switch (tm.getCallState()) {
//
//            case TelephonyManager.CALL_STATE_RINGING:
//                String phoneNr= intent.getStringExtra("incoming_number");
//                Toast.makeText(context, phoneNr,Toast.LENGTH_LONG).show();
//                break;
//        }

    }

    private String checa(String number) {
       return number.replace("+91","");
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


