package com.habibur.breakdown_assistance.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.habibur.breakdown_assistance.R;
import com.habibur.breakdown_assistance.config.Prefs;
import com.habibur.breakdown_assistance.databinding.FragmentPanelBinding;
import com.habibur.breakdown_assistance.utils.ViewUtils;

public class PanelFragment extends BaseFragment {

    private FragmentPanelBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPanelBinding.inflate(inflater, container, false);

        ViewUtils.setToolbarTitle(requireContext(), binding.header.toolbar, R.string.panel, true);

        binding.cardAddService.setVisibility(Prefs.isAdmin() ? View.VISIBLE : View.GONE);
        binding.cardAddGarage.setOnClickListener(v -> MainFragment.replaceFragment(new NewGarageFragment()));

        binding.cardAddService.setVisibility(Prefs.isAdmin() ? View.VISIBLE : View.GONE);
        binding.cardAddService.setOnClickListener(v -> MainFragment.replaceFragment(new NewServiceFragment()));

        binding.cardViewUsers.setVisibility(Prefs.isAdmin() ? View.VISIBLE : View.GONE);

        binding.cardViewComplaints.setVisibility(Prefs.isAdmin() ? View.VISIBLE : View.GONE);

        binding.cardServiceRequest.setVisibility(Prefs.isMechanic() ? View.VISIBLE : View.GONE);

        binding.cardViewHistory.setVisibility(Prefs.isMechanic() ? View.VISIBLE : View.GONE);

        return binding.getRoot();
    }
}