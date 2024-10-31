package com.habibur.breakdown_assistance.ui.fragments;

import static com.habibur.breakdown_assistance.BreakdownAssistance.getAppContext;
import static com.habibur.breakdown_assistance.config.Constants.GARAGES_DATABASE;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.habibur.breakdown_assistance.R;
import com.habibur.breakdown_assistance.databinding.FragmentLocationBinding;
import com.habibur.breakdown_assistance.models.GarageModel;
import com.habibur.breakdown_assistance.utils.DrawableUtils;
import com.habibur.breakdown_assistance.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LocationFragment extends BaseFragment {

    private FragmentLocationBinding binding;
    private final ActivityResultLauncher<String> requestLocationPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), this::loadMapFragment);
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private GoogleMap googleMap;
    private Marker currentMarker;
    private ListenerRegistration listenerRegistration;
    private final List<GarageModel> garageModels = new ArrayList<>();

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
        binding.header.progressBar.setVisibility(View.VISIBLE);
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
            binding.header.progressBar.setVisibility(View.GONE);

            LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());

            if (currentMarker != null) {
                currentMarker.remove();
            }

            googleMap.getUiSettings().setMapToolbarEnabled(false);

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latlng)
                    .title(getString(R.string.my_location));
            currentMarker = googleMap.addMarker(markerOptions);

            fetchGaragesAndDisplay(latlng);
        }
    }

    private void fetchGaragesAndDisplay(LatLng myLocation) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        listenerRegistration = firestore.collection(GARAGES_DATABASE)
                .addSnapshotListener((QuerySnapshot snapshots, FirebaseFirestoreException e) -> {
                    if (e != null) {
                        Log.d(LocationFragment.class.getSimpleName(), Objects.requireNonNull(e.getMessage()));
                        return;
                    }

                    if (snapshots != null) {
                        for (DocumentSnapshot snapshot : snapshots) {
                            GarageModel garage = snapshot.toObject(GarageModel.class);
                            if (garage != null) {
                                garageModels.add(garage);
                            }
                        }

                        displayGaragesOnMap(garageModels, myLocation);
                    }
                });
    }

    private void displayGaragesOnMap(List<GarageModel> garageModels, LatLng myLocation) {
        if (googleMap != null) {
            for (GarageModel garage : garageModels) {
                LatLng latLng = new LatLng(garage.getLatitude(), garage.getLongitude());
                Bitmap bitmap = DrawableUtils.drawableToBitmap(getAppContext(), R.drawable.ic_garage_marker);
                bitmap = DrawableUtils.tintBitmap(bitmap, getAppContext().getColor(R.color.dark_purple));
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .title(garage.getName())
                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                googleMap.addMarker(markerOptions);
            }

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLocation, 12);
            googleMap.animateCamera(cameraUpdate);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }
}
