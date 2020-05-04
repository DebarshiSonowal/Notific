package com.deb.notific;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Adapter1 extends RecyclerView.Adapter<Adapter1.ViewHolder> {
    private List<String> namelist;
    private Context context;
    private List<String> pnumber;
    private LayoutInflater mLayoutInflater;
    private List<String> mTimeList;

    public Adapter1(Context context, List<String> namelist, List<String> mTimeList, List<String> pnumber) {
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
        this.namelist = namelist;
        this.mTimeList = mTimeList;
        this.pnumber = pnumber;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.customview2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
            holder.name.setText(namelist.get(position));
            holder.time.setText(mTimeList.get(position));
            holder.callbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri number = Uri.parse(pnumber.get(position));
                    Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                    context.startActivity(callIntent);
                }
            });
    }

    @Override
    public int getItemCount() {
        return namelist.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name,time;
        Button callbtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
           name = itemView.findViewById(R.id.contactname);
            time = itemView.findViewById(R.id.time);
            callbtn = itemView.findViewById(R.id.callbtn);
        }
    }
}