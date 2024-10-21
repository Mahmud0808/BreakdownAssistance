package com.habibur.breakdown_assistance.ui.fragments;

import static com.habibur.breakdown_assistance.config.Constants.COMPLAINTS_DATABASE;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.habibur.breakdown_assistance.R;
import com.habibur.breakdown_assistance.adapters.UserComplaintAdapter;
import com.habibur.breakdown_assistance.databinding.FragmentUserComplaintsBinding;
import com.habibur.breakdown_assistance.models.ComplaintModel;
import com.habibur.breakdown_assistance.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserComplaintsFragment extends Fragment {

    private FragmentUserComplaintsBinding binding;
    private UserComplaintAdapter adapter;
    private final List<ComplaintModel> complaintList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserComplaintsBinding.inflate(inflater, container, false);

        ViewUtils.setToolbarTitle(requireContext(), binding.header.toolbar, R.string.complaints, true);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new UserComplaintAdapter(requireContext(), complaintList);
        binding.recyclerView.setAdapter(adapter);

        fetchComplaints();

        return binding.getRoot();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchComplaints() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection(COMPLAINTS_DATABASE).addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.d(UserComplaintsFragment.class.getSimpleName(), Objects.requireNonNull(error.getMessage()));
                return;
            }

            complaintList.clear();

            if (value != null) {
                for (DocumentSnapshot doc : value) {
                    ComplaintModel complaint = doc.toObject(ComplaintModel.class);
                    complaintList.add(complaint);
                }
                adapter.notifyDataSetChanged();

                if (!complaintList.isEmpty()) {
                    binding.noComplaintFound.setVisibility(View.GONE);
                }
            }
        });
    }
}