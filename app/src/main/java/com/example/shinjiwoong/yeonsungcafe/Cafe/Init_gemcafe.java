package com.example.shinjiwoong.yeonsungcafe.Cafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.andremion.counterfab.CounterFab;
import com.bumptech.glide.Glide;
import com.example.shinjiwoong.yeonsungcafe.Menu.CafeMenu;
import com.example.shinjiwoong.yeonsungcafe.Menu.MenuAdapter;
import com.example.shinjiwoong.yeonsungcafe.Order.UserOrder;
import com.example.shinjiwoong.yeonsungcafe.R;
import com.example.shinjiwoong.yeonsungcafe.Setting.SettingsActivity;
import com.example.shinjiwoong.yeonsungcafe.Shopping.ShoppingCart;
import com.example.shinjiwoong.yeonsungcafe.Shopping.ShoppingCartDB;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;

public class Init_gemcafe extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MenuAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<CafeMenu> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private DrawerLayout mDrawerLayout;
    public static Context context;
    public static CounterFab counterFab;
    public static Toolbar toolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_gemcafe);

        context = this;

        toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("그라찌에");
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);

        mDrawerLayout = findViewById(R.id.drawerLayout);

        NavigationView navigationView = findViewById(R.id.nav_view);

        View header = navigationView.getHeaderView(0);

        ImageView logo = header.findViewById(R.id.imageView);
        TextView name = header.findViewById(R.id.nav_name);
        TextView num = header.findViewById(R.id.nav_num);

        navigationView.setCheckedItem(R.id.nav_home);

        navigationView.setNavigationItemSelectedListener(item -> {
            item.setCheckable(true);
            mDrawerLayout.closeDrawers();

            int id = item.getItemId();

            switch (id) {
                case R.id.nav_home:
                    break;
                case R.id.nav_order:
                    startActivity(new Intent(getApplicationContext(), UserOrder.class));
                    break;
                case R.id.nav_setting:
                    startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                    break;
            }
            return true;
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            Glide.with(this).load(R.drawable.logo).into(logo);
            name.setText(user.getDisplayName());
            num.setText(user.getEmail().substring(0, user.getEmail().length() - 7));
        }

        counterFab = findViewById(R.id.counter_fab);
        setCounterFab();

        counterFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Init_gemcafe.this, ShoppingCart.class);
                intent.putExtra("cafeName", getSupportActionBar().getTitle());
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.cafeMenu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();

        database = FirebaseDatabase.getInstance(); //파이어베이스 데이터베이스 연동
        databaseReference = database.getReference("CafeMenu");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //파이어베이스 데이터베이스의 데이터를 받아오는 곳
                arrayList.clear(); //초기화
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //데이터 list 추출
                    CafeMenu cafeMenu = snapshot.getValue(CafeMenu.class);
                    arrayList.add(cafeMenu); //가져온 데이터 추가하기
                }
                adapter.notifyItemInserted(arrayList.size() - 1); //리스트 저장 및 새로고침
                adapter.notifyItemChanged(arrayList.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //디비를 가져오다가 에러 발생 시 에러문 출력
                Log.e("Init_gemcafe", String.valueOf(error.toException()));
            }
        });

        adapter = new MenuAdapter(arrayList, this);
        recyclerView.setAdapter(adapter); //리사이클러뷰에 어댑터 연결

        reSetDB();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { // 왼쪽 상단 버튼 눌렀을 때
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.init_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //menuAdapter.getFilter().filter(newText);
                filter(newText);
                return false;
            }
        });
        return true;
    }

    private void filter(String text) {
        // creating a new array list to filter our data.
        ArrayList<CafeMenu> list = new ArrayList<>();

        // running a for loop to compare elements.
        for (CafeMenu item : arrayList) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                list.add(item);
            }
        }
        if (list.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            //Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            adapter.filterList(list);
        }
    }

    //countFab 초기화
    void setCounterFab() {
        ShoppingCartDB db;
        db = new ShoppingCartDB(Init_gemcafe.this);
        SQLiteDatabase sql = db.getReadableDatabase();
        db.onUpgrade(sql, 1, 2); //퍼지면 써야함
        Cursor cursor = db.readAllData();
        if (cursor.getCount() == 0) {

        } else {
            while (cursor.moveToNext()) {
                counterFab.increase();
            }
        }
    }

    void reSetDB() {
        String DB_PATH = "/data/data/" + getPackageName();
        String DB_NAME = "shoppingcart.db";
        String DB_FULLPATH = DB_PATH + "/databases/" + DB_NAME;

        File dbFile = new File(DB_FULLPATH);
        if (dbFile.delete()) {
            counterFab.setCount(0);
        }
    }
}