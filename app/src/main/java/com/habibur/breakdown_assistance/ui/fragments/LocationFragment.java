package com.habibur.breakdown_assistance.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.habibur.breakdown_assistance.R;
import com.habibur.breakdown_assistance.databinding.FragmentLocationBinding;
import com.habibur.breakdown_assistance.utils.ViewUtils;

public class LocationFragment extends BaseFragment {

    private FragmentLocationBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLocationBinding.inflate(inflater, container, false);

        ViewUtils.setToolbarTitle(requireContext(), binding.header.toolbar, R.string.location, true);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map_fragment);

        if (mapFragment != null) {
            mapFragment.getMapAsync(googleMap -> {
                LatLng location = new LatLng(23.7104, 90.40744);

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(location)
                        .title("My Location");
                googleMap.addMarker(markerOptions);

                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, 10);
                googleMap.animateCamera(cameraUpdate);
            });
        }

        return binding.getRoot();
    }
}