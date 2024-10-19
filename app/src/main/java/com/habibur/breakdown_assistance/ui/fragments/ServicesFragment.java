package com.habibur.breakdown_assistance.ui.fragments;

import static com.habibur.breakdown_assistance.config.Constants.SERVICE_DATABASE;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.habibur.breakdown_assistance.R;
import com.habibur.breakdown_assistance.adapters.ServiceAdapter;
import com.habibur.breakdown_assistance.databinding.FragmentServicesBinding;
import com.habibur.breakdown_assistance.models.ServiceModel;
import com.habibur.breakdown_assistance.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ServicesFragment extends BaseFragment {

    private FragmentServicesBinding binding;
    private ServiceAdapter adapter;
    private final List<ServiceModel> serviceList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentServicesBinding.inflate(inflater, container, false);

        ViewUtils.setToolbarTitle(requireContext(), binding.header.toolbar, R.string.services, true);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ServiceAdapter(requireContext(), serviceList);
        binding.recyclerView.setAdapter(adapter);

        fetchServices();

        return binding.getRoot();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchServices() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection(SERVICE_DATABASE).addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.d(ServicesFragment.class.getSimpleName(), Objects.requireNonNull(error.getMessage()));
                return;
            }

            serviceList.clear();

            if (value != null) {
                for (DocumentSnapshot doc : value) {
                    ServiceModel service = doc.toObject(ServiceModel.class);
                    serviceList.add(service);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}