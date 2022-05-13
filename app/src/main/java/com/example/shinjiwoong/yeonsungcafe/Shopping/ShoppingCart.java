package com.example.shinjiwoong.yeonsungcafe.Shopping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.shinjiwoong.yeonsungcafe.Cafe.Init_gemcafe;
import com.example.shinjiwoong.yeonsungcafe.Cafe.Init_planet37;
import com.example.shinjiwoong.yeonsungcafe.PickUp.Pickup;
import com.example.shinjiwoong.yeonsungcafe.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ShoppingCart extends AppCompatActivity {

    public static Context context;
    TextView tv_name;
    TextView tv_totalPrice;
    Button pickupBtn;
    String cafeName;
    public int total = 0;

    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    public ArrayList<Cart> arrayList;

    ShoppingCartDB db;

    public static Activity preActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        context = this;

        preActivity = ShoppingCart.this;

        Toolbar toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("장바구니");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_name = findViewById(R.id.cafeName);
        tv_totalPrice = findViewById(R.id.totalPrice);
        pickupBtn = findViewById(R.id.orderBtn);

        Intent intent = getIntent();
        cafeName = intent.getExtras().getString("cafeName");
        tv_name.setText(cafeName+"-"+toolBar.getTitle());

        //장바구니 구현
        arrayList = new ArrayList<>();
        recyclerView = findViewById(R.id.shoppingCart_rv);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CartAdapter(arrayList,ShoppingCart.this);

        //db 생성
        db = new ShoppingCartDB(ShoppingCart.this);
        storeDataInArray();

        recyclerView.setAdapter(adapter);

        //픽업 주문하기 버튼 리스너
        pickupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!arrayList.isEmpty()){
                    Intent start = new Intent(ShoppingCart.this, Pickup.class);
                    start.putExtra("cafeName", cafeName);
                    start.putExtra("total", tv_totalPrice.getText());
                    startActivity(start);
                }
            }
        });
    }

    //데이터 가져오기
    public void storeDataInArray(){
        Cursor cursor = db.readAllData();
        DecimalFormat decimalFormat = new DecimalFormat("###,###");
        int count=0;

        if(cursor.getCount()==0){

        }else{
            while (cursor.moveToNext()){
                Cart cart = new Cart(cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6));

                //데이터 등록
                adapter.itemPrice.add(Integer.parseInt(cursor.getString(3)));
                adapter.addItem(cart);
                count+=1;

                //적용
                adapter.notifyItemInserted(count);

                //장바구니에 total 초기화
                total+= Integer.parseInt(cart.getPrice())*Integer.parseInt(cart.getCount());
                tv_totalPrice.setText(decimalFormat.format(total) +"원");

                //fab 초기화
                if(cafeName.equals("그라찌에")) {
                    ((Init_gemcafe) Init_gemcafe.context).counterFab.setCount(count);
                }else{
                    ((Init_planet37)Init_planet37.context).counterFab.setCount(count);
                }
            }
        }
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