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

    public int pos;
    private Bitmap image;

    public Double markerLat;
    public Double markerLong;

    public List<CardData> mReceivedCardDataList;
    public int size;

//    private ArrayList<String> mReceivedTitle;
//    private ArrayList<String> mReceivedTag;
//    private ArrayList<String> mReceivedThumbnail;
//    private ArrayList<Double> mReceivedLat;
//    private ArrayList<Double> mReceivedLong;

//    public JsonAdapter(ArrayList<String> mTitle, ArrayList<String> mTag, ArrayList<String> mThumbnail,
//                       ArrayList<Double> mLat, ArrayList<Double> mLong, positionSetListener positionSetListener) {
//
//        mReceivedTitle=mTitle;
//        mReceivedTag=mTag;
//        mReceivedThumbnail=mThumbnail;
//        mReceivedLat=mLat;
//        mReceivedLong=mLong;
//        this.listener = positionSetListener;
//
//    }

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

        pos=position;

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    image=getBitmapFromURL(mReceivedCardDataList.get(pos).thumbnail);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        holder.mThumbnail.setImageBitmap(image);

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

    public static Bitmap getBitmapFromURL(String src) {
        try {
            Log.e("src",src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap","returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception",e.getMessage());
            return null;
        }
    }

    public interface positionSetListener{

        void getPosition(Double markerLat, Double markerLong);

    }

}
