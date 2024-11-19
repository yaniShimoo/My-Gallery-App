package com.rabin.mygallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.TransactionTooLargeException;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.os.RemoteException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class Gallery extends AppCompatActivity implements AdapterView.OnItemLongClickListener {

    ArrayList<Painting> PaintingList = new ArrayList<>();
    ListView lvP;
    DBHelper dbHelper;
    LinearLayout llsort;
    TextView sName, sDate, sCost, sSize;
    ImageView ivSort, ivSearch, ivPaint;
    EditText etPaint;
    String baseQuery = "SELECT * FROM Paintings";
    Animation sUp, sDown, slideUp, slideDown, swing;
    int pos = -1;
    int myYear, myMonth, myDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        dbHelper = new DBHelper(this, "myDB", null, DBHelper.DATABASE_VERSION);
        llsort = findViewById(R.id.llsortparameters);
        llsort.setVisibility(View.GONE);

        sUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        sDown = AnimationUtils.loadAnimation(this, R.anim.scale_down);
        slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);
        swing = AnimationUtils.loadAnimation(this, R.anim.swing);
        sSize = findViewById(R.id.bysize);
        sDate = findViewById(R.id.bydate);
        sCost = findViewById(R.id.bycost);
        sName = findViewById(R.id.byname);
        ivSort = findViewById(R.id.ivsort);
        ivSearch = findViewById(R.id.ivSearchP);
        ivPaint = findViewById(R.id.ivpaint);
        etPaint = findViewById(R.id.etpaint);
        ivPaint.startAnimation(swing);
        sDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ivPaint.startAnimation(swing);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
            }
        });
        animationImplementation();
        lvP = findViewById(R.id.lvpictures);
        lvP.setOnItemLongClickListener(this);
        registerForContextMenu(lvP);
        ReadFromDB(baseQuery);
        ViewList();
    }

    private void animationImplementation() {
        ivPaint.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    ivPaint.startAnimation(sUp);
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    ivPaint.startAnimation(sDown);
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
        sSize.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    sSize.startAnimation(sUp);
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    sSize.startAnimation(sDown);
                return false;
            }
        });
        sName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    sName.startAnimation(sUp);
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    sName.startAnimation(sDown);
                return false;
            }
        });
        sDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    sDate.startAnimation(sUp);
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    sDate.startAnimation(sDown);
                return false;
            }
        });
        sCost.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    sCost.startAnimation(sUp);
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    sCost.startAnimation(sDown);
                return false;
            }
        });
    }

    public void SearchForPainting(View view) {
        String[] op = {"idP", "nameP", "widthP", "heightP", "dateP", "descriptionP", "paintingPic", "costP"};
        readBySearch(op);
        ViewList();
    }

    private void readBySearch(String[] columns) {
        PaintingList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String topic = etPaint.getText().toString();
        String selection = "idP LIKE ? OR nameP LIKE ? OR widthP LIKE ? OR heightP LIKE ? OR dateP LIKE ? OR descriptionP LIKE ? OR costP LIKE ?";
        String[] selectionArgs = { "%" + topic + "%", "%" + topic + "%", "%" + topic + "%", "%" + topic + "%", "%" + topic + "%", "%" + topic + "%", "%" + topic + "%"};
        Cursor c = db.query("Paintings", columns, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()){
            do {
                int idP = c.getInt((int) c.getColumnIndex("idP"));
                String nameP = c.getString((int) c.getColumnIndex("nameP"));
                int widthP = c.getInt((int) c.getColumnIndex("widthP"));
                int heightP = c.getInt((int) c.getColumnIndex("heightP"));
                String dateP = c.getString((int) c.getColumnIndex("dateP"));
                String descriptionP = c.getString((int) c.getColumnIndex("descriptionP"));
                int costP = c.getInt((int) c.getColumnIndex("costP"));
                Painting painting = new Painting(idP, nameP, widthP, heightP, dateP, descriptionP, costP);
                String paintingPic = getPaintingPicById(idP);
                String uriPic = getUriPicById(idP);
                painting.setPaintingPic(paintingPic);
                painting.setUriP(uriPic);

                PaintingList.add(painting);
            }while (c.moveToNext());
        }
        c.close();
        db.close();
    }

    private String getUriPicById(int idP) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {"uriP"};
        String selection = "idP = ?";
        String[] selectionArgs = {String.valueOf(idP)};
        Cursor c = db.query("Paintings", projection, selection, selectionArgs, null,null,null);
        String uri = "";
        if (c.moveToFirst())
            uri = c.getString((int) c.getColumnIndex("uriP"));
        c.close();
        db.close();
        return uri;
    }

    private String getPaintingPicById(int idP) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {"paintingPic"};
        String selection = "idP = ?";
        String[] selectionArgs = {String.valueOf(idP)};
        Cursor c = db.query("Paintings", projection, selection, selectionArgs, null,null,null);
        String pic = "";
        if (c.moveToFirst())
            pic = c.getString((int)c.getColumnIndex("paintingPic"));
        c.close();
        db.close();
        return pic;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add("Image");
        menu.add("Edit");
        menu.add("Delete");
        menu.add("View image");
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getTitle().toString()) {
            case "Image":
                goToAddImage(PaintingList.get(pos));
                break;
            case "Edit":
                Edit(PaintingList.get(pos));
                break;
            case "Delete":
                Delete(PaintingList.get(pos));
                break;
            case "View image":
                viewImage(PaintingList.get(pos));
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void viewImage(Painting painting) {
        if (painting.getUriP() != null){
            Intent goToFullscreenMode = new Intent();
            goToFullscreenMode.setAction(Intent.ACTION_VIEW);
            goToFullscreenMode.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            String picPath = FileHelper.getRealPathFromURI(this, Uri.parse(painting.getUriP()));
            Toast.makeText(this, "" + picPath, Toast.LENGTH_SHORT).show();
            File picFile = new File(picPath);
            goToFullscreenMode.setDataAndType(FileProvider.getUriForFile(this,
                    getApplicationContext().getPackageName() + ".provider",
                    picFile), "image/*");
            //goToFullscreenMode.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(goToFullscreenMode);
        }
        else
            Toast.makeText(this, "You have not set an image.", Toast.LENGTH_SHORT).show();
    }

    private void goToAddImage(Painting painting) {
        Intent goToAddImage = new Intent(Gallery.this, EditImage.class);
        goToAddImage.putExtra("ID_P", PaintingList.get(pos).getIdP());
        goToAddImage.putExtra("currentImage", painting.getPaintingPic());
        goToAddImage.putExtra("requestCode", 1);
        startActivity(goToAddImage);
        finish();
    }


    private void Delete(Painting painting) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure you want to delete this picture?")
                .setIcon(ContextCompat.getDrawable(Gallery.this, R.drawable.cancelable))
                .setMessage("delete painting : " + painting.getNameP())
                .setCancelable(false).setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        int delCount = db.delete("Paintings", "idP = " + painting.getIdP(), null);
                        db.close();
                        ReadFromDB(baseQuery);
                        ViewList();
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.show();
    }

    private void Edit(Painting painting) {
        final Context context = this;
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_addpainting);
        dialog.show();

        final TextView counter = dialog.findViewById(R.id.counterP);
        final TextView hint = dialog.findViewById(R.id.tvHint);
        final EditText etname = dialog.findViewById(R.id.etnameP);
        TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                counter.setText(String.valueOf(charSequence.length()) + "/30");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // TODO Auto-generated method stub
            }
        };  etname.addTextChangedListener(tw);
        final EditText etwidth = dialog.findViewById(R.id.etWidthP);
        final EditText etheight = dialog.findViewById(R.id.etHeightP);
        final EditText etdate = dialog.findViewById(R.id.etdateP); ReadOnly(etdate);
        etdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choiceDate(etdate);
            }
        });
        final EditText etdescription = dialog.findViewById(R.id.etdescriptionP); hint.setVisibility(View.GONE);
        etdescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus || !etdescription.getText().toString().isEmpty()) hint.setVisibility(View.GONE);
                else hint.setVisibility(View.VISIBLE);
            }
        });
        final EditText etcost = dialog.findViewById(R.id.etcostP);

        etname.setText(painting.getNameP());
        etwidth.setText(Integer.toString(painting.getWidthP()));
        etheight.setText(Integer.toString(painting.getHeightP()));
        etdate.setText(painting.getDateP());
        etdescription.setText(painting.getDescriptionP());
        etcost.setText((Integer.toString(painting.getCostP())));


        final Button save = dialog.findViewById(R.id.saveP);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etname.getText().toString().isEmpty() ||
                        etwidth.getText().toString().isEmpty() ||
                        etheight.getText().toString().isEmpty() ||
                        etdate.getText().toString().isEmpty() ||
                        etcost.getText().toString().isEmpty()) {
                    Toast.makeText(context, "fill up all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (etdescription.getText().toString().isEmpty()){
                    etdescription.setText("---");
                }
                UpdateDB(painting.getIdP(), etname.getText().toString(), etwidth.getText().toString(), etheight.getText().toString(), etdate.getText().toString(), etdescription.getText().toString(), etcost.getText().toString());
                ReadFromDB(baseQuery);
                ViewList();
                dialog.dismiss();
            }
        });

    }

    private void UpdateDB(int idP, String name, String width, String height, String date, String description, String cost) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("nameP", name);
        cv.put("widthP", Integer.parseInt(width));
        cv.put("heightP", Integer.parseInt(height));
        cv.put("dateP", date);
        if (description.isEmpty())
            description = "";
        cv.put("descriptionP", description);
        cv.put("costP", Integer.parseInt(cost));
        int updCount = db.update("Paintings", cv, "idP = ?", new String[]{("" + idP)});
        db.close();
    }


    private void ReadFromDB(String selectQuery) {
        PaintingList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                int idP = c.getInt((int) c.getColumnIndex("idP"));
                String nameP = c.getString((int) c.getColumnIndex("nameP"));
                int widthP = c.getInt((int) c.getColumnIndex("widthP"));
                int heightP = c.getInt((int) c.getColumnIndex("heightP"));
                String dateP = c.getString((int) c.getColumnIndex("dateP"));
                String descriptionP = c.getString((int) c.getColumnIndex("descriptionP"));
                String paintingPic = c.getString((int) c.getColumnIndex("paintingPic"));
                String uriPic = c.getString((int) c.getColumnIndex("uriP"));
                int costP = c.getInt((int) c.getColumnIndex("costP"));
                PaintingList.add(new Painting(idP, nameP, widthP, heightP, dateP, descriptionP, paintingPic, uriPic, costP));
            } while (c.moveToNext());
        }
        c.close();
        db.close();
    }

    private void ViewList() {
        PaintingAdapter adp = new PaintingAdapter(getApplicationContext(), R.layout.listitem_painting, PaintingList);
        lvP.setAdapter(adp);
    }

    public void AddPainting(View view) {
        final Context context = this;
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_addpainting);
        dialog.show();

        final TextView counter = dialog.findViewById(R.id.counterP);
        final TextView hint = dialog.findViewById(R.id.tvHint);
        final EditText etname = dialog.findViewById(R.id.etnameP);
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
        }; etname.addTextChangedListener(tw);
        final EditText etwidth = dialog.findViewById(R.id.etWidthP);
        final EditText etheight = dialog.findViewById(R.id.etHeightP);
        final EditText etdate = dialog.findViewById(R.id.etdateP); ReadOnly(etdate);
        etdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choiceDate(etdate);
            }
        });
        final EditText etdescription = dialog.findViewById(R.id.etdescriptionP);
        etdescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus || !etdescription.getText().toString().isEmpty()) hint.setVisibility(View.GONE);
                else hint.setVisibility(View.VISIBLE);
            }
        });
        final EditText etcost = dialog.findViewById(R.id.etcostP);
        final Button save = dialog.findViewById(R.id.saveP);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etname.getText().toString().isEmpty() ||
                        etwidth.getText().toString().isEmpty() ||
                        etheight.getText().toString().isEmpty() ||
                        etdate.getText().toString().isEmpty() ||
                        etcost.getText().toString().isEmpty()) {
                    Toast.makeText(context, "fill up all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (etdescription.getText().toString().isEmpty()){
                    etdescription.setText("---");
                }
                SaveToDB(etname.getText().toString(), etwidth.getText().toString(), etheight.getText().toString(), etdate.getText().toString(), etdescription.getText().toString(), etcost.getText().toString());
                ReadFromDB(baseQuery);
                ViewList();
                dialog.dismiss();
            }
        });
    }

    private void choiceDate(EditText etDate) {
        currentDate();
        DatePickerDialog datePickerDialog = new DatePickerDialog(Gallery.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                String d = "";
                if (dayOfMonth < 10) d = "0" + dayOfMonth;
                else d = "" + dayOfMonth;
                if (monthOfYear + 1 < 10) d += "/0" + (monthOfYear + 1);
                else d += "/" + (monthOfYear + 1);
                d += "/" + year;
                etDate.setText(d);
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
        String date = "";
        if (myDay < 10) date = "0" + myDay;
        else date = "" + myDay;
        if (myMonth + 1 < 10) date += "/0" + (myMonth + 1);
        else date += "/" + (myMonth + 1);
        date += "/" + myYear;
        return date;
    }

    private void ReadOnly(View view) {
        view.setFocusable(false);
        view.setClickable(false);
        view.setFocusableInTouchMode(false);
    }

    private void SaveToDB(String etname, String width, String height, String etdate, String etdescription, String etcost) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("nameP", etname);
        cv.put("widthP", Integer.parseInt(width));
        cv.put("heightP", Integer.parseInt(height));
        cv.put("dateP", etdate);
        if (etdescription.isEmpty()) etdescription = "";
        cv.put("descriptionP", etdescription);
        cv.put("paintingPic", "");
        cv.put("costP", Integer.parseInt(etcost));
        long rowID = db.insert("Paintings", null, cv);
        db.close();
    }

    public void ShowSort(View view) {

        if (llsort.getVisibility() == view.GONE){
            llsort.setVisibility(View.VISIBLE);
            llsort.startAnimation(slideDown);
        }
        else{
            llsort.startAnimation(slideUp);
            llsort.setVisibility(view.GONE);
        }

    }

    public void SortName(View view) {
        String query = "SELECT * FROM Paintings ORDER BY nameP";
        ReadFromDB(query);
        ViewList();
    }

    public void SortSize(View view) {
        PopupMenu menu = new PopupMenu(this, sSize);
        menu.getMenu().add("Width");
        menu.getMenu().add("Height");
        menu.show();
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getTitle().toString()){
                    case "Width":
                        String queryW = "SELECT * FROM Paintings ORDER BY widthP DESC";
                        ReadFromDB(queryW);
                        ViewList();
                        break;
                    case "Height":
                        String queryH = "SELECT * FROM Paintings ORDER BY heightP DESC";
                        ReadFromDB(queryH);
                        ViewList();
                        break;
                }
                return false;
            }
        });
    }

    public void SortDate(View view) {
        String query = "SELECT * FROM Paintings ORDER BY dateP ASC";
        ReadFromDB(query);
        ViewList();
    }

    public void SortCost(View view) {
        String query = "SELECT * FROM Paintings ORDER BY costP DESC";
        ReadFromDB(query);
        ViewList();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        pos = i;
        return false;
    }
}