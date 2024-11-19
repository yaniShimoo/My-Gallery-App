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

public class SMSIncomeReceiver extends BroadcastReceiver {

    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        // throw new UnsupportedOperationException("Not yet implemented");  // ?
        Toast.makeText(context, "Incoming SMS!", Toast.LENGTH_LONG).show();
        final String action = intent.getAction();
        final Bundle extras = intent.getExtras();
        if (action.equals("android.provider.Telephony.SMS_RECEIVED") && extras!=null)  {
            //Take SMSMessages from PDUs in the Bundle
            final Object[] pdus = (Object[])extras.get("pdus");
            final SmsMessage[] messages = new SmsMessage[pdus.length];
            for (int i = 0; i < pdus.length; i++)
                messages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
            String phoneNumber = messages[0].getDisplayOriginatingAddress();
            String smsText = messages[0].getDisplayMessageBody();
            AlertDialog.Builder adb = new AlertDialog.Builder(context);
            adb.setTitle("SMS incoming");
            //adb.setMessage("From number: "+messages[0].getDisplayOriginatingAddress());
            adb.setMessage("From: \t" + phoneNumber + "\nText: \t" + smsText);
            if (IsCust(context,phoneNumber))
                saveToDB( context , smsText, phoneNumber);
            adb.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    saveToDB( context , smsText, phoneNumber);
                }
            });
            adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.cancel();
                }
            });
            if (!IsCust(context,phoneNumber)) adb.create().show();
        }
    }


    private void saveToDB(Context context, String smsText, String phoneNum) {
        SimpleDateFormat sDf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String date = sDf.format(new Date());
        DBHelper dbHelper;
        dbHelper = new DBHelper ( context , "myDB" , null , DBHelper.DATABASE_VERSION ) ;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues( ) ;
        cv.put("textM", smsText);
        cv.put("dateM", date);
        cv.put("phoneM", phoneNum);
        long rowID = db. insert ("Messages" , null, cv);
        db.close();
    }

    /*
    * db.execSQL("create table Customers ("
                + "idC integer primary key autoincrement,"
                + "nameC text,"
                + "phoneC text,"
                + "adressC text"
                + ");");*/
    private boolean IsCust(Context context, String phoneNum) {
        DBHelper dbHelper = new DBHelper(context, "myDB", null, DBHelper.DATABASE_VERSION);
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