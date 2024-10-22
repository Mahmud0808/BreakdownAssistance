package com.habibur.breakdown_assistance.ui.fragments;

import static com.habibur.breakdown_assistance.config.Constants.USER_DATABASE;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.habibur.breakdown_assistance.R;
import com.habibur.breakdown_assistance.config.Prefs;
import com.habibur.breakdown_assistance.databinding.FragmentProfileBinding;
import com.habibur.breakdown_assistance.models.UserModel;
import com.habibur.breakdown_assistance.ui.activities.MainActivity;
import com.habibur.breakdown_assistance.utils.ViewUtils;

import java.util.Objects;

public class ProfileFragment extends BaseFragment {

    private FragmentProfileBinding binding;
    private UserModel currentUser;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        ViewUtils.setToolbarTitle(requireContext(), binding.header.toolbar, R.string.profile, true);

        binding.btnLogout.setOnClickListener(v -> {
            Prefs.clearPref("logged_in");
            Prefs.clearPref("account_type");
            FirebaseAuth.getInstance().signOut();
            MainActivity.replaceFragment(new LandingFragment());
        });

        String userId = FirebaseAuth.getInstance().getUid();

        if (userId != null) {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            firestore.collection(USER_DATABASE).document(userId)
                    .addSnapshotListener((documentSnapshot, error) -> {
                        if (error != null) {
                            Log.e(ProfileFragment.class.getSimpleName(), Objects.requireNonNull(error.getMessage()));
                            return;
                        }

                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            currentUser = documentSnapshot.toObject(UserModel.class);

                            if (currentUser != null) {
                                binding.editTextFullName.setText(currentUser.getName());
                                binding.editTextPhoneNumber.setText(currentUser.getPhone());
                                binding.editTextVehicleCompany.setText(currentUser.getVehicleCompany());
                                binding.editTextVehicleModel.setText(currentUser.getVehicleModel());
                            }
                        } else {
                            Log.e(ProfileFragment.class.getSimpleName(), getString(R.string.user_not_found));
                        }
                    });

            binding.btnUpdate.setOnClickListener(v -> {
                String name = binding.editTextFullName.getText().toString().trim();
                String phone = binding.editTextPhoneNumber.getText().toString().trim();
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

                UserModel userModel = new UserModel(userId, name, phone, vehicleCompany, vehicleModel);

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection(USER_DATABASE).document(userId).set(userModel)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(requireContext(), R.string.profile_updated_successfully, Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(requireContext(), "Profile update failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
            });

            binding.cardHistory.setOnClickListener(v -> MainFragment.replaceFragment(new MyServicingRequestsFragment()));

            binding.cardComplaint.setOnClickListener(v -> {
                if (currentUser != null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("user", currentUser);

                    Fragment fragment = new SubmitComplaintFragment();
                    fragment.setArguments(bundle);

                    MainFragment.replaceFragment(fragment);
                }
            });
        }

        if (Prefs.isAdminOrMechanic()) {
            binding.linearLayoutCards.setVisibility(View.GONE);
        }

        return binding.getRoot();
    }
}