package com.rabin.mygallery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registration extends AppCompatActivity {

    EditText etPhone, etPass, etRepeat;
    TextView countRepeat, countPass;
    Button bDone;
    Animation sUp, sDown;
    final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        sUp = AnimationUtils.loadAnimation(Registration.this, R.anim.scale_up);
        sDown = AnimationUtils.loadAnimation(Registration.this, R.anim.scale_down);
        bDone = findViewById(R.id.done);
        etPhone = findViewById(R.id.et1);
        etPass = findViewById(R.id.et2);
        etRepeat = findViewById(R.id.et3);
        countPass = findViewById(R.id.counter_Pass_R);
        countRepeat = findViewById(R.id.counter_Repeat_R);
        TextWatcher twPass = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                countPass.setText(String.valueOf(charSequence.length()) + "/25");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // TODO Auto-generated method stub
            }
        }; etPass.addTextChangedListener(twPass);
        TextWatcher twRepeat = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                countRepeat.setText(String.valueOf(charSequence.length()) + "/25");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // TODO Auto-generated method stub
            }
        }; etRepeat.addTextChangedListener(twRepeat);
        animationImplementation();
    }

    private void animationImplementation() {
        bDone.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    bDone.startAnimation(sUp);
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    bDone.startAnimation(sDown);
                return false;
            }
        });
    }

    public void Done(View view){
        if( ! etPass.getText().toString().equals(etRepeat.getText().toString())){
            Toast.makeText(this, "password is invalid", Toast.LENGTH_SHORT).show();
            return;
        }
        if (etPhone.getText().toString().length() != 10 || !etPhone.getText().toString().startsWith("05")){
            Toast.makeText(this, "phone number is invalid", Toast.LENGTH_SHORT).show();
            return;
        }
        if (etPhone.getText().toString().isEmpty()
                || etPass.getText().toString().isEmpty()
                || etRepeat.getText().toString().isEmpty()){
            Toast.makeText(this, "fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isValidPassword(etPass.getText().toString().trim())){
            Toast.makeText(this, "Make sure your password has: \n Capital \n Symbol \n No spaces \n Number", Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("PHONE", etPhone.getText().toString());
        ed.putString("PASSWORD", etPass.getText().toString());
        ed.commit();

        Intent intent = new Intent(Registration.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean isValidPassword(final String pass) {
        Pattern pattern;
        Matcher matcher;
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(pass);
        return matcher.matches();
    }
}