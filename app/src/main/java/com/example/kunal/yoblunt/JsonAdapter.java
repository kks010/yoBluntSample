package com.example.kunal.yoblunt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kunal on 26-07-2017.
 */

class JsonAdapter extends android.support.v7.widget.RecyclerView.Adapter<JsonAdapter.ViewHolder> {

    public positionSetListener listener;

    public Double markerLat;
    public Double markerLong;

    public List<CardData> mReceivedCardDataList;
    public int size;

    public JsonAdapter(List<CardData> mCardDataList, int mSize, positionSetListener positionSetListener) {
        mReceivedCardDataList=mCardDataList;
        this.listener = positionSetListener;
        this.size=mSize;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        markerLat=mReceivedCardDataList.get(position).latitude;
        markerLong=mReceivedCardDataList.get(position).longitude;

        holder.mTitle.setText(mReceivedCardDataList.get(position).title);
        holder.mTag.setText(mReceivedCardDataList.get(position).tag);

        Glide.with(holder.mCardView.getContext())
                .load(mReceivedCardDataList.get(position).thumbnail)
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.mThumbnail);

        if(listener!=null) {
            listener.getPosition(markerLat, markerLong);
        }

        Log.d(" lat ",markerLat.toString());
        Log.d(" long ",markerLong.toString());

        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.getPosition(markerLat, markerLong);
            }
        });

    }

    @Override
    public int getItemCount() {
        return size;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mTitle;
        public TextView mTag;
        public ImageView mThumbnail;
        public CardView mCardView;

        public ViewHolder(View v) {
            super(v);

            //binding
            mTitle=(TextView)v.findViewById(R.id.title_horizontal_bar);
            mTag=(TextView)v.findViewById(R.id.tag_horizontal_bar);
            mThumbnail=(ImageView)v.findViewById(R.id.thumbnail_horizontal_bar);
            mCardView=(CardView) v.findViewById(R.id.card_view);

        }
    }

    public interface positionSetListener{

        void getPosition(Double markerLat, Double markerLong);

    }

}
