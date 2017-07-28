package com.example.kunal.yoblunt;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Kunal on 26-07-2017.
 */

class AroundFragment extends Fragment implements OnMapReadyCallback, JsonAdapter.positionSetListener, LocationListener {

    View inflatedView = null;

    public JsonAdapter.positionSetListener listener;

    public double latitude = 28.4489123;
    public double longitude = 77.3677894;

    public double userLatitude;
    public double userLongitude;

    public double userReceivedLat;
    public double userReceivedLong;

    MapView mMapView;
    private GoogleMap googleMap;
    Marker marker;
    private float zoom = 10f;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;

    private ArrayList<String> mTitle;
    private ArrayList<String> mTag;
    private ArrayList<String> mThumbnail;

    private ArrayList<Double> mLat;
    private ArrayList<Double> mLong;
    private ArrayList<Double> mDistance;

    public List<CardData> mCardDataList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflate and return the layout
        inflatedView = inflater.inflate(R.layout.fragment_around, container, false);

        listener = this;

        //binding map
        mMapView = (MapView) inflatedView.findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);

        //binding recycler view and adapter
        mRecyclerView = (RecyclerView) inflatedView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //load json
        loadJSON(inflatedView);

        mAdapter = new JsonAdapter(mCardDataList,mCardDataList.size(),listener);
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


        marker = googleMap.addMarker(new MarkerOptions().position(position).title("Marker at new position"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, zoom));

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

        while (scanner.hasNextLine()) {
            builder.append(scanner.nextLine());
        }

        ParseJson(builder.toString());
    }

    private void ParseJson(String s) {

        userLatitude = userReceivedLat;
        userLongitude = userReceivedLong;

        try {
            JSONArray root = new JSONArray(s);

            mTitle = new ArrayList<>();
            mTag = new ArrayList<>();
            mThumbnail = new ArrayList<>();

            mLat = new ArrayList<>();
            mLong = new ArrayList<>();
            mDistance = new ArrayList<>();

            for (int i = 0; i < root.length(); i++) {

                JSONObject thumbnail_item = root.getJSONObject(i);
                mTitle.add(thumbnail_item.getString("title"));
                mTag.add(thumbnail_item.getString("tag"));
                mThumbnail.add(thumbnail_item.getString("thumbnail"));

                JSONObject location = thumbnail_item.getJSONObject("location");
                mLat.add(location.getDouble("lan"));
                mLong.add(location.getDouble("lng"));

                mDistance.add(getDistanceFromLatLonInKm(userLatitude, userLongitude, mLat.get(i), mLong.get(i)));

            }

            sortArrayList();


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sortArrayList() {

        mCardDataList = new ArrayList<>();

        for (int i = 0; i < mDistance.size(); i++) {
            mCardDataList.add(new CardData(mTitle.get(i), mTag.get(i), mThumbnail.get(i), mLat.get(i), mLong.get(i), mDistance.get(i)));
        }
        CardData temp;
        for (int i = 0; i < mCardDataList.size(); i++) {
            for (int j = 0; j < mCardDataList.size()-1; j++) {
                if (mCardDataList.get(j).getDistance() > mCardDataList.get(j+1).getDistance()) {
                    temp = mCardDataList.get(j);
                    mCardDataList.set(j,mCardDataList.get(j+1));
                    mCardDataList.set(j+1,temp);
                }
            }
        }

        for (int i = 0; i < mCardDataList.size(); i++) {

            Log.d("card1 ", mCardDataList.get(i).title);
            Log.d("card2 ", mCardDataList.get(i).tag);
            Log.d("card3 ", mCardDataList.get(i).thumbnail);
            Log.d("card6 ", String.valueOf(mCardDataList.get(i).distance));
        }
    }


    @Override
    public void getPosition(Double markerLat, Double markerLong) {


        if (markerLat == null || markerLong == null) {
            Toast.makeText(getActivity(), "check connection", Toast.LENGTH_SHORT);
        } else {
            latitude = markerLat;
            longitude = markerLong;

            LatLng position = new LatLng(latitude, longitude);

            if (marker != null) {

                marker.setPosition(position);       //issue while adding marker again
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, zoom));

            }

        }

    }

    //for location

    @Override
    public void onLocationChanged(Location location) {
        userReceivedLat = location.getLatitude();
        userReceivedLong = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public double getDistanceFromLatLonInKm(double lat1, double lon1, double lat2, double lon2) {
        int r = 6371; // Radius of the earth in km
        double dLat = deg2rad(lat2 - lat1);  // deg2rad below
        double dLon = deg2rad(lon2 - lon1);
        double a =
                Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                        Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                                Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = r * c; // Distance in km
        return d;
    }

    public double deg2rad(double deg) {
        return deg * (Math.PI / 180);
    }
}
