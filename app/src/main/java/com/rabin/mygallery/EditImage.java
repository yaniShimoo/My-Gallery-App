package com.rabin.mygallery;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditImage extends AppCompatActivity {

    ImageView myPic;
    DBHelper dbHelper;
    Button saveImg;
    TextView paintingId;
    String MYIMAGE = "";
    Animation sUp, sDown, slideUp, slideDown;
    String lastImage;
    ActivityResultLauncher<Intent> myStartForResult;
    int requestCode;
    String imgUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_editimage);
        dbHelper = new DBHelper(this, "myDB", null, DBHelper.DATABASE_VERSION);
        sUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        sDown = AnimationUtils.loadAnimation(this, R.anim.scale_down);
        slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);
        paintingId = findViewById(R.id.painting_id);
        myPic = findViewById(R.id.my_pic);
        saveImg = findViewById(R.id.saveImg);
        saveImg.setVisibility(View.GONE);
        saveImg.startAnimation(slideUp);
        Intent takePainting = getIntent();
        requestCode = takePainting.getIntExtra("requestCode", 0);
        if (requestCode == 1){
            paintingId.setText("" + takePainting.getIntExtra("ID_P", 0));
            lastImage = takePainting.getStringExtra("currentImage");
        }
        else if (requestCode == 2){
            paintingId.setText("" + takePainting.getIntExtra("Id", 0));
            lastImage = takePainting.getStringExtra("currentSketch");
        }
        imageListener(lastImage);
        animationImplementation();

        registerResult();
        myPic.setOnClickListener(view -> pickUpImage());
    }

    private void imageListener(String picture) {
        try {
            Bitmap bitmap = StringToBitMap(picture);
            myPic.setImageBitmap(bitmap);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void animationImplementation() {
        saveImg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    saveImg.startAnimation(sUp);
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    saveImg.startAnimation(sDown);
                return false;
            }
        });
    }

    private void pickUpImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        myStartForResult.launch(intent);
    }

    private void registerResult() {
        myStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result != null && result.getResultCode() == RESULT_OK && result.getData().getData() != null) {
                            Uri imageUri = result.getData().getData();
                            if (imageUri != null){
                                myPic.setImageURI(imageUri);
                                imgUri = imageUri.toString();
                                saveImg.setVisibility(View.VISIBLE);
                                saveImg.startAnimation(slideDown);
                            }
                        }
                        try {
                            if (result.getData().getData() != null){
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result.getData().getData());
                                myPic.setImageBitmap(bitmap);
                                MYIMAGE = BitMapToString(bitmap);
                            }
                            //sample = createSample(bitmap);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
    private void saveImageToDB(String MYIMAGE, String imgUri) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("paintingPic", MYIMAGE);
        cv.put("uriP", imgUri);
        int updCount = db.update("Paintings", cv, "idP = ?", new String[]{ paintingId.getText().toString()});
        db.close();
        Toast.makeText(getApplicationContext(), "pic saved!", Toast.LENGTH_SHORT).show();
    }

    public void SaveMyImg(View view) {
        Toast.makeText(getApplicationContext(), "Exccelent", Toast.LENGTH_SHORT).show();
        if (requestCode == 1){
            saveImageToDB(MYIMAGE, imgUri);
            Toast.makeText(getApplicationContext(), "Back!", Toast.LENGTH_SHORT).show();
            Intent back = new Intent(EditImage.this , Gallery.class);
            startActivity(back);
            finish();
        }
        else if (requestCode == 2){
            saveSketchToDB(MYIMAGE);
            Toast.makeText(getApplicationContext(), "Back!", Toast.LENGTH_SHORT).show();
            Intent back = new Intent(EditImage.this , Sketches.class);
            startActivity(back);
            finish();
        }
    }

    private void saveSketchToDB(String myimage) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("sketchPic", myimage);
        int updCount = db.update("Sketches", cv, "idS = ?", new String[]{ paintingId.getText().toString()});
        db.close();
        Toast.makeText(getApplicationContext(), "sketch saved!", Toast.LENGTH_SHORT).show();
    }

    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }
}