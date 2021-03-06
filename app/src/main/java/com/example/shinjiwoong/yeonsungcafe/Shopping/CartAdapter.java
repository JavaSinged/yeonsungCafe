package com.example.shinjiwoong.yeonsungcafe.Shopping;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shinjiwoong.yeonsungcafe.Cafe.Init_gemcafe;
import com.example.shinjiwoong.yeonsungcafe.Cafe.Init_planet37;
import com.example.shinjiwoong.yeonsungcafe.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

import nl.dionsegijn.steppertouch.OnStepCallback;
import nl.dionsegijn.steppertouch.StepperTouch;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final ArrayList<Cart> arrayList;
    private final Context context;
    ArrayList<Integer> itemPrice = new ArrayList<>();

    //가격 세자리 나누기
    DecimalFormat decimalFormat = new DecimalFormat("###,###");

    public CartAdapter(ArrayList<Cart> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CartAdapter.CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_list, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.CartViewHolder holder, int position) {
        ShoppingCartDB db = new ShoppingCartDB(context);

        for (int i = 0; i < arrayList.size(); i++) {
            itemPrice.add(Integer.parseInt(arrayList.get(position).getPrice()));
        }

        String getId = arrayList.get(position).getId();

        Glide.with(holder.itemView)
                .load(arrayList.get(position).getImage())
                .into(holder.imageView);
        holder.menuName.setText(arrayList.get(position).getName());

        holder.temp.setText("음료 온도: " + arrayList.get(position).getTemp().toUpperCase(Locale.ROOT));

        if (arrayList.get(position).getOption().equals("옵션: ")) {
            holder.option.setVisibility(View.GONE);
        } else {
            holder.option.setText(arrayList.get(position).getOption());
        }


        //포맷 실행
        holder.price.setText(decimalFormat.format(Integer.parseInt(arrayList.get(position).getPrice())) + "원");

        holder.count.setCount(Integer.parseInt(arrayList.get(position).getCount()));

        holder.delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int pos = holder.getAbsoluteAdapterPosition();

                if (!arrayList.isEmpty()) {
                    //삭제를 해도 1개 이상의 arrayList 값이 남아있을 때
                    int count = holder.count.getCount();
                    int total = ShoppingCart.total - itemPrice.get(pos) * count;

                    ShoppingCart.total = total;
                    ShoppingCart.tv_totalPrice.setText(decimalFormat.format(total) + "원");


                    //countFab 값 변경
                    if (ShoppingCart.cafeName.equals("그라찌에")) {
                        Init_gemcafe.counterFab.setCount(getItemCount() - 1);
                    } else {
                        Init_planet37.counterFab.setCount(getItemCount() - 1);
                    }
                } else {
                    //arrayList가 모두 비어졌을 때
                    ShoppingCart.tv_totalPrice.setText("0원");
                    ShoppingCart.total = 0;
                    //countFab 값 변경
                    if (ShoppingCart.cafeName.equals("그라찌에")) {
                        Init_gemcafe.counterFab.setCount(0);
                    } else {
                        Init_planet37.counterFab.setCount(0);
                    }
                }

                //삭제 기능
                removeItem(pos);
                notifyItemRemoved(pos);
                db.deleteData(getId);
            }
        });

        holder.count.addStepCallback(new OnStepCallback() {
            @Override
            public void onStep(int i, boolean b) {


                //itemPrice 위치
                int pos = holder.getAbsoluteAdapterPosition();
                //전체 금액 - 아이템 price - 아이템 개수

                if (b && Integer.parseInt(arrayList.get(pos).getCount()) != i) {
                    ShoppingCart.total += itemPrice.get(pos);
                    arrayList.get(pos).setCount(i+"");
                    //수량 변경

                    //cart.tv_totalPrice 변경
                } else if (!b && Integer.parseInt(arrayList.get(pos).getCount()) != i) {
                    ShoppingCart.total -= itemPrice.get(pos);
                    arrayList.get(pos).setCount(i+"");
                    //수량 변경

                    //cart.tv_totalPrice 변경
                }
                db.updateData(getId, String.valueOf(i));
                ShoppingCart.tv_totalPrice.setText(decimalFormat.format(ShoppingCart.total) + "원");
            }

        });
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    //삭제
    public void removeItem(int position) {
        itemPrice.remove(position);
        arrayList.remove(position);
    }

    //추가
    public void addItem(Cart item) {
        arrayList.add(item);
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView menuName;
        TextView temp;
        TextView price;
        TextView option;
        LinearLayout menuView;
        StepperTouch count;

        ImageButton delBtn;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.imageView);
            this.menuName = itemView.findViewById(R.id.menuName);
            this.temp = itemView.findViewById(R.id.tempTv);
            this.price = itemView.findViewById(R.id.priceCart);
            this.menuView = itemView.findViewById(R.id.meneLayout);
            this.count = itemView.findViewById(R.id.stepperTouch);
            this.option = itemView.findViewById(R.id.optionCart);

            this.delBtn = itemView.findViewById(R.id.delBtn);

            this.count.setMinValue(1);
            this.count.setMaxValue(10);
            this.count.setSideTapEnabled(true);
        }
    }
}
