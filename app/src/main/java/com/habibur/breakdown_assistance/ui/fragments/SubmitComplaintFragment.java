package com.habibur.breakdown_assistance.ui.fragments;

import static com.habibur.breakdown_assistance.config.Constants.COMPLAINTS_DATABASE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.habibur.breakdown_assistance.R;
import com.habibur.breakdown_assistance.databinding.FragmentSubmitComplaintBinding;
import com.habibur.breakdown_assistance.models.ComplaintModel;
import com.habibur.breakdown_assistance.models.UserModel;
import com.habibur.breakdown_assistance.utils.ViewUtils;

import java.util.UUID;

public class SubmitComplaintFragment extends BaseFragment {

    private FragmentSubmitComplaintBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSubmitComplaintBinding.inflate(inflater, container, false);

        ViewUtils.setToolbarTitle(requireContext(), binding.header.toolbar, R.string.complaint, true);

        if (getArguments() != null) {
            UserModel currentUser = (UserModel) getArguments().getSerializable("user");

            binding.btnSubmit.setOnClickListener(v -> {
                String contactInfo = binding.editTextContactInfo.getText().toString().trim();
                String description = binding.editTextDescription.getText().toString().trim();
                boolean error = false;

                if (contactInfo.isEmpty()) {
                    binding.editTextContactInfo.setError("Enter contact info");
                    error = true;
                }

                if (description.isEmpty()) {
                    binding.editTextDescription.setError("Enter description");
                    error = true;
                }

                if (error) {
                    return;
                }

                String complaintId = UUID.randomUUID().toString();

                ComplaintModel complaintModel = new ComplaintModel(complaintId, currentUser, contactInfo, description);

                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                firestore.collection(COMPLAINTS_DATABASE).document(complaintId)
                        .set(complaintModel)
                        .addOnSuccessListener(aVoid -> {
                            binding.editTextContactInfo.setText("");
                            binding.editTextDescription.setText("");

                            Toast.makeText(v.getContext(), "Complaint submitted successfully", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(v.getContext(), "Error submitted complaint: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            });
        }

        return binding.getRoot();
    }
}