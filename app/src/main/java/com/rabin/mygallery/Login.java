package com.rabin.mygallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
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

public class Login extends AppCompatActivity {

    EditText etPass;
    String savedPassword, savedPhone;
    Animation sUp, sDown;
    Button enter;
    TextView passCount;
    int attempt = 5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        requestSmsPermission();
        sUp = AnimationUtils.loadAnimation(Login.this, R.anim.scale_up);
        sDown = AnimationUtils.loadAnimation(Login.this, R.anim.scale_down);
        etPass = findViewById(R.id.et5);
        enter = findViewById(R.id.benter);
        passCount = findViewById(R.id.counter_Pass);
        TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passCount.setText(String.valueOf(charSequence.length()) + "/25");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // TODO Auto-generated method stub
            }
        };  etPass.addTextChangedListener(tw);
        SharedPreferences sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        savedPassword = sPref.getString("PASSWORD", "");
        savedPhone = sPref.getString("PHONE", "");
        animationImplementation();
    }

    private void animationImplementation() {
        enter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    enter.startAnimation(sUp);
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    enter.startAnimation(sDown);
                return false;
            }
        });
    }

    public void Enter(View view){
        if (etPass.getText().toString().isEmpty()){
            Toast.makeText(this, "fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (etPass.getText().toString().equals(savedPassword)) {
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            if (attempt == 0){
                attempt = 5;
                SendSMS(savedPhone);
            }
            Toast.makeText(this, "Incorrect password, you have - " + attempt + "attempts", Toast.LENGTH_SHORT).show();
            attempt--;
        }
    }

    private void SendSMS(String savedPhone) {
        //SharedPreferences sPrefs = getSharedPreferences("MyPref", MODE_PRIVATE);
        String message = "Here is your password: " + savedPassword + ".  Now log again";
        sendMsg(savedPhone, message);
    }

    private void sendMsg(String savedPhone, String message) {
        //requestSmsPermission();
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(savedPhone, null, message, null, null);
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

}