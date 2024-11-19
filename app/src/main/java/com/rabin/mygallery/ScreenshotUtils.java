package com.rabin.mygallery;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

public class ScreenshotUtils {
    /*
      public static void captureScreenshot(FrameLayout frm){
          Bitmap bitmap = getBitmapFromView(frm);
          saveBitmapToFile(bitmap);
          Toast.makeText(frm.getContext(), "Done!", Toast.LENGTH_SHORT).show();
      }

      private static void saveBitmapToFile(Bitmap bitmap) {
          String dirPath  = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures" + "/Yanik";
          File dirFile = new File(dirPath);
          if (!dirFile.exists()){
              dirFile.mkdirs();
          }

          File imgFile = new File(dirFile, "yanik.jpg");

          try {
              FileOutputStream fileOutputStream = new FileOutputStream(imgFile);
              bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
              fileOutputStream.flush();
              fileOutputStream.close();
          }catch (Exception e){
              e.printStackTrace();
          }
      }

      private static Bitmap getBitmapFromView(View view) {
          Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
          Canvas canvas = new Canvas(bitmap);
          view.draw(canvas);
          return bitmap;
      } */

    /* nameS text,"
                + "dateS text,"
                + "descriptionS text,"
                + "sketchPic*/
    private static int myYear, myMonth, myDay;
    private static final String tableName = "Sketches";
    private static final String[] columnName = {"nameS", "dateS", "descriptionS", "sketchPic"};
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSION_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    public static void verifyStoragePermission(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSION_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }

    public static void takeScreenshot(final FrameLayout frm, DBHelper dbHelper, Context context) {
        frm.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                frm.getViewTreeObserver().removeOnPreDrawListener(this);
                /* Bitmap bitmap = Bitmap.createBitmap(frm.getWidth(), frm.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                frm.draw(canvas); */
                Bitmap bitmap = captureScreenshot(frm);
                String pic = BitMapToString(bitmap);
                //insertScreenshot(pic, dbHelper, context);
                insertScreenshot(bitmap, pic, dbHelper, context);
                //saveBitmapToFile(bitmap, "yanik.jpg");
                return false;
            }
        });
        frm.requestLayout();
    }

    private static Bitmap captureScreenshot(FrameLayout frameLayout) {
        Bitmap bitmap = Bitmap.createBitmap(frameLayout.getWidth(), frameLayout.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable bg = frameLayout.getBackground();
        canvas.drawColor(Color.parseColor("#efece7"));
        bg.draw(canvas);
        frameLayout.draw(canvas);
        return bitmap;
    }

    private static void insertScreenshot(Bitmap bitmap, String pic, DBHelper dbHelper, Context c) {
        final Context context = c;
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_addsketch);
        dialog.show();

        final TextView counter = dialog.findViewById(R.id.counterS);
        final TextView hint = dialog.findViewById(R.id.tvHintS);
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
                DoDate(dateS, c);
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
                if (descriptionS.getText().toString().isEmpty()){
                    descriptionS.setText("---");
                }
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put(columnName[0], nameS.getText().toString());
                cv.put(columnName[1], dateS.getText().toString());
                cv.put(columnName[2], descriptionS.getText().toString());
                cv.put(columnName[3], pic);
                long rowID = db.insert(tableName, null, cv);
                db.close();
                saveBitmapToFile(bitmap, nameS.getText().toString());
                dialog.dismiss();
            }
        });
    }

    private static void DoDate(EditText dateS, Context c) {
        currentDate();
        DatePickerDialog datePickerDialog = new DatePickerDialog(c, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                String _date = "";
                if (dayOfMonth < 10) _date = "0" + dayOfMonth;
                else _date = "" + dayOfMonth;
                if (monthOfYear + 1 < 10) _date += "/0" + (monthOfYear + 1);
                else _date += "/" + (monthOfYear + 1);
                _date += "/" + year;
                dateS.setText(_date);
            }
        }, myYear, myMonth, myDay);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private static String currentDate() {
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

    private static void ReadOnly(View view) {
        view.setFocusable(false);
        view.setClickable(false);
        view.setFocusableInTouchMode(false);
    }


    private static void saveBitmapToFile(Bitmap bitmap, String s) {
        String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download";
        //File dir = Environment.getExternalStorageDirectory();
        File dirFile = new File(dir, (s + ".jpg"));
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(dirFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

}
