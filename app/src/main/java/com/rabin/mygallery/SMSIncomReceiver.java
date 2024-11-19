package com.rabin.mygallery;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class SMSIncomReceiver extends BroadcastReceiver {
   Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context, "Incoming Sms!", Toast.LENGTH_SHORT).show();
        final String action = intent.getAction();
        final Bundle extras = intent.getExtras();
        if (action.equals("android.provider.Telephony.SMS_RECEIVED") && extras != null){
            final Object[] pdus = (Objects[]) extras.get("pdus");
            final SmsMessage[] smsMessages = new SmsMessage[pdus.length];
            for (int i = 0; i < pdus.length; i++)
                smsMessages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
            String phoneNum = smsMessages[0].getDisplayOriginatingAddress();
            String smsText = smsMessages[0].getDisplayMessageBody();
            AlertDialog.Builder aDB = new AlertDialog.Builder(context);
            aDB.setTitle("Sms Incom").setMessage("Phone= "+ phoneNum + "\n Message=" + smsText);
            if (IsCust(context, phoneNum))
                SaveToDB(context, smsText, phoneNum);
            aDB.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SaveToDB(context, smsText, phoneNum);
                }
            }).setNegativeButton("cancle", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            if (!IsCust(context, phoneNum))
                aDB.create().show();
        }
    }
    /* Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Incoming SMS!", Toast.LENGTH_LONG).show();
        final String action = intent.getAction();
        final Bundle extras = intent.getExtras();
        if (action.equals("android.provider.Telephony.SMS_RECEIVED") && extras!=null)  {
            final Object[] pdus = (Object[])extras.get("pdus");
            final SmsMessage[] messages = new SmsMessage[pdus.length];
            for (int i = 0; i < pdus.length; i++)
                messages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
            String phoneNum = messages[0].getDisplayOriginatingAddress();
            String smsText = messages[0].getDisplayMessageBody();
            AlertDialog.Builder adb=new AlertDialog.Builder(context);
            adb.setTitle("SMS incoming").setMessage("number= " + phoneNum + "\ntext=" + smsText);
            if (IsCust(context, phoneNum)) SaveToDB(context, smsText, phoneNum);
            adb.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    SaveToDB(context, smsText, phoneNum);
                }
            });
            adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.cancel();
                }
            });
            if (!IsCust(context, phoneNum))
                adb.create().show();
        }
    }
 */
    private void SaveToDB(Context context, String smsText, String phoneNum) {
        SimpleDateFormat sDf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String date = sDf.format(new Date());
        DBHelper dbHelper;
        dbHelper = new DBHelper ( context , "myDB" , null , 1 ) ;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues( ) ;
        cv.put("textM", smsText);
        cv.put("dateM", date);
        cv.put("phoneM", phoneNum);
        long rowID = db. insert ("Messages" , null, cv);
        db.close();
    }

    private boolean IsCust(Context context, String phoneNum) {
        DBHelper dbHelper = new DBHelper(context, "myDB", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selectQuery = "SELECT phoneC FROM Customers WHERE phoneC = " + "'" + phoneNum + "'" ;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()){
            db.close();
            return true;
        }
        else {
            db.close();
            return false;
        }
    }
}