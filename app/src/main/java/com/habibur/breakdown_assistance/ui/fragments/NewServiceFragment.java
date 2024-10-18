package com.habibur.breakdown_assistance.ui.fragments;

import static com.habibur.breakdown_assistance.config.Constants.SERVICE_DATABASE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;
import com.habibur.breakdown_assistance.R;
import com.habibur.breakdown_assistance.databinding.FragmentNewServiceBinding;
import com.habibur.breakdown_assistance.models.ServiceModel;
import com.habibur.breakdown_assistance.utils.ViewUtils;

import java.util.UUID;

public class NewServiceFragment extends BaseFragment {

    private FragmentNewServiceBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNewServiceBinding.inflate(inflater, container, false);

        ViewUtils.setToolbarTitle(requireContext(), binding.header.toolbar, R.string.new_service, true);

        binding.btnSubmit.setOnClickListener(v -> {
            String imageUrl = binding.editTextImageUrl.getText().toString().trim();
            String title = binding.editTextTitle.getText().toString().trim();
            String description = binding.editTextDescription.getText().toString().trim();
            String minimumPriceString = binding.editTextMinimumPrice.getText().toString().trim();
            String serviceChargeString = binding.editTextServiceCharge.getText().toString().trim();
            String durationString = binding.editTextDuration.getText().toString().trim();
            boolean error = false;

            if (imageUrl.isEmpty()) {
                binding.editTextImageUrl.setError("Enter image url");
                error = true;
            }

            if (title.isEmpty()) {
                binding.editTextTitle.setError("Enter title");
                error = true;
            }

            if (description.isEmpty()) {
                binding.editTextDescription.setError("Enter description");
                error = true;
            }

            if (minimumPriceString.isEmpty()) {
                binding.editTextMinimumPrice.setError("Enter minimum price");
                error = true;
            }

            if (serviceChargeString.isEmpty()) {
                binding.editTextServiceCharge.setError("Enter service charge");
                error = true;
            }

            if (durationString.isEmpty()) {
                binding.editTextDuration.setError("Enter duration");
                error = true;
            }

            if (error) {
                return;
            }

            String serviceId = UUID.randomUUID().toString();
            int minimumPrice = Integer.parseInt(minimumPriceString);
            int serviceCharge = Integer.parseInt(serviceChargeString);
            int duration = Integer.parseInt(durationString);

            ServiceModel serviceModel = new ServiceModel(serviceId, imageUrl, title, description, minimumPrice, serviceCharge, duration);

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection(SERVICE_DATABASE).document(serviceId)
                    .set(serviceModel)
                    .addOnSuccessListener(aVoid -> {
                        binding.editTextImageUrl.setText("");
                        binding.editTextTitle.setText("");
                        binding.editTextDescription.setText("");
                        binding.editTextMinimumPrice.setText("");
                        binding.editTextServiceCharge.setText("");
                        binding.editTextDuration.setText("");

                        Toast.makeText(v.getContext(), "Service saved successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(v.getContext(), "Error saving service: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        return binding.getRoot();
    }
}