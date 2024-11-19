package com.rabin.mygallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;
import java.util.Calendar;

public class NewOrder extends AppCompatActivity {

    DBHelper dbHelper;
    TextView date, time;
    EditText cusName, cusPhone, paintingName, adress, price;
    LottieAnimationView delivery;
    boolean isChecked = false;
    ArrayList<Customer> customerList = new ArrayList<>();
    ArrayList<Painting> paintingList = new ArrayList<>();
    String baseQuery = "SELECT * FROM Orders";
    Button save;
    int idOrd = 0;
    int paintPrice = 0;
    int shipPrice = 30;
    boolean updtInpt = true;
    //String isShipping;
    int myYear, myMonth, myDay, myHour, myMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);
        dbHelper = new DBHelper(this, "myDB", null, DBHelper.DATABASE_VERSION);
        date = findViewById(R.id.order_date);
        time = findViewById(R.id.order_time);
        save = findViewById(R.id.order_save);
        cusName = findViewById(R.id.order_name);
        ReadOnly(cusName);
        cusPhone = findViewById(R.id.order_phone);
        ReadOnly(cusPhone);
        paintingName = findViewById(R.id.order_namePaint);
        ReadOnly(paintingName);
        adress = findViewById(R.id.order_adress);
        adress.setVisibility(View.GONE); ReadOnly(adress);
        delivery = findViewById(R.id.deliveryswitch);
        price = findViewById(R.id.order_price);
        ReadOnly(price);
        date.setText(CurrentDate());
        time.setText(CurrentTime());
        UpdateOrInput();
    }

    public void ChooseDate(View view) {
        CurrentDate();
        DatePickerDialog datePickerDialog = new DatePickerDialog(NewOrder.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                String _date = "";
                if (dayOfMonth < 10) _date = "0" + dayOfMonth;
                else _date = "" + dayOfMonth;
                if (monthOfYear + 1 < 10) _date += "/0" + (monthOfYear + 1);
                else _date += "/" + (monthOfYear + 1);
                _date += "/" + year;
                date.setText(_date);
            }
        }, myYear, myMonth, myDay);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    public void ChooseTime(View view) {
        CurrentTime();
        TimePickerDialog timePickerDialog = new TimePickerDialog(NewOrder.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                String _time = "";
                if (hourOfDay > 9) _time += hourOfDay;
                else _time += "0" + hourOfDay;
                if (minute > 9) _time += ":" + minute;
                else _time += ":0" + minute;
                time.setText(_time);
            }
        }, myHour, myMinute, true);
        timePickerDialog.show();
    }

    private void UpdateOrInput() {
        updtInpt = true;
        Intent takeIt = getIntent();
        idOrd = takeIt.getIntExtra("idOrd", 0);
        if (idOrd != 0) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            String selectQuery = "SELECT * FROM Orders WHERE idOrd = " + "'" + idOrd + "'";
            Cursor c = db.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                String namePaintOrd = c.getString((int) c.getColumnIndex("namePaintOrd"));
                String phoneOrd = c.getString((int) c.getColumnIndex("phoneOrd"));
                String dateOrd = DateDB(c.getString((int) c.getColumnIndex("dateOrd")));
                String timeOrd = c.getString((int) c.getColumnIndex("timeOrd"));
                String adressOrd = c.getString((int) c.getColumnIndex("adressOrd"));
                String priceOrd = c.getString((int) c.getColumnIndex("priceOrd"));
                String shipping = c.getString((int) c.getColumnIndex("shipping"));

                Order order = new Order(idOrd, namePaintOrd, phoneOrd, dateOrd, timeOrd, adressOrd, priceOrd);
                order.setShipping(shipping);
                cusPhone.setText(order.getPhoneOrd());
                cusName.setText(getNameByPhone(order.getPhoneOrd()));
                paintingName.setText(order.getNamePaintOrd());
                date.setText(order.getDateOrd());
                time.setText(order.getTimeOrd());
                adress.setText(order.getAdressOrd());
                price.setText(order.getPriceOrd());
                paintPrice = Integer.parseInt(order.getPriceOrd());
                if (order.getShipping().equals("Yes")) {
                    adress.setVisibility(View.VISIBLE);
                    turnOn(delivery);
                } else {
                    adress.setVisibility(View.GONE);
                    paintPrice += shipPrice;
                    turnOff(delivery);
                }
            }
            updtInpt = false;
            c.close();
            db.close();
        }
    }

    private void turnOff(LottieAnimationView switcher) {
        switcher.setMinAndMaxProgress(0.5f, 1.0f);
        switcher.setSpeed(2);
        switcher.playAnimation();
        isChecked = false;
    }
    private void turnOn(LottieAnimationView switcher){
        switcher.setMinAndMaxProgress(0.0f, 0.5f);
        switcher.setSpeed(2);
        switcher.playAnimation();
        isChecked = true;
    }
    public void onSwitch(View view){
        if (updtInpt){
            if (!isChecked){
                adress.setVisibility(View.VISIBLE);
                price.setText("" + (paintPrice + shipPrice));
                turnOn(delivery);
            }
            else {
                adress.setVisibility(View.GONE);
                price.setText("" + paintPrice);
                turnOff(delivery);
            }
        }
        else {
            if (!isChecked){
                adress.setVisibility(View.VISIBLE);
                price.setText("" + ((paintPrice - shipPrice) + shipPrice));
                turnOn(delivery);
            }
            else {
                adress.setVisibility(View.GONE);
                price.setText("" + (paintPrice - shipPrice));
                turnOff(delivery);
            }
        }
    }

    private String getNameByPhone(String phoneOrd) {
        String name = "";
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selectQuery = "SELECT * FROM Customers WHERE phoneC = " + "'" + phoneOrd + "'";
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            name = c.getString((int) c.getColumnIndex("nameC"));
        }
        c.close();
        db.close();
        return name;
    }

    private String CurrentDate() {
        Calendar c = Calendar.getInstance();
        myYear = c.get(Calendar.YEAR);
        myMonth = c.get(Calendar.MONTH);
        myDay = c.get(Calendar.DAY_OF_MONTH);
        String date = "";
        if (myDay < 10) date = "0" + myDay;
        else date = "" + myDay;
        if (myMonth + 1 < 10) date += "/0" + (myMonth + 1);
        else date += "/" + (myMonth + 1);
        date += "/" + myYear;
        return date;
    }

    private String CurrentTime() {
        Calendar c = Calendar.getInstance();
        myHour = c.get(Calendar.HOUR_OF_DAY);
        myMinute = c.get(Calendar.MINUTE);
        String time = "";
        if (myHour > 9) time += myHour;
        else time += "0" + myHour;
        if (myMinute > 9) time += ":" + myMinute;
        else time += ":0" + myMinute;
        return time;
    }

    public void SaveOrder(View view) {
        if (cusName.getText().toString().isEmpty() || paintingName.getText().toString().isEmpty()) {
            Toast.makeText(this, "Fill up the customer`s fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (updtInpt == true) {
            String SMS = "Your order is set." + " You will receive your order on " + date.getText().toString() + " in " + time.getText().toString() + " name: " + paintingName.getText().toString() + " " + price.getText().toString() + "\tShekel";
            SMSDialog(SMS, cusPhone.getText().toString());
            SaveToDB(paintingName.getText().toString(), cusPhone.getText().toString(), date.getText().toString(), time.getText().toString(), adress.getText().toString(), price.getText().toString());
        } else {
            String SMS = "Your order is altered." + " You will receive your order on " + date.getText().toString() + " in " + time.getText().toString() + " name: " + paintingName.getText().toString() + " " + price.getText().toString() + "\tShekel";
            SMSDialog(SMS, cusPhone.getText().toString());
            UpdateDB(paintingName.getText().toString(), cusPhone.getText().toString(), date.getText().toString(), time.getText().toString(), adress.getText().toString(), price.getText().toString());
        }
    }

    private void UpdateDB(String paintName, String cusPhone, String date, String time, String adress, String price) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("namePaintOrd", paintName);
        cv.put("phoneOrd", cusPhone);
        cv.put("dateOrd", DateDB(date));
        cv.put("timeOrd", time);
        cv.put("adressOrd", adress);
        cv.put("priceOrd", price);
        if (isChecked) cv.put("shipping", "Yes");
        else cv.put("shipping", "No");
        cv.put("complete", "No");
        int updCount = db.update("Orders", cv, "idOrd = ?", new String[]{("" + idOrd)});
        db.close();
    }

    private String DateDB(String dt) {
        String[] dateArray = dt.split("/");
        String td = dateArray[2] + "/" + dateArray[1] + "/" + dateArray[0];
        return td;
    }

    private void SaveToDB(String nameOrd, String phoneOrd, String dateOrd, String timeOrd, String adressOrd, String priceOrd) {
        //String SMS = "Your order is set. \n" + "You will get your order on" + dateOrd + "in " + timeOrd + "\n" + "name: " + nameOrd + "\t\t\t" + priceOrd + "₪";
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("namePaintOrd", nameOrd);
        cv.put("phoneOrd", phoneOrd);
        cv.put("dateOrd", DateDB(dateOrd));
        cv.put("timeOrd", timeOrd);
        cv.put("adressOrd", adressOrd);
        cv.put("priceOrd", priceOrd);
        if (isChecked) cv.put("shipping", "Yes");
        else cv.put("shipping", "No");
        cv.put("complete", "No");
        long rowID = db.insert("Orders", null, cv);
        db.close();
    }

    private void SMSDialog(String sms, String phoneOrd) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Send message")
                .setIcon(ContextCompat.getDrawable(NewOrder.this, R.drawable.cancelable))
                .setMessage("Would you like to send a message to \t" + phoneOrd)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sendMsg(phoneOrd, sms);
                        Intent intent = new Intent(NewOrder.this, Orders.class);
                        startActivity(intent);
                        finish();
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(NewOrder.this, Orders.class);
                        startActivity(intent);
                        finish();
                        dialogInterface.dismiss();
                    }
                });
        builder.show();
    }


    private void sendMsg(String phoneNum, String message) {
        Toast.makeText(NewOrder.this, "The sms is sent to: " + phoneNum, Toast.LENGTH_SHORT).show();
        requestSmsPermission();
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNum, null, message, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestSmsPermission() {
        String permission = Manifest.permission.SEND_SMS;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if (grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = permission;
            ActivityCompat.requestPermissions(this, permission_list, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
            }
        }
    }

    public void SelectCustomers(View view){
        customerList = ReadCustomersDB();
        final ArrayList<String> ALL_CUSTOMERS = new ArrayList<>();
        for ( int i = 0; i < customerList.size(); i++)
            ALL_CUSTOMERS.add(customerList.get(i).getNameC());
        final int[] pos = {-1};
        final boolean[] elementIsSelected = {false};
        AlertDialog.Builder alertAdpt = new AlertDialog.Builder(this);
        alertAdpt.setTitle("Select customer: ").setIcon(ContextCompat.getDrawable(NewOrder.this, R.drawable.add));
        ListView lv_choice  = new ListView(this);
        ArrayAdapter<String> listAdpt = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, ALL_CUSTOMERS);
        lv_choice.setAdapter(listAdpt);
        lv_choice.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv_choice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                elementIsSelected[0] = true;
                pos[0] = i;
                Toast.makeText(NewOrder.this, ALL_CUSTOMERS.get(i), Toast.LENGTH_SHORT).show();
            }
        });
        alertAdpt.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (elementIsSelected[0]){
                    cusName.setText(customerList.get(pos[0]).getNameC());
                    cusPhone.setText(customerList.get(pos[0]).getPhoneC());
                    adress.setText(customerList.get(pos[0]).getAdressC());
                }
                else
                    Toast.makeText(NewOrder.this, "Nothing selected", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertAdpt.setView(lv_choice);
        alertAdpt.create();
        alertAdpt.show();
    }

    private ArrayList<Customer> ReadCustomersDB() {
        customerList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("Customers",null,null,null,null,null,null);
        if (c.moveToFirst()){
            do {
                int idC = c.getInt((int) c.getColumnIndex("idC"));
                String nameC = c.getString((int) c.getColumnIndex("nameC"));
                String phoneC = c.getString((int) c.getColumnIndex("phoneC"));
                String adressC = c.getString((int) c.getColumnIndex("adressC"));
                Customer customer = new Customer(idC, nameC, phoneC, adressC);
                customerList.add(customer);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return  customerList;
    }

    public void SelectPainting(View view){
        paintingList = ReadPaintingDB();
        final ArrayList<String> ALL_PAINTING= new ArrayList<>();
        for (int i = 0; i < paintingList.size(); i++)
            ALL_PAINTING.add(paintingList.get(i).getNameP());
        final int[] pos = {-1};
        final boolean[] elementIsSelected = {false};
        AlertDialog.Builder alertAdp = new AlertDialog.Builder(this);
        alertAdp.setTitle("select a painting").setIcon(ContextCompat.getDrawable(NewOrder.this, R.drawable.add));
        ListView lv_choice = new ListView(this);
        ArrayAdapter<String> lvAdp = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, ALL_PAINTING);
        lv_choice.setAdapter(lvAdp);
        lv_choice.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv_choice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                elementIsSelected[0] = true;
                pos[0] = i;
                Toast.makeText(NewOrder.this, "" + ALL_PAINTING.get(i), Toast.LENGTH_SHORT).show();
            }
        });
        alertAdp.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (elementIsSelected[0]){
                    paintingName.setText(paintingList.get(pos[0]).getNameP());
                    paintPrice = paintingList.get(pos[0]).getCostP();
                    price.setText("" + paintPrice);
                }
                else
                    Toast.makeText(NewOrder.this, "Nothing selected", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        alertAdp.setView(lv_choice);
        alertAdp.create();
        alertAdp.show();
    }

    private ArrayList<Painting> ReadPaintingDB() {
        paintingList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("Paintings",null,null,null,null,null,null);
        if (c.moveToFirst()){
            do {
                int idP = c.getInt((int) c.getColumnIndex("idP"));
                String nameP = c.getString((int) c.getColumnIndex("nameP"));
                int widthP = c.getInt((int) c.getColumnIndex("widthP"));
                int heightP = c.getInt((int) c.getColumnIndex("heightP"));
                String dateP = c.getString((int) c.getColumnIndex("dateP"));
                String descriptionP = c.getString((int) c.getColumnIndex("descriptionP"));
                String paintingPic = c.getString((int) c.getColumnIndex("paintingPic"));
                String uriP = c.getString((int) c.getColumnIndex("uriP"));
                int costP = c.getInt((int) c.getColumnIndex("costP"));
                Painting painting = new Painting(idP, nameP, widthP, heightP, dateP, descriptionP, paintingPic, uriP, costP);
                paintingList.add(painting);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return paintingList;
    }

    private void ReadOnly(View view) {
        view.setFocusable(false);
        view.setClickable(false);
        view.setFocusableInTouchMode(false);
    }
}