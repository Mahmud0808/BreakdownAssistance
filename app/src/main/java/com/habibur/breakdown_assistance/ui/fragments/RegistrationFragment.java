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
import com.google.firebase.firestore.FirebaseFirestore;
import com.habibur.breakdown_assistance.R;
import com.habibur.breakdown_assistance.databinding.FragmentRegistrationBinding;
import com.habibur.breakdown_assistance.models.UserModel;
import com.habibur.breakdown_assistance.ui.activities.MainActivity;
import com.habibur.breakdown_assistance.utils.ViewUtils;

import java.util.Objects;

public class RegistrationFragment extends BaseFragment {

    private FragmentRegistrationBinding binding;
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false);

        ViewUtils.applyWindowInsets(binding.frameLayoutRegistration, true, true);

        binding.imageViewEmail.setOnClickListener(v -> {
            binding.linearLayoutPhone.setVisibility(View.GONE);
            binding.linearLayoutEmail.setVisibility(View.VISIBLE);
            binding.imageViewEmail.setVisibility(View.GONE);
            binding.imageViewPhone.setVisibility(View.VISIBLE);
            binding.btnContinue.setText(R.string.register);
        });

        binding.imageViewPhone.setOnClickListener(v -> {
            binding.linearLayoutPhone.setVisibility(View.VISIBLE);
            binding.linearLayoutEmail.setVisibility(View.GONE);
            binding.imageViewEmail.setVisibility(View.VISIBLE);
            binding.imageViewPhone.setVisibility(View.GONE);
            binding.btnContinue.setText(R.string.continue_btn);
        });

        binding.btnContinue.setOnClickListener(v -> {
            String name = binding.editTextFullName.getText().toString().trim();
            String phone = binding.editTextPhoneNumber.getText().toString().trim().replace(" ", "");
            String email = binding.editTextEmailAddress.getText().toString().trim();
            String password = binding.editTextPassword.getText().toString().trim();
            String repeatPassword = binding.editTextRepeatPassword.getText().toString().trim();
            String vehicleCompany = binding.editTextVehicleCompany.getText().toString().trim();
            String vehicleModel = binding.editTextVehicleModel.getText().toString().trim();
            boolean isPhoneRegistration = binding.linearLayoutPhone.getVisibility() == View.VISIBLE;
            boolean isEmailRegistration = binding.linearLayoutEmail.getVisibility() == View.VISIBLE;
            boolean error = false;

            if (name.isEmpty()) {
                binding.editTextFullName.setError(getString(R.string.enter_name));
                error = true;
            }

            if (isPhoneRegistration) {
                if (phone.isEmpty() || !phone.matches("^[+]?[0-9]{11,13}$")) {
                    binding.editTextPhoneNumber.setError(getString(R.string.enter_valid_phone_number));
                    error = true;
                }
            } else if (isEmailRegistration) {
                if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    binding.editTextEmailAddress.setError(getString(R.string.enter_valid_email));
                    error = true;
                }

                if (password.isEmpty()) {
                    binding.editTextPassword.setError(getString(R.string.enter_password));
                    error = true;
                }

                if (password.length() < 8) {
                    binding.editTextPassword.setError(getString(R.string.password_length));
                    error = true;
                }

                if (!password.equals(repeatPassword)) {
                    binding.editTextRepeatPassword.setError(getString(R.string.password_mismatch));
                    error = true;
                }
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

            if (isPhoneRegistration) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("isRegistration", true);
                bundle.putString("name", name);
                bundle.putString("phone", phone);
                bundle.putString("vehicle_company", vehicleCompany);
                bundle.putString("vehicle_model", vehicleModel);

                Fragment fragment = new OtpVerificationFragment();
                fragment.setArguments(bundle);

                MainActivity.replaceFragment(fragment);
            } else if (isEmailRegistration) {
                registerWithEmailAddress(name, email, password, vehicleCompany, vehicleModel);
            }
        });

        binding.textViewLogin.setOnClickListener(v -> {
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        });

        return binding.getRoot();
    }

    private void registerWithEmailAddress(String name, String email, String password, String vehicleCompany, String vehicleModel) {
        binding.progressBar.setVisibility(View.VISIBLE);

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    String userId = user.getUid();

                    UserModel userModel = new UserModel(userId, name, "", vehicleCompany, vehicleModel, null);

                    firestore.collection(USER_DATABASE).document(userId).set(userModel)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(requireContext(), getString(R.string.registration_successful), Toast.LENGTH_LONG).show();
                                requireActivity().getOnBackPressedDispatcher().onBackPressed();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(requireContext(), "Registration failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                }
            } else {
                Toast.makeText(requireContext(), "Registration failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}