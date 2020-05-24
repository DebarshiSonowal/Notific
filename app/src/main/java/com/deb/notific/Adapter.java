package com.deb.notific;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>  {
    private LayoutInflater mLayoutInflater;
    private List<String> namelist;
    DatabaseReference mDatabaseReference;
    Context mContext;
//    private List<LatLng> mLatLngs;

    public Adapter(Context context ,List<String> namelist) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        this.namelist = namelist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.customview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.nam.setText(namelist.get(position));
        holder.mDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Marked Location").child(namelist.get(position));
                mDatabaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FancyToast.makeText(mContext,"Successfully deleated",FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,true);
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return namelist.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView nam;
        ImageButton mDel;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nam = itemView.findViewById(R.id.textView5);
            mDel = itemView.findViewById(R.id.button3);
        }
    }
}
