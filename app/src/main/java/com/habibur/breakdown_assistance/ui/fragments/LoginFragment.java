package com.habibur.breakdown_assistance.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.habibur.breakdown_assistance.R;
import com.habibur.breakdown_assistance.databinding.FragmentLoginBinding;
import com.habibur.breakdown_assistance.ui.activities.MainActivity;
import com.habibur.breakdown_assistance.utils.TextUtils;
import com.habibur.breakdown_assistance.utils.ViewUtils;

public class LoginFragment extends BaseFragment {

    private FragmentLoginBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);

        ViewUtils.applyWindowInsets(binding.frameLayoutLogin, true, true);

        binding.textViewOtpDescription.setText(TextUtils.getSpannedText(getString(R.string.otp_description)));

        binding.btnLogin.setOnClickListener(v -> {
            String phone = binding.editTextPhoneNumber.getText().toString().trim().replace(" ", "");

            if (phone.isEmpty() || !phone.matches("^[+]?[0-9]{11,13}$")) {
                binding.editTextPhoneNumber.setError(getString(R.string.enter_valid_phone_number));
                return;
            }

            Bundle bundle = new Bundle();
            bundle.putBoolean("isRegistration", false);
            bundle.putString("phone", phone);

            Fragment fragment = new OtpVerificationFragment();
            fragment.setArguments(bundle);

            MainActivity.replaceFragment(fragment);
        });

        binding.textViewRegister.setOnClickListener(v -> {
            MainActivity.replaceFragment(new RegistrationFragment());
        });

        return binding.getRoot();
    }
}