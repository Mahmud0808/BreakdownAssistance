package com.habibur.breakdown_assistance.ui.fragments;

import static com.habibur.breakdown_assistance.config.Constants.USER_DATABASE;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.habibur.breakdown_assistance.R;
import com.habibur.breakdown_assistance.config.Prefs;
import com.habibur.breakdown_assistance.databinding.FragmentLoginBinding;
import com.habibur.breakdown_assistance.ui.activities.MainActivity;
import com.habibur.breakdown_assistance.utils.TextUtils;
import com.habibur.breakdown_assistance.utils.ViewUtils;

import java.util.Objects;

public class LoginFragment extends BaseFragment {

    private FragmentLoginBinding binding;
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);

        ViewUtils.applyWindowInsets(binding.frameLayoutLogin, true, true);

        // Login with phone number
        binding.textViewOtpDescription.setText(TextUtils.getSpannedText(getString(R.string.otp_description)));

        binding.btnGetOtp.setOnClickListener(v -> {
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

        binding.imageViewEmail.setOnClickListener(v -> {
            binding.linearLayoutPhoneLogin.setVisibility(View.GONE);
            binding.linearLayoutEmailLogin.setVisibility(View.VISIBLE);
        });

        // Login with email
        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.editTextEmail.getText().toString().trim();
            String password = binding.editTextPassword.getText().toString().trim();
            boolean error = false;

            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.editTextEmail.setError(getString(R.string.enter_valid_email));
                error = true;
            }

            if (password.isEmpty()) {
                binding.editTextPassword.setError(getString(R.string.enter_password));
                error = true;
            } else if (password.length() < 8) {
                binding.editTextPassword.setError(getString(R.string.password_length));
                error = true;
            }

            if (error) {
                return;
            }

            loginUserWithEmailPassword(email, password);
        });

        binding.imageViewPhone.setOnClickListener(v -> {
            binding.linearLayoutPhoneLogin.setVisibility(View.VISIBLE);
            binding.linearLayoutEmailLogin.setVisibility(View.GONE);
        });

        binding.textViewRegister.setOnClickListener(v -> {
            MainActivity.replaceFragment(new RegistrationFragment());
        });

        return binding.getRoot();
    }

    private void loginUserWithEmailPassword(String email, String password) {
        binding.progressBar.setVisibility(View.VISIBLE);

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    String userId = user.getUid();
                    DocumentReference userDocRef = firestore.collection(USER_DATABASE).document(userId);

                    userDocRef.get().addOnCompleteListener(docTask -> {
                        if (docTask.isSuccessful() && docTask.getResult().exists()) {
                            String accountType = docTask.getResult().getString("accountType");
                            Prefs.putString("account_type", accountType);

                            MainActivity.replaceFragment(new MainFragment());
                        } else {
                            firebaseAuth.signOut();
                            Toast.makeText(requireContext(), R.string.you_don_t_have_an_account, Toast.LENGTH_SHORT).show();
                        }

                        binding.progressBar.setVisibility(View.INVISIBLE);
                    });
                } else {
                    // Fallback case, user should not be null here
                    Toast.makeText(requireContext(), R.string.unexpected_error_occurred, Toast.LENGTH_LONG).show();

                    binding.progressBar.setVisibility(View.INVISIBLE);
                }
            } else {
                Toast.makeText(requireContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();

                binding.progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}