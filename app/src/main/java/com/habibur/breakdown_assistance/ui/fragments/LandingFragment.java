package com.habibur.breakdown_assistance.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.habibur.breakdown_assistance.config.Prefs;
import com.habibur.breakdown_assistance.databinding.FragmentLandingBinding;
import com.habibur.breakdown_assistance.ui.activities.MainActivity;
import com.habibur.breakdown_assistance.utils.ViewUtils;

public class LandingFragment extends BaseFragment {

    private FragmentLandingBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLandingBinding.inflate(inflater, container, false);

        ViewUtils.applyWindowInsets(binding.layoutLanding, true, false);
        ViewUtils.applyWindowInsets(binding.layoutLanguage, false, true);

        binding.btnEnglish.setOnClickListener(v -> {
            Prefs.putString("app_language", "en-US");
            MainActivity.replaceFragment(new LoginFragment());
        });

        binding.btnBengali.setOnClickListener(v -> {
            Prefs.putString("app_language", "bn-BD");
            MainActivity.replaceFragment(new LoginFragment());
        });

        return binding.getRoot();
    }
}