package com.example.shinjiwoong.yeonsungcafe.Menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.shinjiwoong.yeonsungcafe.R;
import com.example.shinjiwoong.yeonsungcafe.Shopping.ShoppingCartDB;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class SelectItem extends AppCompatActivity {

    ImageView imageView;
    TextView name;
    TextView price;
    RadioGroup rg;
    RadioButton hot;
    RadioButton ice;
    TextView optionTV;
    List<String> selectList;

    String cafeName, src, id, id_price, format_price, only, id_ice, select = "";
    int toss_price;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_item);
        //가격 포맷 적용
        DecimalFormat decimalFormat = new DecimalFormat("###,###");

        imageView = findViewById(R.id.imageView);
        name = findViewById(R.id.coffee);
        price = findViewById(R.id.coffeePrice);
        Toolbar toolBar = findViewById(R.id.toolbar);
        FloatingActionButton addCart = findViewById(R.id.addBtn);
        Button button = findViewById(R.id.dialog_button);
        optionTV = findViewById(R.id.optionTV);
        boolean[] boolArr = new boolean[4];
        selectList = new ArrayList<>();

        rg = findViewById(R.id.radioGroup);
        hot = findViewById(R.id.hot);
        ice = findViewById(R.id.ice);

        //메뉴판에서 메뉴 데이터 가져오기
        Intent intent = getIntent();

        cafeName = intent.getExtras().getString("cafeName");
        src = intent.getExtras().getString("src");
        id = intent.getExtras().getString("id");
        id_price = intent.getExtras().getString("id_price");
        id_ice = intent.getExtras().getString("ice");
        only = intent.getExtras().getString("only");


        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle(id);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //초기 세팅
        hot.toggle();
        select = "hot";
        toss_price = Integer.parseInt(id_price);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.hot) {
                    //hot 처리
                    toss_price = Integer.parseInt(id_price) + count * 500;
                    format_price = decimalFormat.format(toss_price) + "원";
                    price.setText(format_price);
                    select = "hot";
                } else if (i == R.id.ice) {
                    //ice 처리
                    toss_price = Integer.parseInt(id_price) + count * 500;
                    format_price = decimalFormat.format(toss_price + Integer.parseInt(id_ice)) + "원";
                    price.setText(format_price);
                    select = "ice";
                }
            }
        });

        addCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (select.isEmpty()) {
                    Snackbar.make(view, "음료의 온도를 선택하여 주세요.", Snackbar.LENGTH_SHORT).show();
                } else if (select.equals("hot") || select.equals("ice")) {
                    //DB객체 생성
                    ShoppingCartDB addDB = new ShoppingCartDB(SelectItem.this);
                    addDB.addCart(cafeName, id, src, String.valueOf(toss_price), select, String.valueOf(1), String.valueOf(optionTV.getText()));

                    SweetAlertDialog dialog = new SweetAlertDialog(SelectItem.this, SweetAlertDialog.SUCCESS_TYPE);
                    dialog.setTitleText(id + "이(가) 장바구니에 추가되었습니다.");
                    dialog.setCancelable(false);
                    dialog.show();
                }
            }
        });

        Glide.with(imageView).load(src).into(imageView);
        name.setText(id);

        //가격 "," 처리
        format_price = decimalFormat.format(Integer.parseInt(id_price)) + "원";
        price.setText(format_price);

        //ice only 상품이나 hot only 상품 처리하기
        if (only != null) {
            if (only.equals("ice")) {
                hot.setVisibility(View.GONE);
                hot.setGravity(Gravity.CENTER);
                ice.toggle();
                select = "ice";
            } else if (only.equals("hot")) {
                ice.setVisibility(View.GONE);
                hot.setGravity(Gravity.CENTER);
                hot.toggle();
                select = "hot";
            }
        } else {
            hot.setVisibility(View.VISIBLE);
            ice.setVisibility(View.VISIBLE);
            ice.setText("ICE +" + id_ice + "원");
        }

        button.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(SelectItem.this);
            builder.setTitle("옵션을 선택하세요. (500원 추가)");

            builder.setMultiChoiceItems(R.array.options, boolArr, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                    String[] items = getResources().getStringArray(R.array.options);
                    if (b) {
                        selectList.add(items[i]);
                        count++;
                    } else if (select.contains(items[i])) {
                        selectList.remove(items[i]);
                    } else {
                        count--;
                        selectList.remove(items[i]);
                    }
                }
            });

            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    optionTV.setText("옵션: ");
                    for (String s : selectList)
                        optionTV.append(s + ", ");

                    optionTV.setText(optionTV.getText().toString().substring(0, optionTV.getText().length()-2));

                    if (select.equals("ice")) {
                        toss_price = Integer.parseInt(id_price) + Integer.parseInt(id_ice) + count * 500;
                    } else {
                        toss_price = Integer.parseInt(id_price) + count * 500;
                    }

                    format_price = decimalFormat.format(toss_price) + "원";
                    price.setText(format_price);
                }
            });

            builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}