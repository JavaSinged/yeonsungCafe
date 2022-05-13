package com.example.shinjiwoong.yeonsungcafe.Host;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.shinjiwoong.yeonsungcafe.Notify.FCMSend;
import com.example.shinjiwoong.yeonsungcafe.Order.Order;
import com.example.shinjiwoong.yeonsungcafe.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class HostActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private DatabaseReference orderTable;

    private RecyclerView recyclerView;
    private HostAdapter adapter;
    private LinearLayoutManager layoutManager;
    private ArrayList<Order> arrayList;

    public ArrayList<Host> userID;

    private FirebaseAuth mAuth;
    String email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        userID = new ArrayList<>();
        recyclerView = findViewById(R.id.host_recycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();

        database = FirebaseDatabase.getInstance(); //파이어베이스 데이터베이스 연동
        databaseReference = database.getReference("UserToken");
        orderTable = database.getReference("OrderTable");

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            email = user.getDisplayName();
        }

        setHost();

        adapter = new HostAdapter(arrayList, this);
        recyclerView.setAdapter(adapter); //리사이클러뷰에 어댑터 연결
    }

    private void setHost(){
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        DateFormat df = new SimpleDateFormat("yyyyMMdd");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            int count = 0;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Host host = dataSnapshot.getValue(Host.class);

                    userID.add(host);
                    Log.e("token", userID.get(count).getUserId() + "");
                    orderTable.child(userID.get(count).getUserId()).child(df.format(cal.getTime())).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            Order order = snapshot.getValue(Order.class);
                            Log.e("token", snapshot.getValue().toString());


                            try {
                                if(order.getCafeName().equals(email)||(order.getCafeName().equals("플래닛37"))&&email.equals("플래닛")){
                                    arrayList.add(order);
                                    recyclerView.smoothScrollToPosition(arrayList.size()-1);
                                    layoutManager.scrollToPositionWithOffset(0, 0);
                                }
                            }catch (Exception e){
                                System.out.println(e.toString());
                            }
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            Order order = snapshot.getValue(Order.class);
                            try {
                                if(order.getCafeName().equals(email)&&!order.getPickupTime().equals(arrayList.get(arrayList.size()-1).getPickupTime())){
                                    arrayList.add(order);
                                    recyclerView.smoothScrollToPosition(arrayList.size()-1);
                                    layoutManager.scrollToPositionWithOffset(0, 0);
                                }
                            }catch (Exception e){
                                Log.e("error", e.toString());
                            }
                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                            recyclerView.smoothScrollToPosition(arrayList.size()-1);
                            layoutManager.scrollToPositionWithOffset(0, 0);
                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            recyclerView.smoothScrollToPosition(arrayList.size()-1);
                            layoutManager.scrollToPositionWithOffset(0, 0);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    count++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        recyclerView.smoothScrollToPosition(0);
        layoutManager.scrollToPositionWithOffset(0, 0);
    }
}