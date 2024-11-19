package com.rabin.mygallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Orders extends AppCompatActivity implements AdapterView.OnItemLongClickListener {
    DBHelper dbHelper;
    ArrayList<Order> orderList = new ArrayList<>();
    LinearLayout llsort;
    ListView lv;
    int pos = -1;
    int myYear, myMonth, myDay;
    ImageView addOrd, statisticView, ivSearch;
    EditText searchOrd;
    Animation swingText, sUp, sDown, slideDown, slideUp;
    String baseQuery = "SELECT * FROM Orders";
    TextView tvDate, tvPrice, tvComplete;
    int current = 0, total = 0;
    boolean isSwitchOn = false;
    //LottieAnimationView switchSort;
    ImageView ivSortOrd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        dbHelper = new DBHelper(this, "myDB", null, DBHelper.DATABASE_VERSION);
        tvDate = findViewById(R.id.bydate_ord);
        tvPrice = findViewById(R.id.byprice_ord);
        tvComplete = findViewById(R.id.bycomplete_ord);
        //switchSort = findViewById(R.id.sortswitch);
        ivSortOrd = findViewById(R.id.sortOrders);
        ivSearch = findViewById(R.id.ivSearchOrd);
        llsort = findViewById(R.id.llsortord);
        llsort.setVisibility(View.GONE);
        addOrd = findViewById(R.id.ivaddorder);
        statisticView = findViewById(R.id.info);
        searchOrd = findViewById(R.id.et_search_order);
        swingText = AnimationUtils.loadAnimation(this, R.anim.swing);
        sUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        sDown = AnimationUtils.loadAnimation(this, R.anim.scale_down);
        slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);
        slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        addOrd.startAnimation(swingText);
        sDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                addOrd.startAnimation(swingText);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
            }
        });
        //YoYo.with(Techniques.Swing).duration(1000).repeat(5).playOn(tvDate);
        AnimationImplementation();
        lv = findViewById(R.id.lvorders);
        registerForContextMenu(lv);
        lv.setOnItemLongClickListener(this);
        ReadFromDB(baseQuery);
        ViewList();
    }

    private void AnimationImplementation() {
        ivSortOrd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    ivSortOrd.startAnimation(sUp);
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    ivSortOrd.startAnimation(sDown);
                return false;
            }
        });
        ivSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    ivSearch.startAnimation(sUp);
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    ivSearch.startAnimation(sDown);
                return false;
            }
        });
        tvDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    tvDate.startAnimation(sUp);
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    tvDate.startAnimation(sDown);
                return false;
            }
        });
        tvComplete.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    tvComplete.startAnimation(sUp);
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    tvComplete.startAnimation(sDown);
                return false;
            }
        });
        tvPrice.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    tvPrice.startAnimation(sUp);
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    tvPrice.startAnimation(sDown);
                return false;
            }
        });
        statisticView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    statisticView.startAnimation(sUp);
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    statisticView.startAnimation(sDown);
                return false;
            }
        });
        addOrd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    addOrd.startAnimation(sUp);
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    addOrd.startAnimation(sDown);
                return false;
            }
        });
    }

    public void SearchForOrder(View view) {
        String[] op = {"idOrd", "namePaintOrd", "phoneOrd", "dateOrd", "timeOrd", "adressOrd", "priceOrd", "complete", "shipping"};
        ReadBySearch(op);
        ViewList();
    }

    private void ReadBySearch(String[] columns) {
        orderList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String topic = searchOrd.getText().toString();
        String selection = "idOrd LIKE ? OR namePaintOrd LIKE ? OR phoneOrd LIKE ? OR dateOrd LIKE ? OR timeOrd LIKE ? OR adressOrd LIKE ? OR priceOrd LIKE ? OR complete LIKE ? OR shipping LIKE ?";
        String[] selectionArgs = {"%" + topic + "%", "%" + topic + "%", "%" + topic + "%", "%" + topic + "%", "%" + topic + "%", "%" + topic + "%", "%" + topic + "%", "%" + topic + "%", "%" + topic + "%"};
        Cursor c = db.query("Orders", columns, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            do {
                int idOrd = c.getInt((int) c.getColumnIndex("idOrd"));
                //String nameCusOrd = c.getString((int)c.getColumnIndex("nameCusOrd"));
                String namePaintOrd = c.getString((int) c.getColumnIndex("namePaintOrd"));
                String phoneOrd = c.getString((int) c.getColumnIndex("phoneOrd"));
                String dateOrd = c.getString((int) c.getColumnIndex("dateOrd"));
                String timeOrd = c.getString((int) c.getColumnIndex("timeOrd"));
                String adressOrd = c.getString((int) c.getColumnIndex("adressOrd"));
                String priceOrd = c.getString((int) c.getColumnIndex("priceOrd"));
                String complete = c.getString((int) c.getColumnIndex("complete"));
                String shipping = c.getString((int) c.getColumnIndex("shipping"));
                Order order = new Order(idOrd, namePaintOrd, phoneOrd, dateOrd, timeOrd, adressOrd, priceOrd);
                order.setNameCusOrd(getNameByPhone(phoneOrd));
                order.setComplete(complete);
                order.setShipping(shipping);
                orderList.add(order);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
    }

    /*public void onSwitchSorts(View view) {
        if (isSwitchOn) {
            llsort.startAnimation(slideUp);
            llsort.setVisibility(View.GONE);
            switchSort.setMinAndMaxProgress(0.5f, 1.0f);
            switchSort.setSpeed(3);
            switchSort.playAnimation();
            isSwitchOn = false;
        } else {
            llsort.setVisibility(View.VISIBLE);
            llsort.startAnimation(slideDown);
            switchSort.setMinAndMaxProgress(0.0f, 0.5f);
            switchSort.setSpeed(3);
            switchSort.playAnimation();
            isSwitchOn = true;
        }
    } */

    public void onClickSort(View view){
        if (isSwitchOn) {
            llsort.startAnimation(slideUp);
            llsort.setVisibility(View.GONE);
            isSwitchOn = false;
        }
        else {
            llsort.setVisibility(View.VISIBLE);
            llsort.startAnimation(slideDown);
            isSwitchOn = true;
        }
    }


    private void ViewList() {
       /* ArrayList<String> ALL = new ArrayList<>();
        for (int i = 0; i < orderList.size(); i++) {
            ALL.add(orderList.get(i).toString());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.listitem, R.id.list_item, ALL);
        lv.setAdapter(adapter);*/
        CustomerAdapter adp = new CustomerAdapter(getApplicationContext(), R.layout.listitem_customer, orderList);
        lv.setAdapter(adp);
    }

    private void ReadFromDB(String selectQuery) {
        orderList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                int idOrd = c.getInt((int) c.getColumnIndex("idOrd"));
                //String nameCusOrd = c.getString((int)c.getColumnIndex("nameCusOrd"));
                String namePaintOrd = c.getString((int) c.getColumnIndex("namePaintOrd"));
                String phoneOrd = c.getString((int) c.getColumnIndex("phoneOrd"));
                String dateOrd = c.getString((int) c.getColumnIndex("dateOrd"));
                String timeOrd = c.getString((int) c.getColumnIndex("timeOrd"));
                String adressOrd = c.getString((int) c.getColumnIndex("adressOrd"));
                String priceOrd = c.getString((int) c.getColumnIndex("priceOrd"));
                String complete = c.getString((int) c.getColumnIndex("complete"));
                String shipping = c.getString((int) c.getColumnIndex("shipping"));
                Order order = new Order(idOrd, namePaintOrd, phoneOrd, dateOrd, timeOrd, adressOrd, priceOrd);
                order.setNameCusOrd(getNameByPhone(phoneOrd));
                order.setComplete(complete);
                order.setShipping(shipping);
                orderList.add(order);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
    }

    private String getNameByPhone(String phoneOrd) {
        String nameC = "";
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selectQuery = "SELECT nameC FROM Customers WHERE phoneC = " + "'" + phoneOrd + "'";
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            nameC = c.getString((int) c.getColumnIndex("nameC"));
        }
        c.close();
        db.close();
        return nameC;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add("Edit");
        menu.add("Delete");
        menu.add("Set complete");
        menu.add("Make call");
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getTitle().toString()) {
            case "Edit":
                Intent intent = new Intent(Orders.this, NewOrder.class);
                intent.putExtra("idOrd", orderList.get(pos).getIdOrd());
                startActivity(intent);
                break;
            case "Delete":
                Delete(orderList.get(pos));
                break;
            case "Set complete":
                CompleteOrder(pos);
                break;
            case "Make call":
                CallCustomer(orderList.get(pos).getPhoneOrd());
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void CallCustomer(String phone) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "App failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void CompleteOrder(int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set complete")
                .setIcon(ContextCompat.getDrawable(Orders.this, R.drawable.cancelable))
                .setMessage("set " + orderList.get(pos).getIdOrd() + "complete?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        ContentValues cv = new ContentValues();
                        cv.put("complete", "Yes");
                        int updCount = db.update("Orders", cv, "idOrd = ?", new String[]{("" + orderList.get(pos).getIdOrd())});
                        db.close();
                        ReadFromDB(baseQuery);
                        ViewList();
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        ContentValues cv = new ContentValues();
                        cv.put("complete", "No");
                        int updCount = db.update("Orders", cv, "idOrd = ?", new String[]{("" + orderList.get(pos).getIdOrd())});
                        db.close();
                        ReadFromDB(baseQuery);
                        ViewList();
                        dialogInterface.dismiss();
                    }
                });
        builder.show();
    }

    private void Delete(Order order) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure?")
                .setIcon(ContextCompat.getDrawable(Orders.this, R.drawable.cancelable))
                .setMessage("delete the order №: " + order.getIdOrd())
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DeleteOrder(order);
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton("cancle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        builder.show();
    }

    private void DeleteOrder(Order order) {
        /*boolean tmp = false;
        try {
            tmp = CompareDates(CurrentDate(), order.getDateOrd());
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        //Toast.makeText(this, ""+ tmp, Toast.LENGTH_SHORT).show();
        AlertDialog.Builder editDialog = new AlertDialog.Builder(this);
        editDialog.setTitle("Send a message that the order has been canceled?")
                .setIcon(ContextCompat.getDrawable(Orders.this, R.drawable.cancelable))
                .setMessage("delete order: " + order.getIdOrd());
        final EditText text = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);

        text.setLayoutParams(lp);
        editDialog.setView(text);
        text.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        text.setSingleLine(false);
        text.setVerticalScrollBarEnabled(true);
        text.setHint("This field is unnecessary.");
        String customerPhone = order.getPhoneOrd();
        //text.setText(order.getPhoneOrd());
        editDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (text.getText().toString().isEmpty()){
                    String requiredSms = "Your order for " + order.getDateOrd() + "has been canceled.";
                    SendSMS(customerPhone, requiredSms);
                    del(order);
                }
                else{
                    String sms = text.getText().toString();
                    SendSMS(customerPhone, sms);
                    del(order);
                }
                dialogInterface.dismiss();
            }
        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        editDialog.show();
    }

    private void SendSMS(String phoneOrd, String sms) {
        requestSmsPermission();
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneOrd, null, sms, null, null);
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

    private void del(Order order) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int delCount = db.delete("Orders", "idOrd = " + order.getIdOrd(), null);
        db.close();
        ReadFromDB(baseQuery);
        ViewList();
    }

    public String CurrentDate() {
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

    public void AddOrder(View view) {
        Intent intent = new Intent(Orders.this, NewOrder.class);
        startActivity(intent);
    }

    public void SelectDate(View view) {
        CurrentDate();
        DatePickerDialog datePickerDialog = new DatePickerDialog(Orders.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                String _date = "";
                if (dayOfMonth < 10) _date = "0" + dayOfMonth;
                else _date = "" + dayOfMonth;
                if (monthOfYear + 1 < 10) _date += "/0" + (monthOfYear + 1);
                else _date += "/" + (monthOfYear + 1);
                _date += "/" + year;
                tvDate.setText(_date);
                String dateKey = DateDB(_date);
                String selectQuery = "SELECT * FROM Orders WHERE dateOrd = " + "'" + dateKey + "'";
                ReadFromDB(selectQuery);
                ViewList();
            }
        }, myYear, myMonth, myDay);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private String DateDB(String date) {
        String[] dateArray = date.split("/");
        String td = dateArray[2] + "/" + dateArray[1] + "/" + dateArray[0];
        return td;
    }

    public void CompleteSort(View view) {
        String selectQuery = "SELECT * FROM Orders ORDER By complete";
        ReadFromDB(selectQuery);
        ViewList();
    }

    public void PriceSort(View view) {
        String selectQuery = "SELECT * FROM Orders ORDER By priceOrd";
        ReadFromDB(selectQuery);
        ViewList();
    }

    public void Statistics(View view) {
        final Context context = this;
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_analyticss);
        dialog.show();

        current = CalcCurrent();
        total = CalcTotal();
        total -= current;
        final TextView tvCurrent = dialog.findViewById(R.id.tvCurrent_n);
        final TextView tvTotal = dialog.findViewById(R.id.tvTotal_n);
        final PieChart pieChart = dialog.findViewById(R.id.piechart_n);
        final Button exit = dialog.findViewById(R.id.end_analytics);

        exit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    exit.startAnimation(sUp);
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    exit.startAnimation(sDown);
                return false;
            }
        });

        Toast.makeText(this, "" + current + "  " + total, Toast.LENGTH_SHORT).show();
        tvCurrent.setText(Integer.toString(current));
        tvTotal.setText(Integer.toString(total));
        pieChart.addPieSlice(new PieModel("Total", Integer.parseInt(tvTotal.getText().toString()), Color.parseColor("#EF5350")));
        pieChart.addPieSlice(new PieModel("Current", Integer.parseInt(tvCurrent.getText().toString()), Color.parseColor("#29B6F6")));
        pieChart.startAnimation();
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        /*
        if (orderList.size() == 0){
            Toast.makeText(this, "You don`t have orders", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent goToAnalytics = new Intent(Orders.this, Analytics.class);
        int current = CalcCurrent();
        int total = CalcTotal();
        goToAnalytics .putExtra("current",current);
        goToAnalytics .putExtra("total",total);
        startActivity(goToAnalytics);
         // finish(); */
    }

    private int CalcTotal() {
        int sum = 0;
        for (int i = 0; i < orderList.size(); i++)
            sum += Integer.parseInt(orderList.get(i).getPriceOrd());
        return sum;
    }

    private int CalcCurrent() {
        int sum = 0;
        for (int i = 0; i < orderList.size(); i++)
            if (orderList.get(i).getComplete().equals("Yes"))
                sum += Integer.parseInt(orderList.get(i).getPriceOrd());
        return sum;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        pos = i;
        return false;
    }
}