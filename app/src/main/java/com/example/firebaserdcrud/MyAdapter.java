package com.example.firebaserdcrud;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private List<DataClass> dataClassList;
    private Context context;

    public MyAdapter(Context context, List<DataClass> dataClassList) {
        this.context = context;
        this.dataClassList = dataClassList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_item_main,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(dataClassList.get(position).getDataImg()).into(holder.recImage);
        holder.recTitle.setText(dataClassList.get(position).getDatatitle());
        holder.recDescription.setText(dataClassList.get(position).getDataDesc());
        holder.recLang.setText(dataClassList.get(position).getDatalang());

        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, DetailsActivity.class);
                intent.putExtra("Image",dataClassList.get(holder.getAdapterPosition()).getDataImg());
                intent.putExtra("Description",dataClassList.get(holder.getAdapterPosition()).getDataDesc());
                intent.putExtra("Title",dataClassList.get(holder.getAdapterPosition()).getDatatitle());
                intent.putExtra("Key",dataClassList.get(holder.getAdapterPosition()).getKey());
                intent.putExtra("Language",dataClassList.get(holder.getAdapterPosition()).getDatalang());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataClassList.size();
    }

    public void searchDataList(ArrayList<DataClass> searchList){
        dataClassList=searchList;
        notifyDataSetChanged();
    }
}

class MyViewHolder extends RecyclerView.ViewHolder{

    ImageView recImage;
    TextView recTitle, recDescription, recLang;
    CardView recCard;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        recImage=itemView.findViewById(R.id.recImageMain);
        recTitle=itemView.findViewById(R.id.recTitleMain);
        recDescription=itemView.findViewById(R.id.recDescMain);
        recLang=itemView.findViewById(R.id.recLangMain);

        recCard=itemView.findViewById(R.id.recCardmain);
    }
}
