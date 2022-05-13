package com.example.shinjiwoong.yeonsungcafe.Cafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.example.shinjiwoong.yeonsungcafe.Host.HostActivity;
import com.example.shinjiwoong.yeonsungcafe.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class SelectCafe extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private CafeAdapter mRecyclerAdapter;
    private ArrayList<CafeItem> mCafeItems;

    private FirebaseAuth mAuth;

    String name = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_cafe);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_cafe);

        Button notifiBtn = findViewById(R.id.noti);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            name = user.getDisplayName();
        }

        if(name.equals("플래닛")||name.equals("그라찌에")){
            notifiBtn.setVisibility(View.VISIBLE);
        } else{
            notifiBtn.setVisibility(View.GONE);
        }

        notifiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectCafe.this, HostActivity.class);
                startActivity(intent);
            }
        });

        /* initiate adapter */
        mRecyclerAdapter = new CafeAdapter();
        mRecyclerAdapter.setOnItemClickListener(new CafeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position == 0) {
                    Intent intent = new Intent(SelectCafe.this, Init_gemcafe.class);
                    startActivity(intent);
                }
                if (position == 1) {
                    Intent intent = new Intent(SelectCafe.this, Init_planet37.class);
                    startActivity(intent);
                }
            }
        });

        /* initiate recyclerview */
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        /* adapt data */
        mCafeItems = new ArrayList<>();
        mCafeItems.add(new CafeItem(R.drawable.gemcafe, "GEM Cafe", "유리관 1층"));
        mCafeItems.add(new CafeItem(R.drawable.planet37, "Planet37", "대학본관 4층"));
        mRecyclerAdapter.setFriendList(mCafeItems);

    }
}