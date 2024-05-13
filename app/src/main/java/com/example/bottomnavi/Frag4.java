package com.example.bottomnavi;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Frag4 extends Fragment implements OnMapReadyCallback {

    private View view;
    private ArrayList<String> selectedPlaces = new ArrayList<>();
    private FusedLocationProviderClient fusedLocationClient;
    private MapView mapView;
    private GoogleMap googleMap;
    private Button btnCurrentLocation;
    private Location currentLocation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag4,container,false);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        loadSelectedPlaces();
        getCurrentLocation();

        btnCurrentLocation = view.findViewById(R.id.btnCurrentLocation);
        btnCurrentLocation.setOnClickListener(v -> {
            if (currentLocation != null) {
                LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
            }
        });

        return view;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        LatLng defaultLocation = new LatLng(-34, 151);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(defaultLocation));

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);

        fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), location -> {
            if (location != null) {
                currentLocation = location;
                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void loadSelectedPlaces() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("selectedPlaces", Context.MODE_PRIVATE);
        Set<String> set = sharedPreferences.getStringSet("selectedPlaces", null);
        if (set != null) {
            selectedPlaces = new ArrayList<>(set);
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        Task<Location> locationResult = fusedLocationClient.getLastLocation();
        locationResult.addOnCompleteListener(getActivity(), task -> {
            if (task.isSuccessful()) {
                Location lastKnownLocation = task.getResult();
                if (lastKnownLocation != null) {
                    double latitude = lastKnownLocation.getLatitude();
                    double longitude = lastKnownLocation.getLongitude();
                    getDirections(latitude, longitude);
                }
            }
        });
    }

    private void getDirections(double latitude, double longitude) {
        String origin = latitude + "," + longitude;
        String destination = origin;
        String waypoints = "optimize:true|";
        for (String place : selectedPlaces) {
            waypoints += place + "|";
        }

        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + origin + "&destination=" + destination + "&waypoints=" + waypoints + "&key=AIzaSyA7aq3xaaMlOOwtTWMyOMUCtyWFbvu9AGo";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                JSONArray routes = response.getJSONArray("routes");
                for (int i = 0; i < routes.length(); i++) {
                    JSONObject route = routes.getJSONObject(i);
                    JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
                    String encodedPath = overviewPolyline.getString("points");

                    // Decode the encoded path and add it to the map
                    List<LatLng> path = PolyUtil.decode(encodedPath);
                    PolylineOptions polylineOptions = new PolylineOptions().addAll(path).color(Color.BLUE).width(10);
                    googleMap.addPolyline(polylineOptions);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, Throwable::printStackTrace);

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(request);
    }
}