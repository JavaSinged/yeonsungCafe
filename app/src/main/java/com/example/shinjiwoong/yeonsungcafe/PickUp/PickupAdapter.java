package com.example.shinjiwoong.yeonsungcafe.PickUp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shinjiwoong.yeonsungcafe.R;
import com.example.shinjiwoong.yeonsungcafe.Shopping.Cart;
import com.example.shinjiwoong.yeonsungcafe.Shopping.ShoppingCartDB;

import java.util.ArrayList;
import java.util.Locale;

public class PickupAdapter extends RecyclerView.Adapter<PickupAdapter.PickupViewHolder> {

    private final ArrayList<Cart> arrayList;
    private final Context context;

    public PickupAdapter(ArrayList<Cart> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public PickupAdapter.PickupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pickup_list, parent, false);
        return new PickupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PickupAdapter.PickupViewHolder holder, int position) {

        Glide.with(holder.itemView)
                .load(arrayList.get(position).getImage())
                .into(holder.imageView);
        holder.name.setText(arrayList.get(position).getName());

        holder.temp.setText("음료 온도: "+arrayList.get(position).getTemp().toUpperCase(Locale.ROOT));

        if(arrayList.get(position).getOption().equals("옵션: ")){
            holder.option.setVisibility(View.GONE);
        }else{
            holder.option.setText(arrayList.get(position).getOption());
        }

        holder.count.setText(arrayList.get(position).getCount()+"잔");
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    //삭제
    public void removeItem(int position){
        arrayList.remove(position);
    }

    //추가
    public void addItem(Cart item){
        arrayList.add(item);
    }

    public class PickupViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView name;
        TextView temp;
        TextView option;
        TextView count;

        public PickupViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.imageView);
            this.name = itemView.findViewById(R.id.menuName);
            this.temp = itemView.findViewById(R.id.tempTv);
            this.option = itemView.findViewById(R.id.optionPick);
            this.count = itemView.findViewById(R.id.count);
        }
    }
}
