package com.example.shinjiwoong.yeonsungcafe.Order;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shinjiwoong.yeonsungcafe.R;
import com.example.shinjiwoong.yeonsungcafe.Shopping.Cart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private ArrayList<Order> arrayList;
    private final Context context;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference subOrderTable = database.getReference("subOrderTable");

    String email;

    public OrderAdapter(ArrayList<Order> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderAdapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_list, parent, false);
        OrderViewHolder holder = new OrderViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.OrderViewHolder holder, int position) {

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            email = user.getEmail();
            email = email.substring(0, email.length() - 7);
        }

        holder.cafeName.setText(arrayList.get(position).getCafeName());

        holder.orderTime.setText(arrayList.get(position).getOrderTime());

        holder.pickTime.setText(arrayList.get(position).getPickupTime());

        holder.totalPrice.setText(arrayList.get(position).getTotalPrice());

        holder.want.setText(arrayList.get(position).getWant());


        holder.orderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAbsoluteAdapterPosition();

                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                String tableItem = arrayList.get(pos).getOrderTime();
                String clickItem = arrayList.get(pos).getPickupTime();
                String childItem = "";
                Calendar cal = Calendar.getInstance();

                try {
                    Date date = dateFormat.parse(clickItem);
                    cal.setTime(date);
                    cal.add(Calendar.MINUTE, -10);
                    childItem = dateFormat.format(cal.getTime());
                    childItem = childItem.replace(":", "");
                    tableItem = tableItem.replace("년", "");
                    tableItem = tableItem.replace("월", "");
                    tableItem = tableItem.replace("일", "");
                } catch (ParseException e) {
//                    e.printStackTrace();
                }

                subOrderTable.child(email).child(tableItem).child(childItem).addListenerForSingleValueEvent(new ValueEventListener() {

                    ArrayList<String> pre = new ArrayList<>();

                    ArrayList<String> templist = new ArrayList<>();
                    ArrayList<String> pricelist = new ArrayList<>();
                    ArrayList<String> countlist = new ArrayList<>();
                    ArrayList<String> menuNamelist = new ArrayList<>();
                    ArrayList<String> optionlist = new ArrayList<>();

                    int search = 0;

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            pre.add(snapshot.getValue().toString());
                            String str = pre.get(search);
                            str = str.replace("[", "");
                            str = str.replace("]", "");
                            pre.set(search, str);
                            search++;
                        }


                        String[] count = pre.get(0).split(",");
                        String[] menuName = pre.get(1).split(",");
                        String[] option = pre.get(2).split(",");
                        String[] price = pre.get(3).split(",");
                        String[] temp = pre.get(4).split(",");

                        for (int k = 0; k < temp.length; k++) {
                            templist.add(temp[k]);
                            pricelist.add(price[k] + "원");
                            countlist.add(count[k] + "잔");
                            menuNamelist.add(menuName[k]);
                            optionlist.add(option[k]);
                        }

                        final CharSequence[] m = menuNamelist.toArray(new String[menuNamelist.size()]);
                        final CharSequence[] p = pricelist.toArray(new String[pricelist.size()]);
                        final CharSequence[] t = templist.toArray(new String[templist.size()]);
                        final CharSequence[] o = optionlist.toArray(new String[optionlist.size()]);
                        final CharSequence[] c = countlist.toArray(new String[countlist.size()]);

                        final CharSequence[] all = new CharSequence[menuNamelist.size()];
                        for (int i = 0; i < menuNamelist.size(); i++) {
                            all[i] = t[i] + "\t" + m[i] + "\t" + c[i] + "\t" + p[i] + "\n" + o[i];
                        }


                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("상세내역");
                        builder.setItems(all, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });

                        builder.show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("orderView", String.valueOf(error.toException()));
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView cafeName;
        TextView orderTime;
        TextView pickTime;
        TextView totalPrice;
        TextView want;

        GridLayout orderView;


        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            this.cafeName = itemView.findViewById(R.id.cafeName);
            this.orderTime = itemView.findViewById(R.id.orderTime);
            this.pickTime = itemView.findViewById(R.id.pickupTime);
            this.totalPrice = itemView.findViewById(R.id.totalPrice);
            this.want = itemView.findViewById(R.id.want);
            this.orderView = itemView.findViewById(R.id.gridLayout);
        }
    }
}
