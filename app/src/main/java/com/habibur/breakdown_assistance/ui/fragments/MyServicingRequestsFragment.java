package com.habibur.breakdown_assistance.ui.fragments;

import static com.habibur.breakdown_assistance.config.Constants.REQUESTED_SERVICES_DATABASE;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.habibur.breakdown_assistance.R;
import com.habibur.breakdown_assistance.adapters.MyRequestedServicingAdapter;
import com.habibur.breakdown_assistance.databinding.FragmentServicingRequestsBinding;
import com.habibur.breakdown_assistance.models.RequestServiceModel;
import com.habibur.breakdown_assistance.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyServicingRequestsFragment extends BaseFragment {

    private FragmentServicingRequestsBinding binding;
    private MyRequestedServicingAdapter adapter;
    private final List<RequestServiceModel> requestServiceList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentServicingRequestsBinding.inflate(inflater, container, false);

        ViewUtils.setToolbarTitle(requireContext(), binding.header.toolbar, R.string.my_servicing_requests, true);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new MyRequestedServicingAdapter(requireContext(), requestServiceList);
        binding.recyclerView.setAdapter(adapter);

        fetchMyServicingRequests();

        return binding.getRoot();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchMyServicingRequests() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection(REQUESTED_SERVICES_DATABASE).addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.d(MyServicingRequestsFragment.class.getSimpleName(), Objects.requireNonNull(error.getMessage()));
                return;
            }

            requestServiceList.clear();

            if (value != null) {
                String userId = FirebaseAuth.getInstance().getUid();

                for (DocumentSnapshot doc : value) {
                    RequestServiceModel requestedService = doc.toObject(RequestServiceModel.class);

                    if (requestedService != null && Objects.equals(requestedService.getRequestedBy().getId(), userId)) {
                        requestServiceList.add(requestedService);
                    }
                }
                adapter.notifyDataSetChanged();

                if (!requestServiceList.isEmpty()) {
                    binding.noRequestFound.setVisibility(View.GONE);
                }
            }
        });
    }
}