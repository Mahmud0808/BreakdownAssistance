package com.habibur.breakdown_assistance.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.habibur.breakdown_assistance.R;
import com.habibur.breakdown_assistance.databinding.FragmentServicesBinding;
import com.habibur.breakdown_assistance.utils.ViewUtils;

public class ServicesFragment extends BaseFragment {

    private FragmentServicesBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentServicesBinding.inflate(inflater, container, false);

        ViewUtils.setToolbarTitle(requireContext(), binding.header.toolbar, R.string.services, true);

        return binding.getRoot();
    }
}