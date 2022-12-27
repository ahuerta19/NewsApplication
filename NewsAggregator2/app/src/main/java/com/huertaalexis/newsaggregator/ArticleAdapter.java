package com.huertaalexis.newsaggregator;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleViewHolder> {

    private final MainActivity mainActivity;
    private final ArrayList<Article> articleList;


    public ArticleAdapter(MainActivity mainActivity, ArrayList<Article> articleList) {
        this.mainActivity = mainActivity;
        this.articleList = articleList;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ArticleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.article_entry,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
    Article article = articleList.get(position);

        if(article.getTitle()==null || article.getTitle()=="null"){
            holder.artT.setVisibility(View.INVISIBLE);
        }else{
            holder.artT.setText(article.getTitle());
        }
        if(article.getDate()==null || article.getDate()=="null"){
            holder.artD.setVisibility(View.INVISIBLE);
        }else{
            holder.artD.setText(article.getDate());
        }
    //holder.artD.setText(article.getDate());
    if(article.getAuthor()==null || article.getAuthor()=="null"){
        holder.artA.setVisibility(View.INVISIBLE);
    }else{
        holder.artA.setText(article.getAuthor());
    }

    if(article.getPhoto()!=null){
        Glide.with(mainActivity)
                .load(article.getPhoto())
                .placeholder(R.drawable.loading)
                .error(R.drawable.brokenimage)
                .into(holder.artP);
    }else{
        Picasso.get().load(R.drawable.noimage).into(holder.artP);
    }
        if(article.getDesc()==null || article.getDesc()=="null"){
            holder.artDes.setVisibility(View.INVISIBLE);
        }else{
            holder.artDes.setText(article.getDesc());
        }

    //holder.artDes.setText(article.getDesc() + "\n");
    holder.artN.setText(position+1 + " of " + article.getNum());

    ImageView artImg = holder.artP;
    TextView descBox = holder.artDes;
    TextView titleBox = holder.artT;
    if(article.getLink()!=null){
       descBox.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                web(article.getLink());
            }
            private void web(String s) {
                Uri uri = Uri.parse(s);
                mainActivity.startActivity(new Intent(Intent.ACTION_VIEW,uri));
            }
        });
        titleBox.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                web(article.getLink());
            }
            private void web(String s) {
                Uri uri = Uri.parse(s);
                mainActivity.startActivity(new Intent(Intent.ACTION_VIEW,uri));
            }
        });

        artImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                web(article.getLink());
            }
            private void web(String s) {
                Uri uri = Uri.parse(s);
                mainActivity.startActivity(new Intent(Intent.ACTION_VIEW,uri));
            }
        });
    }
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

}
