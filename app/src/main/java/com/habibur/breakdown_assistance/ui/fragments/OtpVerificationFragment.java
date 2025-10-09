package com.habibur.breakdown_assistance.ui.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.habibur.breakdown_assistance.R;
import com.habibur.breakdown_assistance.databinding.FragmentOtpVerificationBinding;
import com.habibur.breakdown_assistance.ui.activities.MainActivity;
import com.habibur.breakdown_assistance.utils.TextUtils;
import com.habibur.breakdown_assistance.utils.ViewUtils;

public class OtpVerificationFragment extends Fragment {

    private FragmentOtpVerificationBinding binding;
    private CountDownTimer countDownTimer;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOtpVerificationBinding.inflate(inflater, container, false);

        ViewUtils.applyWindowInsets(binding.frameLayoutOtpVerification, true, true);

        assert getArguments() != null;
        boolean isRegistration = getArguments().getBoolean("isRegistration");

        String phone = getArguments().getString("phone");
        if (phone != null && !phone.startsWith("+880")) {
            phone = "+880" + phone.substring(1);
        }

        binding.textViewOtpDescription.setText(TextUtils.getSpannedText(getString(R.string.enter_otp_description, phone)));

        startCountdownTimer();

        binding.textViewResendOtp.setOnClickListener(v -> {
            startCountdownTimer();
        });

        if (isRegistration) {
            // TODO: Add registration process

            binding.btnVerify.setOnClickListener(v -> {
                MainActivity.replaceFragment(new MainFragment());
            });
        } else {
            // TODO: Add login process

            binding.btnVerify.setOnClickListener(v -> {
                MainActivity.replaceFragment(new MainFragment());
            });
        }

        return binding.getRoot();
    }

    private void startCountdownTimer() {
        binding.linearlayoutResendOtp.setVisibility(View.INVISIBLE);

        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String timer = getString(R.string.otp_time, millisUntilFinished / 1000);
                binding.textViewOtpTime.setText(timer);
            }

            @Override
            public void onFinish() {
                String timer = getString(R.string.otp_time, 0);
                binding.textViewOtpTime.setText(timer);

                binding.linearlayoutResendOtp.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}