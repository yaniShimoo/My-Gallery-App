package com.rabin.mygallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Sketches extends AppCompatActivity implements AdapterView.OnItemLongClickListener {

    DBHelper dbHelper;
    ArrayList<Sketch> sketchList = new ArrayList<>();
    ListView lv;
    ImageView plus, ivSort;
    String baseQuery = "SELECT * FROM Sketches";
    int pos = -1;
    Animation sUp, sDown, swing, slideUp, slideDown;
    int myYear, myMonth, myDay;
    TextView tvDate;
    boolean isDisplayingDate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sketches);
        dbHelper = new DBHelper(this, "myDB", null, DBHelper.DATABASE_VERSION);
        sUp = AnimationUtils.loadAnimation(Sketches.this, R.anim.scale_up);
        sDown = AnimationUtils.loadAnimation(Sketches.this, R.anim.scale_down);
        swing = AnimationUtils.loadAnimation(Sketches.this, R.anim.swing);
        slideUp = AnimationUtils.loadAnimation(Sketches.this, R.anim.slide_up);
        slideDown = AnimationUtils.loadAnimation(Sketches.this, R.anim.slide_down);
        lv = findViewById(R.id.lvsketches);
        plus = findViewById(R.id.ivaddsketch);
        tvDate = findViewById(R.id.tvDisplayDate); tvDate.setVisibility(View.INVISIBLE);
        plus.startAnimation(swing);
        sDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                plus.startAnimation(swing);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
            }
        });
        ivSort = findViewById(R.id.ivsortsketch);
        /*ivSort.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (isDisplayingDate){
                    tvDate.startAnimation(slideUp);
                    tvDate.setVisibility(View.INVISIBLE);
                    isDisplayingDate = false;
                }
                else {
                    tvDate.setVisibility(View.VISIBLE);
                    tvDate.startAnimation(slideDown);
                    isDisplayingDate = true;
                }
                return false;
            }
        }); */
        animationImplementation();
        lv.setOnItemLongClickListener(this);
        registerForContextMenu(lv);
        ReadFromDB(baseQuery);
        ViewList();
    }

    private void animationImplementation() {
        plus.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    plus.startAnimation(sUp);
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    plus.startAnimation(sDown);
                return false;
            }
        });
        ivSort.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    ivSort.startAnimation(sUp);
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    ivSort.startAnimation(sDown);
                return false;
            }
        });
    }

    private void SortDateS() {
        currentDate();
        DatePickerDialog datePickerDialog = new DatePickerDialog(Sketches.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                String _date = "";
                if (dayOfMonth < 10) _date = "0" + dayOfMonth;
                else _date = "" + dayOfMonth;
                if (monthOfYear + 1 < 10) _date += "/0" + (monthOfYear + 1);
                else _date += "/" + (monthOfYear + 1);
                _date += "/" + year;
                isDisplayingDate = true;
                tvDate.setText(_date);
                tvDate.setVisibility(View.VISIBLE);
                tvDate.startAnimation(slideDown);
                String selectQuery = "SELECT * FROM Sketches WHERE dateS = " + "'" + _date + "'";
                ReadFromDB(selectQuery);
                ViewList();
            }
        }, myYear, myMonth, myDay);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void ViewList() {
        SketchAdapter adp = new SketchAdapter(getApplicationContext(), R.layout.listitem_sketch, sketchList);
        lv.setAdapter(adp);
    }

    private void ReadFromDB(String selectQuery) {
        sketchList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                int idS = c.getInt((int) c.getColumnIndex("idS"));
                String nameS = c.getString((int) c.getColumnIndex("nameS"));
                String dateS = c.getString((int) c.getColumnIndex("dateS"));
                String descriptionS = c.getString((int) c.getColumnIndex("descriptionS"));
                String sketchPic = c.getString((int) c.getColumnIndex("sketchPic"));
                sketchList.add(new Sketch(idS, nameS, dateS, descriptionS, sketchPic));
            } while (c.moveToNext());
        }
        c.close();
        db.close();
    }

    public void createSortItemMenu(View view){
        PopupMenu popupMenu = new PopupMenu(this, ivSort);
        popupMenu.getMenu().add("View all");
        popupMenu.getMenu().add("Sort by date");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getTitle().toString()){
                    case "View all":
                        if (isDisplayingDate){
                            tvDate.startAnimation(slideUp);
                            tvDate.setVisibility(View.INVISIBLE);
                            isDisplayingDate = false;
                        }
                        ReadFromDB(baseQuery);
                        ViewList();
                        break;
                    case "Sort by date":
                        SortDateS();
                        break;
                }
                return false;
            }
        });
    }


    private void SaveToDB(String nameS, String dateS, String descriptionS) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("nameS", nameS);
        cv.put("dateS", dateS);
        cv.put("descriptionS", descriptionS);
        cv.put("sketchPic", "");
        long rowID = db.insert("Sketches", null, cv);
        db.close();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        menu.add("Edit");
        menu.add("Delete");
        menu.add("Alter image");
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getTitle().toString()) {
            case "Edit":
                EditSketch(sketchList.get(pos));
                break;
            case "Delete":
                DeleteSketch(sketchList.get(pos));
                break;
            case "Alter image":
                goToAddImage(sketchList.get(pos));
        }
        return super.onContextItemSelected(item);
    }

    private void goToAddImage(Sketch sketch) {
        Intent goToAlterImage = new Intent(Sketches.this, EditImage.class);
        goToAlterImage.putExtra("Id", sketchList.get(pos).getIdS());
        goToAlterImage.putExtra("currentSketch", sketch.getSketchPic());
        goToAlterImage.putExtra("requestCode", 2);
        startActivity(goToAlterImage);
        finish();
    }

    private void DeleteSketch(Sketch sketch) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure")
                .setIcon(ContextCompat.getDrawable(this, R.drawable.cancelable))
                .setMessage("delete the sketch №: " + sketch.getNameS())
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Delete(sketch);
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        builder.show();
    }

    private void Delete(Sketch sketch) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int clearCount = db.delete("Sketches", "idS = " + sketch.getIdS(), null);
        db.close();
        ReadFromDB(baseQuery);
        ViewList();
    }

    private void EditSketch(Sketch sketch) {
        final Context context = this;
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_addsketch);
        dialog.show();

        final TextView hint = dialog.findViewById(R.id.tvHintS);
        final TextView counter = dialog.findViewById(R.id.counterS);
        final EditText nameS = dialog.findViewById(R.id.etnameS);
        final TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                counter.setText(String.valueOf(charSequence.length()) + "/30");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        }; nameS.addTextChangedListener(tw);
        final EditText dateS = dialog.findViewById(R.id.etdateS);
        dateS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DoDate(dateS);
            }
        });
        final EditText descriptionS = dialog.findViewById(R.id.etdescriptionS); hint.setVisibility(View.GONE);
        descriptionS.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus || !descriptionS.getText().toString().isEmpty()) hint.setVisibility(View.GONE);
                else hint.setVisibility(View.VISIBLE);
            }
        });
        final Button save_sketch = dialog.findViewById(R.id.saveS);

        nameS.setText(sketch.getNameS());
        dateS.setText(sketch.getDateS());
        descriptionS.setText(sketch.getDescriptionS());

        save_sketch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nameS.getText().toString().isEmpty()
                        || dateS.getText().toString().isEmpty()
                        || descriptionS.getText().toString().isEmpty()) {
                    Toast.makeText(context, "Fill all the fields.", Toast.LENGTH_SHORT).show();
                    return;
                }
                UpdateDB(sketch.getIdS(), nameS.getText().toString(), dateS.getText().toString(), descriptionS.getText().toString());
                ReadFromDB(baseQuery);
                ViewList();
                dialog.dismiss();
            }
        });
    }

    private void UpdateDB(int idS, String nameS, String dateS, String descrptionS) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("nameS", nameS);
        cv.put("dateS", dateS);
        cv.put("descriptionS", descrptionS);
        int updCount = db.update("Sketches", cv, "idS = ?", new String[]{("" + idS)});
        db.close();
    }

    public void onPlusClick(View view) {
        PopupMenu menu = new PopupMenu(Sketches.this, plus);
        menu.getMenu().add("Add");
        menu.getMenu().add("Draw");
        menu.show();
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getTitle().toString()) {
                    case "Add":
                        AddSketch();
                        break;
                    case "Draw":
                        Intent goToDrawScreen = new Intent(Sketches.this, Paint.class);
                        startActivity(goToDrawScreen);
                        break;
                }
                return false;
            }
        });
    }

    private void AddSketch() {
        final Context context = this;
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_addsketch);
        dialog.show();

        final TextView hint = dialog.findViewById(R.id.tvHintS);
        final TextView counter = dialog.findViewById(R.id.counterS);
        final EditText nameS = dialog.findViewById(R.id.etnameS);
        final TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                counter.setText(String.valueOf(charSequence.length()) + "/30");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // TODO Auto-generated method stub
            }
        }; nameS.addTextChangedListener(tw);
        final EditText dateS = dialog.findViewById(R.id.etdateS); ReadOnly(dateS);
        dateS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DoDate(dateS);
            }
        });
        final EditText descriptionS = dialog.findViewById(R.id.etdescriptionS);
        descriptionS.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus || !descriptionS.getText().toString().isEmpty()) hint.setVisibility(View.GONE);
                else hint.setVisibility(View.VISIBLE);
            }
        });

        final Button save_sketch = dialog.findViewById(R.id.saveS);

        save_sketch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nameS.getText().toString().isEmpty()
                        || dateS.getText().toString().isEmpty()) {
                    Toast.makeText(context, "Fill all the fields.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (descriptionS.getText().toString().isEmpty())
                    descriptionS.setText("---");
                SaveToDB(nameS.getText().toString()
                        , dateS.getText().toString()
                        , descriptionS.getText().toString());
                ReadFromDB(baseQuery);
                ViewList();
                dialog.dismiss();
            }
        });
    }

    private void DoDate(EditText dateS) {
        currentDate();
        DatePickerDialog datePickerDialog = new DatePickerDialog(Sketches.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                String d = "";
                if (dayOfMonth < 10) d = "0" + dayOfMonth;
                else d = "" + dayOfMonth;
                if (monthOfYear + 1 < 10) d += "/0" + (monthOfYear + 1);
                else d += "/" + (monthOfYear + 1);
                d += "/" + year;
                dateS.setText(d);
            }
        }, myYear, myMonth, myDay);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private String currentDate() {
        Calendar c = Calendar.getInstance();
        myYear = c.get(Calendar.YEAR);
        myMonth = c.get(Calendar.MONTH);
        myDay = c.get(Calendar.DAY_OF_MONTH);
        String d = "";
        if (myDay < 10) d = "0" + myDay;
        else d = "" + myDay;
        if (myMonth + 1 < 10) d += "/0" + (myMonth + 1);
        else d += "/" + (myMonth + 1);
        d += "/" + myYear;
        return d;
    }

    private void ReadOnly(View view) {
        view.setFocusable(false);
        view.setClickable(false);
        view.setFocusableInTouchMode(false);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        pos = i;
        return false;
    }
}