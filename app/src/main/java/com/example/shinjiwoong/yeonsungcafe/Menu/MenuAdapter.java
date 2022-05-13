package com.example.shinjiwoong.yeonsungcafe.Menu;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shinjiwoong.yeonsungcafe.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.CustomViewHolder>{

    private ArrayList<CafeMenu> arrayList;
    private final Context context;


    public MenuAdapter(ArrayList<CafeMenu> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    public void filterList(ArrayList<CafeMenu> list){
        arrayList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MenuAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cafe_menu, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MenuAdapter.CustomViewHolder holder, int position) {

        Glide.with(holder.itemView)
                .load(arrayList.get(position).getImage())
                .into(holder.imageView);

        holder.menuName.setText(arrayList.get(position).getName());

        //가격 세자리 나누기
        DecimalFormat decimalFormat = new DecimalFormat("###,###");
        holder.price.setText(decimalFormat.format(arrayList.get(position).getPrice()) + "원");

        holder.menuView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int mPosition = holder.getAdapterPosition();
                Context context = view.getContext();

                Intent select = new Intent(context, SelectItem.class);
                select.putExtra("cafeName", context.toString());
                select.putExtra("src", arrayList.get(mPosition).getImage());
                select.putExtra("id", arrayList.get(mPosition).getName());
                select.putExtra("id_price", String.valueOf(arrayList.get(mPosition).getPrice()));
                select.putExtra("only", arrayList.get(mPosition).getOnly());
                select.putExtra("ice", String.valueOf(arrayList.get(mPosition).getIce()));

                (context).startActivity(select);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView menuName;
        TextView price;
        LinearLayout menuView;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.imageView);
            this.menuName = itemView.findViewById(R.id.menuName);
            this.price = itemView.findViewById(R.id.price);
            this.menuView = itemView.findViewById(R.id.menuView);
        }
    }
}
