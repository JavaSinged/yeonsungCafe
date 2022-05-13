package com.example.shinjiwoong.yeonsungcafe.PickUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.shinjiwoong.yeonsungcafe.Cafe.Init_gemcafe;
import com.example.shinjiwoong.yeonsungcafe.Cafe.Init_planet37;
import com.example.shinjiwoong.yeonsungcafe.Host.Host;
import com.example.shinjiwoong.yeonsungcafe.Order.Order;
import com.example.shinjiwoong.yeonsungcafe.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

//다른데에서 써야함
import com.example.shinjiwoong.yeonsungcafe.Shopping.Cart;
import com.example.shinjiwoong.yeonsungcafe.Shopping.ShoppingCart;
import com.example.shinjiwoong.yeonsungcafe.Shopping.ShoppingCartDB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.shinjiwoong.yeonsungcafe.Shopping.ShoppingCart.context;

public class Pickup extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PickupAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    public ArrayList<Cart> arrayList;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference("OrderTable");
    private DatabaseReference subOrderTable = database.getReference("subOrderTable");

    SharedPreferences auto;
    SharedPreferences.Editor editor;

    static Activity thisActivity;

    ShoppingCartDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        thisActivity = (Pickup) Pickup.this;

        Intent intent = getIntent();

        Toolbar toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("주문하기");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        auto = getSharedPreferences("autoLogin", Activity.MODE_PRIVATE);
        editor = auto.edit();

        getToken();

        TextView topText = findViewById(R.id.cafeName);
        TextView timeText = findViewById(R.id.pickupTime);
        TextView priceText = findViewById(R.id.total);
        EditText wantText = findViewById(R.id.wantEditText);
        Button pickupBtn = findViewById(R.id.orderBtn);

        String totalPrice = intent.getExtras().getString("total");

        timeText.setText(pickup());

        topText.setText("픽업 요청하기 - " + intent.getExtras().getString("cafeName"));
        priceText.setText(totalPrice);

        pickupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    String email = user.getEmail();

                    addOrder(email.substring(0, email.length() - 7), totalPrice, String.valueOf(wantText.getText()),
                            todayStorege(), intent.getExtras().getString("cafeName"), pickup(), today());

                    SweetAlertDialog dialog = new SweetAlertDialog(Pickup.this, SweetAlertDialog.PROGRESS_TYPE);
                    dialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    dialog.setTitleText("사장님께<br>주문을 전달하고 있습니다.");
                    dialog.setCancelable(false);
                    dialog.show();

                    String DB_PATH = "/data/data/" + getPackageName();
                    String DB_NAME = "shoppingcart.db";
                    String DB_FULLPATH = DB_PATH + "/databases/" + DB_NAME;

                    File dbFile = new File(DB_FULLPATH);
                    if (dbFile.delete()) {
                        if(intent.getExtras().getString("cafeName").equals("그라찌에")){
                            ((Init_gemcafe)Init_gemcafe.context).counterFab.setCount(0);
                        }else{
                            ((Init_planet37)Init_planet37.context).counterFab.setCount(0);
                        }

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                ShoppingCart sp = (ShoppingCart) ShoppingCart.preActivity;
                                sp.finish();
                                Pickup pu = (Pickup) Pickup.thisActivity;
                                pu.finish();
                            }
                        }, 5000);
                    } else {
//                        System.out.println(" 삭제 실패");
                    }
                }
            }
        });

        //장바구니 구현
        arrayList = new ArrayList<>();
        recyclerView = findViewById(R.id.pickupList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PickupAdapter(arrayList, Pickup.this);

        //db 생성
        db = new ShoppingCartDB(Pickup.this);
        storeDataInArray();

        recyclerView.setAdapter(adapter);
    }

    private String pickup() {
        SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm");
        long mNow = System.currentTimeMillis() + 600000; //10분 뒤
        Date date = new Date(mNow);
        return mFormat.format(date);
    }

    private String today() {
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy년MM월dd일");
        long mNow = System.currentTimeMillis();
        Date date = new Date(mNow);
        return mFormat.format(date);
    }

    private String todayStorege() {
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyyMMdd/HHmm");
        long mNow = System.currentTimeMillis();
        Date date = new Date(mNow);
        return mFormat.format(date);
    }

    //데이터 가져오기
    public void storeDataInArray() {
        Cursor cursor = db.readAllData();
        int count = 0;

        if (cursor.getCount() == 0) {

        } else {
            while (cursor.moveToNext()) {
                Cart cart = new Cart(cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6));

                //데이터 등록
                adapter.addItem(cart);
                count += 1;

                //적용
                adapter.notifyItemInserted(count);
            }
        }
    }

    public void addOrder(String user, String totalPrice, String want, String todayStorege, String cafeName, String pickupTime, String today) {

        String token = auto.getString("token", "");

        List<String> name = new ArrayList<>();
        List<String> temp = new ArrayList<>();
        List<String> option = new ArrayList<>();
        List<String> price = new ArrayList<>();
        List<String> count = new ArrayList<>();

        Order order = new Order();
        order.setTotalPrice(totalPrice);
        order.setWant(want);
        order.setCafeName(cafeName);
        order.setPickupTime(pickupTime);
        order.setOrderTime(today);
        order.setSchoolNum(user);

        databaseReference.child(user).child(todayStorege).setValue(order);

        for (int i = 0; i < arrayList.size(); i++) {

            name.add(arrayList.get(i).getName());
            temp.add(arrayList.get(i).getTemp());
            option.add(arrayList.get(i).getOption());
            price.add(arrayList.get(i).getPrice());
            count.add(arrayList.get(i).getCount());

            subOrderTable.child(user).child(todayStorege).child("name").setValue(name);
            subOrderTable.child(user).child(todayStorege).child("temp").setValue(temp);
            subOrderTable.child(user).child(todayStorege).child("option").setValue(option);
            subOrderTable.child(user).child(todayStorege).child("price").setValue(price);
            subOrderTable.child(user).child(todayStorege).child("count").setValue(count);
        }

        Host host = new Host();
        host.setUserId(user);
        host.setToken(token);
        databaseReference.getDatabase().getReference("UserToken").child(user).setValue(host);
    }

    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {

            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(!task.isSuccessful()){
                    Log.d("tag", "failed");
                }

                String token = task.getResult();

                editor.putString("token", token);
                editor.apply();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}