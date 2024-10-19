package com.habibur.breakdown_assistance.ui.fragments;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.habibur.breakdown_assistance.R;
import com.habibur.breakdown_assistance.databinding.FragmentRequestServicingBinding;
import com.habibur.breakdown_assistance.models.ServiceModel;
import com.habibur.breakdown_assistance.models.UserModel;
import com.habibur.breakdown_assistance.utils.ViewUtils;

public class RequestServicingFragment extends BaseFragment {

    private FragmentRequestServicingBinding binding;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRequestServicingBinding.inflate(inflater, container, false);

        ViewUtils.setToolbarTitle(requireContext(), binding.header.toolbar, R.string.servicing, true);

        if (getArguments() != null) {
            ServiceModel serviceModel = (ServiceModel) getArguments().getSerializable("service");
            UserModel userModel = (UserModel) getArguments().getSerializable("user");

            if (serviceModel != null && userModel != null) {
                Glide.with(requireContext())
                        .asBitmap()
                        .placeholder(R.drawable.img_loading)
                        .error(R.drawable.img_error)
                        .transition(withCrossFade())
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .load(serviceModel.getImage())
                        .into(binding.imageView);

                binding.textViewTitle.setText(serviceModel.getTitle());
                binding.textViewDescription.setText(serviceModel.getDescription());
                binding.editTextVehicleCompany.setText(userModel.getVehicleCompany());
                binding.editTextVehicleModel.setText(userModel.getVehicleModel());
                binding.editTextMinimumPrice.setText("৳ " + serviceModel.getMinimumPrice());
                binding.editTextServiceCharge.setText("৳ " + serviceModel.getServiceCharge());
                binding.editTextDuration.setText(serviceModel.getDurationHours() + " Hour(s)");
            }
        }

        return binding.getRoot();
    }
}