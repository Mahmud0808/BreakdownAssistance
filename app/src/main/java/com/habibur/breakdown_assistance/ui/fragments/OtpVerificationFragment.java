package com.habibur.breakdown_assistance.ui.fragments;

import static com.habibur.breakdown_assistance.config.Constants.USER_DATABASE;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.habibur.breakdown_assistance.R;
import com.habibur.breakdown_assistance.config.Prefs;
import com.habibur.breakdown_assistance.databinding.FragmentOtpVerificationBinding;
import com.habibur.breakdown_assistance.models.UserModel;
import com.habibur.breakdown_assistance.ui.activities.MainActivity;
import com.habibur.breakdown_assistance.utils.TextUtils;
import com.habibur.breakdown_assistance.utils.ViewUtils;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class OtpVerificationFragment extends Fragment {

    private FragmentOtpVerificationBinding binding;
    private CountDownTimer countDownTimer;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private String verificationId = "";

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String id, @NonNull PhoneAuthProvider.ForceResendingToken token) {
            super.onCodeSent(id, token);
            verificationId = id;
            binding.btnVerify.setEnabled(true);
            binding.btnVerify.setAlpha(1f);
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            final String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOtpVerificationBinding.inflate(inflater, container, false);

        ViewUtils.applyWindowInsets(binding.frameLayoutOtpVerification, true, true);

        assert getArguments() != null;
        String phone = getArguments().getString("phone");
        if (phone != null && !phone.startsWith("+880")) {
            phone = "+880" + phone.substring(1);
        }

        binding.textViewOtpDescription.setText(TextUtils.getSpannedText(getString(R.string.enter_otp_description, phone)));

        sendVerificationCode(phone);

        binding.btnVerify.setOnClickListener(v -> {
            String code = binding.editTextOtp1.getText().toString().trim() +
                    binding.editTextOtp2.getText().toString().trim() +
                    binding.editTextOtp3.getText().toString().trim() +
                    binding.editTextOtp4.getText().toString().trim() +
                    binding.editTextOtp5.getText().toString().trim() +
                    binding.editTextOtp6.getText().toString().trim();

            if (code.length() < 6) {
                Toast.makeText(requireContext(), "Please enter all digits", Toast.LENGTH_SHORT).show();
                return;
            }

            verifyCode(code);
        });

        binding.textViewResendOtp.setOnClickListener(v -> {
            startCountdownTimer();
        });

        setupOtpInputs();

        return binding.getRoot();
    }

    private void sendVerificationCode(String number) {
        verificationId = "";
        binding.btnVerify.setEnabled(false);
        binding.btnVerify.setAlpha(0.5f);

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(number)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(requireActivity())
                .setCallbacks(mCallBack)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

        startCountdownTimer();
    }

    private void verifyCode(String code) {
        if (verificationId.isEmpty()) {
            Toast.makeText(requireContext(), "Unexpected error occurred. Please try again.", Toast.LENGTH_LONG).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (getArguments() != null) {
                    boolean isRegistration = getArguments().getBoolean("isRegistration");

                    if (isRegistration) {
                        registerUser();
                    } else {
                        checkIfUserRegistered();
                    }
                }
            } else {
                Toast.makeText(requireContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();

                binding.progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void startCountdownTimer() {
        binding.linearlayoutResendOtp.setVisibility(View.INVISIBLE);

        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String timer = getString(R.string.otp_time, String.valueOf((int) (millisUntilFinished / 1000)));
                binding.textViewOtpTime.setText(timer);
            }

            @Override
            public void onFinish() {
                String timer = getString(R.string.otp_time, "0");
                binding.textViewOtpTime.setText(timer);

                binding.linearlayoutResendOtp.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    private void checkIfUserRegistered() {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            String userId = user.getUid();
            DocumentReference userDocRef = firestore.collection(USER_DATABASE).document(userId);

            userDocRef.get().addOnCompleteListener(docTask -> {
                if (docTask.isSuccessful() && docTask.getResult().exists()) {
                    // User is registered
                    String accountType = docTask.getResult().getString("accountType");
                    Prefs.putString("account_type", accountType);

                    MainActivity.replaceFragment(new MainFragment());

                    binding.progressBar.setVisibility(View.INVISIBLE);
                } else {
                    firebaseAuth.signOut();
                    Toast.makeText(requireContext(), R.string.you_don_t_have_an_account, Toast.LENGTH_SHORT).show();

                    binding.progressBar.setVisibility(View.INVISIBLE);
                }
            });
        } else {
            // Fallback case, user should not be null here
            Toast.makeText(requireContext(), R.string.unexpected_error_occurred, Toast.LENGTH_LONG).show();

            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void registerUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null && getArguments() != null) {
            String userId = user.getUid();
            String name = getArguments().getString("name");
            String phone = getArguments().getString("phone");
            String vehicleCompany = getArguments().getString("vehicle_company");
            String vehicleModel = getArguments().getString("vehicle_model");

            UserModel userModel = new UserModel(userId, name, phone, vehicleCompany, vehicleModel, null);

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection(USER_DATABASE).document(userId).set(userModel)
                    .addOnSuccessListener(aVoid -> {
                        Prefs.putString("account_type", "USER");
                        MainActivity.replaceFragment(new MainFragment());

                        binding.progressBar.setVisibility(View.INVISIBLE);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Registration failed: " + e.getMessage(), Toast.LENGTH_LONG).show();

                        binding.progressBar.setVisibility(View.INVISIBLE);
                    });
        }
    }

    private void setupOtpInputs() {
        binding.editTextOtp1.addTextChangedListener(new OtpTextWatcher(binding.editTextOtp1, null, binding.editTextOtp2));
        binding.editTextOtp2.addTextChangedListener(new OtpTextWatcher(binding.editTextOtp2, binding.editTextOtp1, binding.editTextOtp3));
        binding.editTextOtp3.addTextChangedListener(new OtpTextWatcher(binding.editTextOtp3, binding.editTextOtp2, binding.editTextOtp4));
        binding.editTextOtp4.addTextChangedListener(new OtpTextWatcher(binding.editTextOtp4, binding.editTextOtp3, binding.editTextOtp5));
        binding.editTextOtp5.addTextChangedListener(new OtpTextWatcher(binding.editTextOtp5, binding.editTextOtp4, binding.editTextOtp6));
        binding.editTextOtp6.addTextChangedListener(new OtpTextWatcher(binding.editTextOtp6, binding.editTextOtp5, null));
    }

    private static class OtpTextWatcher implements TextWatcher {
        private final EditText nextView;

        OtpTextWatcher(EditText currentView, EditText previousView, EditText nextView) {
            this.nextView = nextView;

            currentView.setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (currentView.getText().toString().isEmpty() && previousView != null) {
                        previousView.requestFocus();
                        previousView.setText("");
                        return true;
                    }
                }
                return false;
            });
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() == 1 && nextView != null) {
                nextView.requestFocus();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}