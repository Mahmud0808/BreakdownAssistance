package com.habibur.breakdown_assistance.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.habibur.breakdown_assistance.databinding.FragmentNewGarageBinding;

public class NewGarageFragment extends BaseFragment {

    private FragmentNewGarageBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNewGarageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}