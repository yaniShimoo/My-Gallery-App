package com.rabin.mygallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Customers extends AppCompatActivity implements AdapterView.OnItemLongClickListener {

    ImageView ivAddCustomer, ivSearch, ivSort;
    EditText search;
    DBHelper dbHelper;
    Animation sUp, sDown, swing, slideUp, slideDown;
    ArrayList<Customer> CustomerList = new ArrayList<>();
    String baseQuery = "SELECT * FROM Customers";
    String selectQueryLike = "SELECT * FROM Customers";
    ListView lv;
    int pos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers);

        sUp = AnimationUtils.loadAnimation(Customers.this, R.anim.scale_up);
        sDown = AnimationUtils.loadAnimation(Customers.this, R.anim.scale_down);
        slideUp = AnimationUtils.loadAnimation(Customers.this, R.anim.slide_up);
        slideDown = AnimationUtils.loadAnimation(Customers.this, R.anim.slide_down);
        swing = AnimationUtils.loadAnimation(Customers.this, R.anim.swing);
        search = findViewById(R.id.etcustomer);
        ivSearch = findViewById(R.id.ivsearchc);
        ivSort = findViewById(R.id.ivsortc);
        ivAddCustomer = findViewById(R.id.ivaddcustomer); ivAddCustomer.startAnimation(swing);
        sDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ivAddCustomer.startAnimation(swing);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
            }
        });

        dbHelper = new DBHelper(this, "myDB", null, DBHelper.DATABASE_VERSION);
        lv = findViewById(R.id.lvcustomers);
        lv.setOnItemLongClickListener(this);
        registerForContextMenu(lv);
        animationImplementation();
        ReadFromDB(baseQuery);
        ViewList();
    }

    private void animationImplementation() {
        ivAddCustomer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    ivAddCustomer.startAnimation(sUp);
                }
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                    ivAddCustomer.startAnimation(sDown);
                }
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
    }

    public void sorter(View view){
        String selectQuery = "SELECT * FROM Customers ORDER BY nameC";
        ReadFromDB(selectQuery);
        ViewList();
    }
    /*"idC integer primary key autoincrement,"
                + "nameC text,"
                + "phoneC text,"
                + "adressC text"
                + ");");*/
    public void SearchForCustomer(View view) {
        String[] op = {"idC", "nameC", "phoneC", "adressC"};
        readBySearch(op);
        ViewList();
        /* SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selectQuery = "SELECT * FROM Customers WHERE nameC = " + "'" + search.getText().toString() + "'" ;
        selectQueryLike = "SELECT * FROM Customers WHERE nameC LIKE " + "'%" + search.getText().toString() + "%'";
        if (!search.getText().toString().isEmpty()){
            ReadFromDB(selectQuery);
            ViewList();
            db.close();
        }
        else {
            ReadFromDB(baseQuery);
            ViewList();
            db.close();
        }  */
    }

    private void readBySearch(String[] columns) {
        CustomerList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String topic = search.getText().toString();
        String selection = "idC LIKE ? OR nameC LIKE ? OR phoneC LIKE ? OR adressC LIKE ?";
        String[] selectionArgs = {"%" + topic + "%", "%" + topic + "%", "%" + topic + "%", "%" + topic + "%"};
        Cursor c = db.query("Customers", columns, selection, selectionArgs, null,null,null);
        if (c.moveToFirst()){
            do {
                int idC = c.getInt((int) c.getColumnIndex("idC"));
                String nameC = c.getString((int) c.getColumnIndex("nameC"));
                String phoneC = c.getString((int) c.getColumnIndex("phoneC"));
                String adressC = c.getString((int) c.getColumnIndex("adressC"));
                CustomerList.add(new Customer(idC, nameC, phoneC, adressC));
            }while (c.moveToNext());
        }
        c.close();
        db.close();
    }

    private void ViewList() {
        /*ArrayList<String> ALL = new ArrayList<>();
        for (int i = 0; i < CustomerList.size(); i++) {
            ALL.add(CustomerList.get(i).toString());
        } */
        OnlyCustomerAdapter adapter = new OnlyCustomerAdapter(this, R.layout.listitem_only_customers, CustomerList);
        lv.setAdapter(adapter);
    }

    private void ReadFromDB(String selectQuery) {
        CustomerList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()){
            do {
                int idC = c.getInt((int) c.getColumnIndex("idC"));
                String nameC = c.getString((int) c.getColumnIndex("nameC"));
                String phoneC = c.getString((int) c.getColumnIndex("phoneC"));
                String adressC = c.getString((int) c.getColumnIndex("adressC"));
                CustomerList.add(new Customer(idC, nameC, phoneC, adressC));
            }while (c.moveToNext());
        }
        c.close();
        db.close();
    }

    public void AddCustomer(View view){
        final Context context = this;
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_addcustomer);
        dialog.show();

        final TextView counter1 = dialog.findViewById(R.id.counterC_a);
        final TextView counter2 = dialog.findViewById(R.id.counterC_b);
        final EditText etname = dialog.findViewById(R.id.etnameC);
        TextWatcher tw1 = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                counter1.setText(String.valueOf(charSequence.length()) + "/30");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // TODO Auto-generated method stub
            }
        };  etname.addTextChangedListener(tw1);
        final EditText etphone = dialog.findViewById(R.id.etPhoneC);
        final EditText etadress = dialog.findViewById(R.id.etAdressC);
        TextWatcher tw2 = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                counter2.setText(String.valueOf(charSequence.length()) + "/30");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // TODO Auto-generated method stub
            }
        }; etadress.addTextChangedListener(tw2);
        final Button saveC = dialog.findViewById(R.id.saveC);
        saveC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etname.getText().toString().isEmpty() ||
                        etphone.getText().toString().isEmpty() ||
                        etadress.getText().toString().isEmpty()){
                    Toast.makeText(context, "fill up all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(etphone.getText().toString().length() != 10 || !etphone.getText().toString().startsWith("05")){
                    Toast.makeText(context, "your phone number is invalied", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Duplication(etphone.getText().toString(), etadress.getText().toString())){
                    Toast.makeText(context, "this customer is already exists", Toast.LENGTH_SHORT).show();
                    return;
                }
                SaveToDB(etname.getText().toString(), etphone.getText().toString(), etadress.getText().toString());
                ReadFromDB(baseQuery);
                ViewList();
                dialog.dismiss();
            }
        });

    }

    private boolean Duplication(String phone, String adress) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selectQuery = "SELECT nameC FROM Customers WHERE phoneC = " + "'" + phone + "'";
        Cursor c;
        c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()){
            Toast.makeText(this, "duplicate phone", Toast.LENGTH_SHORT).show();
            return true;
        }
        selectQuery = "SELECT nameC FROM Customers WHERE adressC = " + "'" + adress + "'";
        c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()){
            Toast.makeText(this, "duplicate adress", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, view, menuInfo);
        menu.add("Edit");
        menu.add("Delete");
        menu.add("Make call");
    }
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item){
        /*if (item.toString().equals("Edit"))
            EditCust(CustomerList.get(pos));
        if (item.toString().equals("Delete"))
            DeleteCust(CustomerList.get(pos));
        if (item.toString().equals("Make call"))
            CallToCust(CustomerList.get(pos).getPhoneC()); */
        switch (item.getTitle().toString()){
            case "Edit":
                EditCust(CustomerList.get(pos));
                break;
            case "Delete":
                DeleteCust(CustomerList.get(pos));
                break;
            case "Make call":
                CallToCust(CustomerList.get(pos).getPhoneC());
        }
        return super.onContextItemSelected(item);
    }

    private void CallToCust(String phone) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
            startActivity(intent);
        }catch (android.content.ActivityNotFoundException e){
            Toast.makeText(getApplicationContext(), "application failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void DeleteCust(Customer customer) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm delete")
                .setIcon(ContextCompat.getDrawable(Customers.this, R.drawable.cancelable))
                .setMessage("Are you sure you want to delete this customer?")
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        int delCount = db.delete("Customers", "idC = " + customer.getIdC(), null);
                        db.close();
                        ReadFromDB(baseQuery);
                        ViewList();
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton("cancle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.show();
    }

    private void EditCust(Customer customer) {
        final Context context = this;
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_addcustomer);
        dialog.show();

        final TextView counter1 = dialog.findViewById(R.id.counterC_a);
        final TextView counter2 = dialog.findViewById(R.id.counterC_b);
        final EditText etname = dialog.findViewById(R.id.etnameC);
        TextWatcher tw1 = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                counter1.setText(String.valueOf(charSequence.length()) + "/30");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // TODO Auto-generated method stub
            }
        };  etname.addTextChangedListener(tw1);
        final EditText etphone = dialog.findViewById(R.id.etPhoneC);
        final EditText etadress = dialog.findViewById(R.id.etAdressC);
        TextWatcher tw2 = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                counter2.setText(String.valueOf(charSequence.length()) + "/30");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // TODO Auto-generated method stub
            }
        }; etadress.addTextChangedListener(tw2);
        etname.setText(customer.getNameC());
        etphone.setText(customer.getPhoneC());
        etadress.setText(customer.getAdressC());
        final Button saveC = dialog.findViewById(R.id.saveC);
        saveC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etname.getText().toString().isEmpty() ||
                        etphone.getText().toString().isEmpty() ||
                        etadress.getText().toString().isEmpty()){
                    Toast.makeText(context, "fill up all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(etphone.getText().toString().length() != 10 || !etphone.getText().toString().startsWith("05")){
                    Toast.makeText(context, "your phone number is invalied", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Duplication(etphone.getText().toString(), etadress.getText().toString())){
                    Toast.makeText(context, "this customer is already exists", Toast.LENGTH_SHORT).show();
                    return;
                }
                UpdateDB(customer.getIdC(), etname.getText().toString(), etphone.getText().toString(), etadress.getText().toString());
                ReadFromDB(baseQuery);
                ViewList();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void UpdateDB(int id, String name, String phone, String adress) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("nameC", name);
        cv.put("phoneC", phone);
        cv.put("adressC", adress);
        int updCount = db.update("Customers", cv, "idC = ?", new String[]{("" + id)});
        db.close();
    }


    private void SaveToDB(String name, String phone, String adress) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("nameC", name);
        cv.put("phoneC", phone);
        cv.put("adressC", adress);
        long rowID = db.insert("Customers", null, cv);
        db.close();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        pos = i;
        return false;
    }
}