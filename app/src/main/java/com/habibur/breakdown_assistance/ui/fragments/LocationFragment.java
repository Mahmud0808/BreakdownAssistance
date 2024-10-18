package com.habibur.breakdown_assistance.ui.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.habibur.breakdown_assistance.R;
import com.habibur.breakdown_assistance.databinding.FragmentLocationBinding;
import com.habibur.breakdown_assistance.utils.ViewUtils;

public class LocationFragment extends BaseFragment {

    private FragmentLocationBinding binding;
    private final ActivityResultLauncher<String> requestLocationPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), this::loadMapFragment);
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private GoogleMap googleMap;
    private Marker currentMarker;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLocationBinding.inflate(inflater, container, false);

        ViewUtils.setToolbarTitle(requireContext(), binding.header.toolbar, R.string.location, true);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        askLocationPermission();

        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    private void askLocationPermission() {
        binding.header.progress.setVisibility(View.VISIBLE);
        requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void loadMapFragment(boolean hasLocationPermission) {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);

        if (mapFragment != null && hasLocationPermission) {
            mapFragment.getMapAsync(this::initializeMap);
        }
    }

    private void initializeMap(GoogleMap map) {
        this.googleMap = map;
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                    .setWaitForAccurateLocation(false)
                    .setMinUpdateIntervalMillis(5000)
                    .setMaxUpdateDelayMillis(10000)
                    .build();

            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    for (Location location : locationResult.getLocations()) {
                        updateMapLocation(location);
                    }
                }
            };

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }

    private void updateMapLocation(Location location) {
        if (googleMap != null && location != null) {
            binding.header.progress.setVisibility(View.GONE);

            LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());

            if (currentMarker != null) {
                googleMap.clear();
                currentMarker.remove();
            }

            googleMap.getUiSettings().setMapToolbarEnabled(false);

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latlng)
                    .title("My Location");
            currentMarker = googleMap.addMarker(markerOptions);

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latlng, 15);
            googleMap.animateCamera(cameraUpdate);
        }
    }
}
