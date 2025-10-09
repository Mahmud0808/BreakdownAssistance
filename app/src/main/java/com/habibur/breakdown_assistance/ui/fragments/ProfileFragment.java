package com.habibur.breakdown_assistance.ui.fragments;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.READ_MEDIA_VIDEO;
import static android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED;
import static com.habibur.breakdown_assistance.BreakdownAssistance.getAppContext;
import static com.habibur.breakdown_assistance.config.Constants.USER_DATABASE;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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
    private Uri profilePictureLocation;
    private String currentProfilePictureUrl;
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        binding.profilePicture.setImageURI(selectedImageUri);
                        profilePictureLocation = selectedImageUri;
                    }
                }
            }
    );
    private final ActivityResultLauncher<String[]> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            result -> {
                if (result.containsValue(true)) {
                    openImagePicker();
                } else {
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        ViewUtils.setToolbarTitle(requireContext(), binding.header.toolbar, R.string.profile, true);

        binding.profilePicture.setOnClickListener(v -> checkPermissionAndPickImage());

        binding.btnLogout.setOnClickListener(v -> {
            Prefs.clearPref("logged_in");
            Prefs.clearPref("account_type");
            FirebaseAuth.getInstance().signOut();
            MainActivity.replaceFragment(new LandingFragment());
        });

        String userId = FirebaseAuth.getInstance().getUid();

        if (userId != null) {
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
                                currentProfilePictureUrl = currentUser.getImageUrl();

                                RequestOptions reqOptions = new RequestOptions()
                                        .centerCrop()
                                        .override(300, 300);

                                Glide.with(getAppContext())
                                        .asBitmap()
                                        .apply(reqOptions)
                                        .placeholder(R.drawable.img_placeholder_profile_picture)
                                        .error(R.drawable.img_placeholder_profile_picture)
                                        .dontAnimate()
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .load(currentProfilePictureUrl)
                                        .into(binding.profilePicture);
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

                UserModel userModel = new UserModel(userId, name, phone, vehicleCompany, vehicleModel, currentProfilePictureUrl);

                if (profilePictureLocation != null) {
                    uploadImageAndSaveUser(profilePictureLocation, userModel);
                } else {
                    saveUserToFirestore(userModel);
                }
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

    private void checkPermissionAndPickImage() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(requireContext(), READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                requestPermissions();
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            if (ContextCompat.checkSelfPermission(requireContext(), READ_MEDIA_VISUAL_USER_SELECTED) == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                requestPermissions();
            }
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(), READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                requestPermissions();
            }
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            permissionLauncher.launch(new String[]{
                    READ_MEDIA_IMAGES,
                    READ_MEDIA_VIDEO,
                    READ_MEDIA_VISUAL_USER_SELECTED
            });
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(new String[]{
                    READ_MEDIA_IMAGES,
                    READ_MEDIA_VIDEO
            });
        } else {
            permissionLauncher.launch(new String[]{READ_EXTERNAL_STORAGE});
        }
    }

    private void uploadImageAndSaveUser(Uri imageUri, UserModel userModel) {
        if (imageUri != null) {
            StorageReference storageRef = storage.getReference().child("profile_images/" + userModel.getId() + ".jpg");

            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        userModel.setImageUrl(imageUrl);

                        saveUserToFirestore(userModel);
                    }))
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Image upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        }
    }

    private void saveUserToFirestore(UserModel userModel) {
        firestore.collection(USER_DATABASE).document(userModel.getId()).set(userModel)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), R.string.profile_updated_successfully, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Profile update failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}