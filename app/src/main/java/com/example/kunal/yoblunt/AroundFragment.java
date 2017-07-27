package com.example.kunal.yoblunt;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Kunal on 26-07-2017.
 */

class AroundFragment extends Fragment implements OnMapReadyCallback,JsonAdapter.positionSetListener{

    View inflatedView = null;

    public JsonAdapter.positionSetListener listener;

    public double latitude = 28.4489123;
    public double longitude = 77.3677894;

    MapView mMapView;
    private GoogleMap googleMap;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;

    private ArrayList<String> mTitle;
    private ArrayList<String> mTag;
    private ArrayList<String> mThumbnail;

    private ArrayList<Double> mLat;
    private ArrayList<Double> mLong;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflate and return the layout
        inflatedView = inflater.inflate(R.layout.fragment_around, container, false);

        listener=this;

        //binding map
        mMapView = (MapView) inflatedView.findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);

        //binding recycler view and adapter
        mRecyclerView = (RecyclerView)inflatedView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(),LinearLayoutManager.HORIZONTAL,false);
        mRecyclerView.setLayoutManager(mLayoutManager);


        //load json
        loadJSON(inflatedView);


        mAdapter = new JsonAdapter(mTitle,mTag,mThumbnail,mLat,mLong,listener);
        mRecyclerView.setAdapter(mAdapter);


        // needed to get the map to display immediately
        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);

        // Perform any camera updates here
        return inflatedView;
    }


    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        LatLng position = new LatLng(latitude, longitude);

        googleMap.addMarker(new MarkerOptions().position(position).title("Marker at position"));

        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.setTrafficEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }


    public void loadJSON(View view) {


        InputStream is = getResources().openRawResource(R.raw.data);

        Scanner scanner = new Scanner(is);

        StringBuilder builder = new StringBuilder();

        while(scanner.hasNextLine()){
            builder.append(scanner.nextLine());
        }
        
        ParseJson(builder.toString());
    }

    private void ParseJson(String s) {

        StringBuilder builder= new StringBuilder();

        try {
            JSONArray root = new JSONArray(s);

            mTitle = new ArrayList<>();
            mTag = new ArrayList<>();
            mThumbnail = new ArrayList<>();

            mLat = new ArrayList<>();
            mLong = new ArrayList<>();

            for(int i=0;i<s.length();i++){

                JSONObject thumbnail_item = root.getJSONObject(i);
                mTitle.add(thumbnail_item.getString("title"));
                mTag.add(thumbnail_item.getString("tag"));
                mThumbnail.add(thumbnail_item.getString("thumbnail"));

                JSONObject location = thumbnail_item.getJSONObject("location");
                mLat.add(location.getDouble("lan"));
                mLong.add(location.getDouble("lng"));

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getPosition(Double markerLat, Double markerLong) {


        if(markerLat==null||markerLong==null) {
            Toast.makeText(getActivity(), "check connection", Toast.LENGTH_SHORT);
        }else {
            latitude = markerLat;
            longitude = markerLong;

            LatLng position = new LatLng(latitude, longitude);

            MarkerOptions marker = new MarkerOptions().position(position).title("Marker at new position");

            if(marker!=null) {

                googleMap.addMarker(marker); //issue while adding marker again

                CameraPosition cameraPosition = new CameraPosition.Builder().target(position).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }

        }

    }


}
