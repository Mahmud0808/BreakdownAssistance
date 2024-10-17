package com.habibur.breakdown_assistance.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.habibur.breakdown_assistance.databinding.FragmentLocationBinding;

public class LocationFragment extends BaseFragment {

    private FragmentLocationBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLocationBinding.inflate(inflater, container, false);

        binding.mapView.onCreate(savedInstanceState);

        MapsInitializer.initialize(requireContext());

//        binding.mapView.getMapAsync(googleMap -> {
//            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
//            googleMap.setMyLocationEnabled(true);
//
//            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(43.1, -87.9), 10);
//            googleMap.animateCamera(cameraUpdate);
//        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        binding.mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        binding.mapView.onLowMemory();
    }
}