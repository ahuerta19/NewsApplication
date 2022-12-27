package com.huertaalexis.newsaggregator;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huertaalexis.newsaggregator.databinding.ActivityMainBinding;

public class ArticleViewHolder extends RecyclerView.ViewHolder {
    private ActivityMainBinding binding;

    TextView artT;
    TextView artD;
    TextView artA;
    ImageView artP;
    TextView artDes;
    TextView artN;



    public ArticleViewHolder(@NonNull View itemView) {
        super(itemView);
        artT = itemView.findViewById(R.id.artTitle);
        artD = itemView.findViewById(R.id.artDate);
        artA = itemView.findViewById(R.id.authTitle);
        artP = itemView.findViewById(R.id.artImg);
        artDes = itemView.findViewById(R.id.artDesc);
        artN = itemView.findViewById(R.id.artNum);
    }
}
