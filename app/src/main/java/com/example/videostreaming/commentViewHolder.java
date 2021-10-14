package com.example.videostreaming;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class commentViewHolder extends RecyclerView.ViewHolder {

    ImageView cuImage;
    TextView cuMessage,cuName,cuDate;
    public commentViewHolder(@NonNull final View itemView) {
        super(itemView);
        cuImage=itemView.findViewById(R.id.cuImage);
        cuMessage=itemView.findViewById(R.id.cuMessage);
        cuName=itemView.findViewById(R.id.cuName);
        cuDate=itemView.findViewById(R.id.cuDate);


    }


}
