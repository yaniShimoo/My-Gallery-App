package com.rabin.mygallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Messenger extends AppCompatActivity implements AdapterView.OnItemLongClickListener {

    TextView customDate;
    int pos = -1;
    DBHelper dbHelper;
    ListView lv;
    LottieAnimationView menuAnim;
    ImageView infoMsg;
    Animation slideUp, slideDown;
    ArrayList<Message> MessagesList = new ArrayList<>();
    String baseQuery = "SELECT * FROM Messages";
    int myYear = 2023, myMonth = 11, myDay = 9;

    boolean isDisplayDate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        dbHelper = new DBHelper(this, "myDB", null, DBHelper.DATABASE_VERSION);
        lv = findViewById(R.id.lvmessages);
        infoMsg = findViewById(R.id.infomessage);
        menuAnim = findViewById(R.id.menuAnim);
        menuAnim.playAnimation();
        menuAnim.loop(true);
        customDate = findViewById(R.id.SMSdate); customDate.setVisibility(View.INVISIBLE);
        slideDown = AnimationUtils.loadAnimation(Messenger.this, R.anim.slide_down);
        slideUp = AnimationUtils.loadAnimation(Messenger.this, R.anim.slide_up);
        lv.setOnItemLongClickListener(this);
        registerForContextMenu(lv);
        ReadFromDB(baseQuery);
        ViewList();
    }

    private void ReadFromDB(String selectQuery) {
        MessagesList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                int id = c.getInt((int) c.getColumnIndex("idM"));
                String text = c.getString((int) c.getColumnIndex("textM"));
                String date = c.getString((int) c.getColumnIndex("dateM"));
                String phone = c.getString((int) c.getColumnIndex("phoneM"));
                MessagesList.add(new Message(id, text, date, phone));
            }while (c.moveToNext());
        }
        c.close();
        db.close();
    }
    public void PopUpMenu(View view){
        PopupMenu menu =new PopupMenu(Messenger.this, infoMsg);
        menu.getMenu().add("View all");
        menu.getMenu().add("View today");
        menu.getMenu().add("Choose date");
        menu.show();
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getTitle().toString()){
                    case "View all":
                        if (isDisplayDate){
                            customDate.startAnimation(slideUp);
                            customDate.setVisibility(View.INVISIBLE);
                            isDisplayDate = false;
                        }
                        ReadFromDB(baseQuery);
                        ViewList();
                        break;
                    case "View today":
                        if (isDisplayDate){
                            customDate.startAnimation(slideUp);
                            customDate.setVisibility(View.INVISIBLE);
                            isDisplayDate = false;
                        }
                        ViewToday();
                        break;
                    case "Choose date":
                        CustomDate();
                        break;
                }
                return false;
            }
        });
    }

    public String CurrentDate(){
        Calendar c = Calendar.getInstance();
        myYear = c.get(Calendar.YEAR);
        myMonth = c.get(Calendar.MONTH);
        myDay = c.get(Calendar.DAY_OF_MONTH);
        String date = "";
        if (myDay < 10) date = "0" + myDay;
        else date = "" + myDay;
        if (myMonth + 1 < 10) date += "/0" + (myMonth+1);
        else date += "/" + (myMonth+1);
        date += "/" + myYear;
        return  date;
    }

    private void CustomDate() {
        CurrentDate();
        DatePickerDialog datePickerDialog = new DatePickerDialog(Messenger.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker,  int year, int monthOfYear, int dayOfMonth) {
                String date = "";
                if (dayOfMonth < 10) date = "0" + dayOfMonth;
                else date = "" + dayOfMonth;
                if (monthOfYear + 1 < 10) date += "/0" + (monthOfYear+1);
                else date += "/" + (monthOfYear+1);
                date += "/" + year;
                ViewCustomDate(date);
            }
        }, myYear,myMonth,myDay );
        datePickerDialog.show();
    }



    private void ViewCustomDate(String date) {
        isDisplayDate = true;
        customDate.setText(date);
        customDate.setVisibility(View.VISIBLE);
        customDate.startAnimation(slideDown);
        String YMD = DateDB(date);
        String customDayQuery = "SELECT * FROM Messages WHERE dateM LIKE " + "'%" + YMD + "%'";
        ReadFromDB(customDayQuery);
        ViewList();
        Toast.makeText(this, "date is set!", Toast.LENGTH_SHORT).show();
    }

    private String DateDB(String date) {
        String[] dateArray = date.split("/");
        String td = dateArray[2] + "/" + dateArray[1] + "/" + dateArray[0];
        return td;
    }

    private void ViewToday() {
        SimpleDateFormat SDF = new SimpleDateFormat("yyyy/MM/dd");
        String YMD = SDF.format(new Date());
        String  selectQueryToday = "SELECT * FROM Messages WHERE dateM LIKE " + "'" + YMD + "%'";
        ReadFromDB(selectQueryToday);
        ViewList();
        Toast.makeText(this, "date is set!", Toast.LENGTH_SHORT).show();
    }

    private void ViewList() {
        ArrayList<String> ALL = new ArrayList<>();
        for (int i = 0; i < MessagesList.size(); i++) {
            ALL.add(MessagesList.get(i).toString());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.listitem, R.id.list_item, ALL);
        lv.setAdapter(adapter);
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, view, menuInfo);
        menu.add("Delete message");
        menu.add("Make call");
    }
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item){
        if (item.toString().equals("Delete message")){
            DeleteSMS(pos);
        }
        if (item.toString().equals("Make call")){
            try {
                String s = MessagesList.get(pos).getPhoneM();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + s));
                startActivity(intent);
            }catch (android.content.ActivityNotFoundException e){
                Toast.makeText(getApplicationContext(), "application failed", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onContextItemSelected(item);
    }

    private void DeleteSMS(int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Delete")
                .setIcon(ContextCompat.getDrawable(Messenger.this, R.drawable.cancelable))
                .setMessage("Are you sure you want to delete this Sms Message?")
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        int delCount = db.delete("Messages", "idM = " + MessagesList.get(pos).getIdM(), null);
                        db.close();
                        ReadFromDB(baseQuery);
                        ViewList();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                    }
                });
        builder.show();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        pos = i;
        return false;
    }
}