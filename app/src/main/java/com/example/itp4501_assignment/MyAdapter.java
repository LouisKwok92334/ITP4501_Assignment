package com.example.itp4501_assignment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    Context context;
    String[] nData;

    private OnRecyclerViewClickListener listener;
    public MyAdapter(Context context, String[] nData){
        this.context = context;
        this.nData = nData;
    }

    public void setItemClickListener(OnRecyclerViewClickListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);

        if(listener != null){
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClickListener(view);
                }
            });
        }

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.txtItem.setText(nData[position]);
    }

    @Override
    public int getItemCount() {
        return nData.length;
    }
}

