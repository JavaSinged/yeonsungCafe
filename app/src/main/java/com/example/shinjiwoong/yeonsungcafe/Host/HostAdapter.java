package com.example.shinjiwoong.yeonsungcafe.Host;

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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shinjiwoong.yeonsungcafe.Notify.FCMSend;
import com.example.shinjiwoong.yeonsungcafe.Order.Order;
import com.example.shinjiwoong.yeonsungcafe.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class HostAdapter extends RecyclerView.Adapter<HostAdapter.HostViewHorder> {

    private final ArrayList<Order> arrayList;
    private final Context context;

    private FirebaseAuth mAuth;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference subOrderTable = database.getReference("subOrderTable");
    //    String email = "2021100241";
    String tableItem = "";
    String childItem = "";


    public HostAdapter(ArrayList<Order> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public HostAdapter.HostViewHorder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.host_check_list, parent, false);
        return new HostViewHorder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HostAdapter.HostViewHorder holder, int position) {

        holder.cardView.setVisibility(View.VISIBLE);

        holder.schoolNum.setText(arrayList.get(position).getSchoolNum());

        holder.cafeName.setText(arrayList.get(position).getCafeName());

        holder.orderTime.setText(arrayList.get(position).getOrderTime());

        holder.pickTime.setText(arrayList.get(position).getPickupTime());

        holder.totalPrice.setText(arrayList.get(position).getTotalPrice());

        holder.want.setText(arrayList.get(position).getWant());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAbsoluteAdapterPosition();

                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                tableItem = arrayList.get(pos).getOrderTime();
                String clickItem = arrayList.get(pos).getPickupTime();
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

                subOrderTable.child(holder.schoolNum.getText().toString()).child(tableItem).child(childItem).addListenerForSingleValueEvent(new ValueEventListener() {

                    final ArrayList<String> pre = new ArrayList<>();
                    final ArrayList<Host> userToken = new ArrayList<>();


                    final ArrayList<String> templist = new ArrayList<>();
                    final ArrayList<String> pricelist = new ArrayList<>();
                    final ArrayList<String> countlist = new ArrayList<>();
                    final ArrayList<String> menuNamelist = new ArrayList<>();
                    final ArrayList<String> optionlist = new ArrayList<>();

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
                                })
                                .setPositiveButton("메뉴 준비 완료", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //알림 요청
                                        DatabaseReference token = database.getReference("UserToken");
                                        token.addListenerForSingleValueEvent(new ValueEventListener() {

                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                    Host host = dataSnapshot.getValue(Host.class);
                                                    userToken.add(host);
                                                }

                                                for (int i = 0; i < userToken.size(); i++) {
                                                    if (userToken.get(i).getUserId().equals(arrayList.get(pos).getSchoolNum())) {
                                                        FCMSend.pushNotification(
                                                                context,
                                                                userToken.get(i).getToken(),
                                                                "준비 완료",
                                                                "메뉴가 준비되었습니다."
                                                        );
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Log.e("token", error.toString());
                                                Log.e("token", userToken.size() + "");
                                            }
                                        });
                                    }
                                })
                                .setNegativeButton("주문 취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //취소 버튼
                                        //알림 요청
                                        DatabaseReference token = database.getReference("UserToken");
                                        token.addListenerForSingleValueEvent(new ValueEventListener() {

                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                    Host host = dataSnapshot.getValue(Host.class);
                                                    userToken.add(host);
                                                }

                                                for (int i = 0; i < userToken.size(); i++) {
                                                    if (userToken.get(i).getUserId().equals(arrayList.get(pos).getSchoolNum())) {
                                                        FCMSend.pushNotification(
                                                                context,
                                                                userToken.get(i).getToken(),
                                                                "주문 취소",
                                                                "매장 사정에 의해 주문이 취소되었습니다."
                                                        );
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Log.e("token", error.toString());
                                                Log.e("token", userToken.size() + "");
                                            }
                                        });

                                        DatabaseReference deleteItem = database.getReference("OrderTable");
                                        deleteItem.child(holder.schoolNum.getText().toString()).child(tableItem).child(childItem).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.e("삭제성공", "success");

                                                removeItem(pos);
                                                notifyItemRemoved(pos);
                                                notifyItemRangeChanged(0, getItemCount());
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e("삭제실패", "fail");
                                            }
                                        });
                                    }
                                });
                        builder.setNeutralButton("확인", new DialogInterface.OnClickListener() {
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

    //추가
    public void addItem(Order item) {
        arrayList.add(item);
        notifyItemInserted(arrayList.size() - 1);
    }

    //삭제
    public void removeItem(int position) {
        arrayList.remove(position);
    }


    public class HostViewHorder extends RecyclerView.ViewHolder {
        TextView schoolNum;
        TextView cafeName;
        TextView orderTime;
        TextView pickTime;
        TextView totalPrice;
        TextView want;

        GridLayout orderView;
        CardView cardView;

        public HostViewHorder(@NonNull View itemView) {
            super(itemView);
            this.schoolNum = itemView.findViewById(R.id.schoolNum);
            this.cafeName = itemView.findViewById(R.id.cafeName);
            this.orderTime = itemView.findViewById(R.id.orderTime);
            this.pickTime = itemView.findViewById(R.id.pickupTime);
            this.totalPrice = itemView.findViewById(R.id.totalPrice);
            this.want = itemView.findViewById(R.id.want);
            this.orderView = itemView.findViewById(R.id.gridLayout);
            this.cardView = itemView.findViewById(R.id.cardview_host);
        }
    }
}
