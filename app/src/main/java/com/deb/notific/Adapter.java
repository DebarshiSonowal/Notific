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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.skydoves.elasticviews.ElasticImageView;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>  {
    private LayoutInflater mLayoutInflater;
    private List<String> namelist;
    private List<String> area;
    DatabaseReference mDatabaseReference;
    Context mContext;
//    private List<LatLng> mLatLngs;

    public Adapter(Context context ,List<String> namelist,List<String>area) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        this.namelist = namelist;
        this.area = area;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.customview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        String  id = namelist.get(position);
        holder.nam.setText(namelist.get(position));
        holder.itemView.setTag(id);
        holder.Area.setText(area.get(position)+" sq m");
        holder.mDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Marked Location").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(namelist.get(position));
                mDatabaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FancyToast.makeText(mContext,"Successfully deleated",FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,true);
                    }
                });
                notifyItemRemoved(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return namelist.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView nam,Area;
        ElasticImageView mDel;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nam = itemView.findViewById(R.id.textView5);
            mDel = itemView.findViewById(R.id.imageView11);
            Area = itemView.findViewById(R.id.textView32);
        }
    }
}
