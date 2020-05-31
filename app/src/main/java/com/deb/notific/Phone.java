package com.deb.notific;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.deb.notific.helper.Contract;
import com.deb.notific.helper.DatabaseHelper;
import com.deb.notific.helper.pnumber;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Context.NOTIFICATION_SERVICE;

public class Phone extends BroadcastReceiver {
    List<String>pnum = new ArrayList<>();
    List<String>namelist= new ArrayList<>();
    List<String>time = new ArrayList<>();
    public static final String CHANNEL_ID = "MessageChannel";
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy h:mm a", Locale.getDefault());
    String number,nm;
    String sflag;
SQLiteDatabase mDatabase;
    @Override
    public void onReceive(final Context context, Intent intent) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        mDatabase =  databaseHelper.getWritableDatabase();
        String action = intent.getAction();
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

        if(action.equals("android.provider.Telephony.SMS_RECEIVED")){
            Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
            SmsMessage[] msgs = null;
            String msg_from;
            if (bundle != null){
                //---retrieve the SMS message received---

                try{
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for(int i=0; i<msgs.length; i++){
                        msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        String msgBody = msgs[i].getMessageBody();
                        Toast.makeText(context,"Message From:"+msg_from+"/"+msgBody,Toast.LENGTH_SHORT).show();
//                        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
//                                .setContentTitle("New Message")
//                                .setContentText(msgBody)
////                .addAction(R.drawable.address,"Stop",stop)
//                                .build();
//                        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
//                        manager.notify(2,notification);
                    }
                }catch(Exception e){
//                            Log.d("Exception caught",e.getMessage());

                }
            }
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
    private void conversation(){

    }
}


