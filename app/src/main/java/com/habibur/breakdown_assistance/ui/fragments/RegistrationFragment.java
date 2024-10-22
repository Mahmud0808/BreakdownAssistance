package com.habibur.breakdown_assistance.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.habibur.breakdown_assistance.R;
import com.habibur.breakdown_assistance.databinding.FragmentRegistrationBinding;
import com.habibur.breakdown_assistance.ui.activities.MainActivity;
import com.habibur.breakdown_assistance.utils.ViewUtils;

public class RegistrationFragment extends BaseFragment {

    private FragmentRegistrationBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false);

        ViewUtils.applyWindowInsets(binding.frameLayoutRegistration, true, true);

        binding.btnContinue.setOnClickListener(v -> {
            String name = binding.editTextFullName.getText().toString().trim();
            String phone = binding.editTextPhoneNumber.getText().toString().trim().replace(" ", "");
            String vehicleCompany = binding.editTextVehicleCompany.getText().toString().trim();
            String vehicleModel = binding.editTextVehicleModel.getText().toString().trim();
            boolean error = false;

            if (name.isEmpty()) {
                binding.editTextFullName.setError(getString(R.string.enter_name));
                error = true;
            }

            if (phone.isEmpty() || !phone.matches("^[+]?[0-9]{11,13}$")) {
                binding.editTextPhoneNumber.setError(getString(R.string.enter_valid_phone_number));
                error = true;
            }

            if (vehicleCompany.isEmpty()) {
                binding.editTextVehicleCompany.setError(getString(R.string.enter_vehicle_company));
                error = true;
            }

            if (vehicleModel.isEmpty()) {
                binding.editTextVehicleModel.setError(getString(R.string.enter_vehicle_model));
                error = true;
            }

            if (error) {
                return;
            }

            Bundle bundle = new Bundle();
            bundle.putBoolean("isRegistration", true);
            bundle.putString("name", name);
            bundle.putString("phone", phone);
            bundle.putString("vehicle_company", vehicleCompany);
            bundle.putString("vehicle_model", vehicleModel);

            Fragment fragment = new OtpVerificationFragment();
            fragment.setArguments(bundle);

            MainActivity.replaceFragment(fragment);
        });

        binding.textViewLogin.setOnClickListener(v -> {
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        });

        return binding.getRoot();
    }
}