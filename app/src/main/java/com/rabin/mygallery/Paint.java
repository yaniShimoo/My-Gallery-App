package com.rabin.mygallery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.chrono.Era;
import java.util.Calendar;
import java.util.Date;

import yuku.ambilwarna.AmbilWarnaDialog;


public class Paint extends AppCompatActivity {

    DBHelper dbHelper;
    private FrameLayout frame;
    private PaintView paintView;
    ImageView ivPencil,ivEraser, ivText;
    SeekBar sbPencilSize;
    LottieAnimationView saveB;
    private ImageView ivColorPreview;
    private int defaultColor;
    float w = 15;
    boolean move;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);
        dbHelper = new DBHelper(this, "myDB", null, DBHelper.DATABASE_VERSION);

        ScreenshotUtils.verifyStoragePermission(this);
        frame = findViewById(R.id.frm);
        frame.setBackgroundColor(Color.TRANSPARENT);
        move = true;
        saveB = findViewById(R.id.save_D);
        saveB.setMinAndMaxProgress(0.0f, 0.5f);
        saveB.playAnimation();
        saveB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScreenshotUtils.takeScreenshot(frame, dbHelper, Paint.this);
                saveB.setClickable(false);
                saveB.setMinAndMaxProgress(0.5f, 1.0f);
                saveB.playAnimation();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        saveB.setClickable(true);
                        saveB.setMinAndMaxProgress(0.0f, 0.5f);
                        saveB.playAnimation();
                    }
                };
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(runnable, 3000);

            }
        });
        ivPencil = findViewById(R.id.pencil_D);
        ivColorPreview = findViewById(R.id.myColor);
        ivEraser = findViewById(R.id.eraser_D);
        ivText = findViewById(R.id.text_D);
        sbPencilSize = findViewById(R.id.pencilSize_D);
        sbPencilSize.setMax(100);
        sbPencilSize.setProgress((int) w);
        defaultColor = 0;

        ivEraser.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                defaultColor = Color.parseColor("#efece7");
                ivColorPreview.setColorFilter(defaultColor, PorterDuff.Mode.SRC_ATOP);
                ivColorPreview.setTag(String.format("#%06X", (0xFFFFFF & defaultColor)));
                paintView.setCurrentColor(ivColorPreview.getTag().toString());
                return true;
            }
        });

        sbPencilSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                alterSize(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        paintView = new PaintView(this);
        frame.addView(paintView);
    }

    private void changeColor() {
        final AmbilWarnaDialog colorPickerDialogue = new AmbilWarnaDialog(this, defaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                defaultColor = color;
                ivColorPreview.setColorFilter(defaultColor, PorterDuff.Mode.SRC_ATOP);
                ivColorPreview.setTag(String.format("#%06X", (0xFFFFFF & defaultColor)));
                paintView.setCurrentColor(ivColorPreview.getTag().toString());
            }
        });
        colorPickerDialogue.show();
    }

    public void changePoint(View view){
        paintView.setCurrentPoint(view.getTag().toString());
        changeColor();
    }

    private void alterSize(int i) {
        w = (float) i;
        paintView.setWidth(w);
    }

    public void Undo(View view) { paintView.undoShape(); }

    public void generateText(View view){
        EditText et = new EditText(this);
        et.setHint("Enter text here");
       /* et.setHeight(30);
        et.setWidth(30);*/
        et.setTextColor(Color.BLACK);
        et.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        et.setOnTouchListener(new View.OnTouchListener() {
            float dX, dY;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (move){
                    switch (motionEvent.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            dX = view.getX() - motionEvent.getRawX();
                            dY = view.getY() - motionEvent.getRawY();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            view.animate()
                                    .x(motionEvent.getRawX() + dX)
                                    .y(motionEvent.getRawY() + dY)
                                    .setDuration(0)
                                    .start();
                            break;
                        default:
                            return false;
                    }
                }
                else {
                    clampTextBoxPosition(et);
                }
                return false;
            }
        });
        frame.addView(et);
        et.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                createOptionsMenu(view);
                return true;
            }
        });
    }

    private void clampTextBoxPosition(EditText editText) {
        View parentView = (View) editText.getParent();
        int parentWidth = parentView.getWidth();
        int parentHeight = parentView.getHeight();

        int etWidth = editText.getWidth();
        int etHeight = editText.getHeight();

        int maxX = parentWidth - etWidth;
        int maxY = parentHeight - etHeight;

        int currentX = (int) editText.getX();
        int currentY = (int) editText.getY();

        int clampedX = Math.max(0, Math.min(maxX, currentX));
        int clampedY = Math.max(0, Math.min(maxY, currentY));

        editText.setX(clampedX);
        editText.setY(clampedY);
    }

    public void buttonSaveImage(View view){
        Date now = new Date();
        DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
        try {
            String myPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";
            /* v.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v.getDrawingCache());
            v.setDrawingCacheEnabled(false); */
            Bitmap bitmap = Screenshot();
            File imgFile = new File(myPath);
            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();

            FileOutputStream fileOutputStream = new FileOutputStream(imgFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

            //Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
            //openScreenshot(imgFile);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private Bitmap Screenshot(){
        View v = getWindow().getDecorView().getRootView();
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

    private void openScreenshot(File imgFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imgFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }

    private void createOptionsMenu(View view) {
        PopupMenu menu = new PopupMenu(Paint.this, view);
        if (move){
            menu.getMenu().add("Remove");
            menu.getMenu().add("Clamp");
        }
        else {
            menu.getMenu().add("Remove");
            menu.getMenu().add("Unclamp");
        }
        menu.show();

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.toString().equals("Remove"))
                    view.setVisibility(View.GONE);
                if (menuItem.toString().equals("Clamp")) {
                    move = false;
                    Toast.makeText(Paint.this, "" + move, Toast.LENGTH_SHORT).show();
                }
                if (menuItem.toString().equals("Unclamp")) {
                    move = true;
                    Toast.makeText(Paint.this, "" + move, Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }
}