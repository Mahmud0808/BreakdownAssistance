package com.habibur.breakdown_assistance.ui.fragments;

import static com.habibur.breakdown_assistance.config.Constants.GARAGES_DATABASE;
import static com.habibur.breakdown_assistance.config.Constants.REQUESTED_SERVICES_DATABASE;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.habibur.breakdown_assistance.R;
import com.habibur.breakdown_assistance.databinding.FragmentRequestServicingBinding;
import com.habibur.breakdown_assistance.models.GarageModel;
import com.habibur.breakdown_assistance.models.RequestServiceModel;
import com.habibur.breakdown_assistance.models.RequestStatus;
import com.habibur.breakdown_assistance.models.ServiceModel;
import com.habibur.breakdown_assistance.models.UserModel;
import com.habibur.breakdown_assistance.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class RequestServicingFragment extends BaseFragment {

    private FragmentRequestServicingBinding binding;
    private GarageModel selectedGarage;
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRequestServicingBinding.inflate(inflater, container, false);

        ViewUtils.setToolbarTitle(requireContext(), binding.header.toolbar, R.string.servicing, true);

        loadGarages();

        if (getArguments() != null) {
            ServiceModel serviceModel = (ServiceModel) getArguments().getSerializable("service");
            UserModel userModel = (UserModel) getArguments().getSerializable("user");

            if (serviceModel != null && userModel != null) {
                checkRequestAlreadyExists(userModel, serviceModel);

                Glide.with(requireContext())
                        .asBitmap()
                        .centerCrop()
                        .placeholder(R.drawable.img_loading)
                        .error(R.drawable.img_error)
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .load(serviceModel.getImage())
                        .into(binding.imageView);

                binding.textViewTitle.setText(serviceModel.getTitle());
                binding.textViewDescription.setText(serviceModel.getDescription());
                binding.editTextVehicleCompany.setText(userModel.getVehicleCompany());
                binding.editTextVehicleModel.setText(userModel.getVehicleModel());
                binding.editTextMinimumPrice.setText("৳ " + serviceModel.getMinimumPrice());
                binding.editTextServiceCharge.setText("৳ " + serviceModel.getServiceCharge());
                binding.editTextDuration.setText(serviceModel.getDurationHours() + " Hour" + (serviceModel.getDurationHours() > 1 ? "s" : ""));

                binding.btnSubmit.setOnClickListener(v -> {
                    String vehicleCompany = binding.editTextVehicleCompany.getText().toString().trim();
                    String vehicleModel = binding.editTextVehicleModel.getText().toString().trim();
                    String additionalInfo = binding.editTextAdditionalInfo.getText().toString().trim();
                    boolean error = false;

                    if (vehicleCompany.isEmpty()) {
                        binding.editTextVehicleCompany.setError("Enter vehicle company");
                        error = true;
                    }

                    if (vehicleModel.isEmpty()) {
                        binding.editTextVehicleModel.setError("Enter vehicle model");
                        error = true;
                    }

                    if (selectedGarage == null) {
                        binding.textInputLayoutSelectGarage.setError("Select a garage");
                        error = true;
                    } else if (selectedGarage.getId() == null) {
                        binding.textInputLayoutSelectGarage.setError("No garage available at this moment");
                        error = true;
                    }

                    if (error) {
                        return;
                    }

                    String requestId = UUID.randomUUID().toString();

                    RequestServiceModel requestServiceModel = new RequestServiceModel(requestId, userModel, serviceModel, selectedGarage, additionalInfo, RequestStatus.PENDING, null, null, null);

                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    firestore.collection(REQUESTED_SERVICES_DATABASE).document(requestId)
                            .set(requestServiceModel)
                            .addOnSuccessListener(aVoid -> {
                                if (getActivity() != null) {
                                    getActivity().onBackPressed();
                                }

                                Toast.makeText(v.getContext(), "Request submitted", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(v.getContext(), "Error submitting request: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                });
            }
        }

        return binding.getRoot();
    }

    private void loadGarages() {
        firestore.collection(GARAGES_DATABASE).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<GarageModel> garageList = new ArrayList<>();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    GarageModel garage = document.toObject(GarageModel.class);
                    garageList.add(garage);
                }

                if (garageList.isEmpty()) {
                    garageList.add(new GarageModel(null, "None", 0, 0));
                }

                setupGarageDropdown(garageList);
            } else {
                Log.d(RequestServicingFragment.class.getSimpleName(), "Error getting garages: ", task.getException());
                Toast.makeText(requireContext(), "Error getting garages: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupGarageDropdown(List<GarageModel> garageList) {
        List<String> garageNames = new ArrayList<>();

        for (GarageModel garage : garageList) {
            garageNames.add(garage.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.dropdown_item, garageNames);
        binding.autoCompleteTextView.setAdapter(adapter);

        binding.autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedName = (String) parent.getItemAtPosition(position);

            for (GarageModel garage : garageList) {
                if (garage.getName().equals(selectedName)) {
                    selectedGarage = garage;
                    break;
                }
            }
        });

        binding.autoCompleteTextView.setThreshold(1);
    }

    private void checkRequestAlreadyExists(UserModel userModel, ServiceModel serviceModel) {
        firestore.collection(REQUESTED_SERVICES_DATABASE)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean requestExists = false;

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            RequestServiceModel request = document.toObject(RequestServiceModel.class);

                            if (request.getRequestedBy().getId().equals(userModel.getId()) &&
                                    request.getService().getId().equals(serviceModel.getId()) &&
                                    request.getAssignedTo() == null) {
                                requestExists = true;
                                break;
                            }
                        }

                        if (requestExists) {
                            binding.btnSubmit.setEnabled(false);
                            binding.btnSubmit.setAlpha(0.6f);
                            binding.btnSubmit.setText("ALREADY SUBMITTED");
                        } else {
                            binding.btnSubmit.setEnabled(true);
                            binding.btnSubmit.setAlpha(1f);
                            binding.btnSubmit.setText("REQUEST SERVICING");
                        }
                    } else {
                        Log.d(RequestServicingFragment.class.getSimpleName(), "Error checking request: ", task.getException());
                    }
                });
    }
}