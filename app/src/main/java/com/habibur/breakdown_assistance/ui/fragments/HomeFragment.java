package com.habibur.breakdown_assistance.ui.fragments;

import static com.habibur.breakdown_assistance.BreakdownAssistance.getAppContext;
import static com.habibur.breakdown_assistance.config.Constants.SERVICE_DATABASE;
import static com.habibur.breakdown_assistance.config.Constants.USER_DATABASE;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.habibur.breakdown_assistance.R;
import com.habibur.breakdown_assistance.databinding.FragmentHomeBinding;
import com.habibur.breakdown_assistance.models.ServiceModel;
import com.habibur.breakdown_assistance.models.UserModel;
import com.habibur.breakdown_assistance.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends BaseFragment {

    private FragmentHomeBinding binding;
    private UserModel user;
    private final List<ServiceModel> serviceList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        ViewUtils.applyWindowInsets(binding.linearLayoutContainer, true, false);

        getCurrentUser();
        fetchServices();

        return binding.getRoot();
    }

    private void getCurrentUser() {
        String userId = FirebaseAuth.getInstance().getUid();

        if (userId != null) {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            firestore.collection(USER_DATABASE).document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            user = documentSnapshot.toObject(UserModel.class);
                        } else {
                            Log.e(HomeFragment.class.getSimpleName(), getString(R.string.user_not_found));
                        }
                    })
                    .addOnFailureListener(e -> Log.e(HomeFragment.class.getSimpleName(), Objects.requireNonNull(e.getMessage())));
        } else {
            Log.e(HomeFragment.class.getSimpleName(), getString(R.string.user_not_logged_in));
        }
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

                int size = serviceList.size();

                RequestOptions reqOptions = new RequestOptions()
                        .centerCrop()
                        .override(300, 300);

                Glide.with(getAppContext())
                        .asBitmap()
                        .apply(reqOptions)
                        .placeholder(R.drawable.img_loading)
                        .error(R.drawable.img_error)
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .load(serviceList.get(size - 1).getImage())
                        .into(binding.imgService1);

                Glide.with(getAppContext())
                        .asBitmap()
                        .apply(reqOptions)
                        .placeholder(R.drawable.img_loading)
                        .error(R.drawable.img_error)
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .load(serviceList.get(size - 2).getImage())
                        .into(binding.imgService2);

                Glide.with(getAppContext())
                        .asBitmap()
                        .apply(reqOptions)
                        .placeholder(R.drawable.img_loading)
                        .error(R.drawable.img_error)
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .load(serviceList.get(size - 3).getImage())
                        .into(binding.imgService3);

                binding.txtService1.setText(serviceList.get(size - 1).getTitle());
                binding.txtService2.setText(serviceList.get(size - 2).getTitle());
                binding.txtService3.setText(serviceList.get(size - 3).getTitle());

                setOnClickListener(binding.viewService1, serviceList.get(size - 1));
                setOnClickListener(binding.viewService2, serviceList.get(size - 2));
                setOnClickListener(binding.viewService3, serviceList.get(size - 3));
            }
        });

        binding.viewService4.setOnClickListener(v -> MainFragment.replaceFragment(new ServicesFragment()));
    }

    private void setOnClickListener(View view, ServiceModel service) {
        view.setOnClickListener(v -> {
            if (user != null) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("service", service);
                bundle.putSerializable("user", user);

                Fragment fragment = new RequestServicingFragment();
                fragment.setArguments(bundle);

                MainFragment.replaceFragment(fragment);
            } else {
                Log.e(HomeFragment.class.getSimpleName(), getString(R.string.user_not_logged_in));
            }
        });
    }
}