package com.habibur.breakdown_assistance.ui.fragments;

import static com.habibur.breakdown_assistance.config.Constants.GARAGES_DATABASE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;
import com.habibur.breakdown_assistance.R;
import com.habibur.breakdown_assistance.databinding.FragmentNewGarageBinding;
import com.habibur.breakdown_assistance.models.GarageModel;
import com.habibur.breakdown_assistance.utils.ViewUtils;

import java.util.UUID;

public class NewGarageFragment extends BaseFragment {

    private FragmentNewGarageBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNewGarageBinding.inflate(inflater, container, false);

        ViewUtils.setToolbarTitle(requireContext(), binding.header.toolbar, R.string.new_garage, true);

        binding.btnSubmit.setOnClickListener(v -> {
            String garageName = binding.editTextGarageName.getText().toString().trim();
            String latitudeString = binding.editTextLatitude.getText().toString().trim();
            String longitudeChargeString = binding.editTextLongitude.getText().toString().trim();
            boolean error = false;

            if (garageName.isEmpty()) {
                binding.editTextGarageName.setError("Enter garage name");
                error = true;
            }

            if (latitudeString.isEmpty()) {
                binding.editTextLatitude.setError("Enter latitude");
                error = true;
            }

            if (longitudeChargeString.isEmpty()) {
                binding.editTextLongitude.setError("Enter longitude");
                error = true;
            }

            if (error) {
                return;
            }

            String garageId = UUID.randomUUID().toString();
            double latitude = Double.parseDouble(latitudeString);
            double longitude = Double.parseDouble(longitudeChargeString);

            GarageModel garageModel = new GarageModel(garageId, garageName, latitude, longitude);

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection(GARAGES_DATABASE).document(garageId)
                    .set(garageModel)
                    .addOnSuccessListener(aVoid -> {
                        binding.editTextGarageName.setText("");
                        binding.editTextLatitude.setText("");
                        binding.editTextLongitude.setText("");

                        Toast.makeText(v.getContext(), "Garage saved successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(v.getContext(), "Error saving garage: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        return binding.getRoot();
    }
}