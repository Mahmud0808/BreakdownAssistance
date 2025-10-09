package com.habibur.breakdown_assistance.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.habibur.breakdown_assistance.R;
import com.habibur.breakdown_assistance.config.Prefs;
import com.habibur.breakdown_assistance.databinding.FragmentMainBinding;
import com.habibur.breakdown_assistance.utils.FragmentUtils;
import com.habibur.breakdown_assistance.utils.ViewUtils;

import java.util.Objects;

public class MainFragment extends BaseFragment {

    private FragmentMainBinding binding;
    private static FragmentManager fragmentManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Prefs.putBoolean("first_run", false);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        fragmentManager = getChildFragmentManager();

        ViewUtils.applyWindowInsets(binding.bottomNavigationView, false, true);

        if (savedInstanceState == null) {
            replaceFragment(new HomeFragment());
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupBottomNavigationView();
        registerOnBackPressedCallback();
    }

    private void setupBottomNavigationView() {
        getChildFragmentManager().addOnBackStackChangedListener(() -> {
            String tag = FragmentUtils.getTopFragment(getChildFragmentManager());

            if (Objects.equals(tag, HomeFragment.class.getSimpleName())) {
                binding.bottomNavigationView.getMenu().getItem(0).setChecked(true);
            } else if (Objects.equals(tag, ServicesFragment.class.getSimpleName())) {
                binding.bottomNavigationView.getMenu().getItem(1).setChecked(true);
            } else if (Objects.equals(tag, LocationFragment.class.getSimpleName())) {
                binding.bottomNavigationView.getMenu().getItem(2).setChecked(true);
            } else if (Objects.equals(tag, ProfileFragment.class.getSimpleName())) {
                binding.bottomNavigationView.getMenu().getItem(3).setChecked(true);
            }
        });

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                replaceFragment(new HomeFragment());
            } else if (item.getItemId() == R.id.nav_services) {
                replaceFragment(new ServicesFragment());
            } else if (item.getItemId() == R.id.nav_location) {
                replaceFragment(new LocationFragment());
            } else if (item.getItemId() == R.id.nav_profile) {
                replaceFragment(new ProfileFragment());
            } else {
                return false;
            }

            return true;
        });

        binding.bottomNavigationView.setOnItemReselectedListener(item -> {
            // Do nothing
        });
    }

    private void registerOnBackPressedCallback() {
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager fragmentManager = getChildFragmentManager();
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStack();
                } else {
                    requireActivity().finish();
                }
            }
        });
    }

    public static void replaceFragment(Fragment fragment) {
        String tag = fragment.getClass().getSimpleName();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);

        if (Objects.equals(tag, HomeFragment.class.getSimpleName())) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else if (Objects.equals(tag, ServicesFragment.class.getSimpleName()) ||
                Objects.equals(tag, LocationFragment.class.getSimpleName()) ||
                Objects.equals(tag, ProfileFragment.class.getSimpleName())
        ) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fragmentTransaction.addToBackStack(tag);
        } else {
            fragmentTransaction.addToBackStack(tag);
        }

        fragmentTransaction.commit();
    }
}