package com.deb.notific;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>  {
    private LayoutInflater mLayoutInflater;
    private List<String> namelist;
//    private List<LatLng> mLatLngs;

    public Adapter(Context context ,List<String> namelist) {
        this.mLayoutInflater = LayoutInflater.from(context);
        this.namelist = namelist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.customview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {
        holder.mview.setText(namelist.get(position));
        holder.mDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.mview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return namelist.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView nam;
        Button mDel,mview;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nam = itemView.findViewById(R.id.textView5);
            mDel = itemView.findViewById(R.id.button3);
            mview =itemView.findViewById(R.id.button4);
        }
    }
}
