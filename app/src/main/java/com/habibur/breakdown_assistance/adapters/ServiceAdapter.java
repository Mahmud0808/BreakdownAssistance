package com.habibur.breakdown_assistance.adapters;

import static com.habibur.breakdown_assistance.config.Constants.USER_DATABASE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.habibur.breakdown_assistance.R;
import com.habibur.breakdown_assistance.models.ServiceModel;
import com.habibur.breakdown_assistance.models.UserModel;
import com.habibur.breakdown_assistance.ui.fragments.MainFragment;
import com.habibur.breakdown_assistance.ui.fragments.RequestServicingFragment;

import java.util.List;
import java.util.Objects;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private final Context context;
    private final List<ServiceModel> serviceList;

    public ServiceAdapter(Context context, List<ServiceModel> serviceList) {
        this.context = context;
        this.serviceList = serviceList;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_service_list, parent, false);
        return new ServiceViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        ServiceModel service = serviceList.get(position);

        RequestOptions reqOptions = new RequestOptions()
                .centerCrop()
                .override(300, 300);

        Glide.with(context)
                .asBitmap()
                .apply(reqOptions)
                .placeholder(R.drawable.img_loading)
                .error(R.drawable.img_error)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .load(service.getImage())
                .into(holder.image);

        holder.title.setText(service.getTitle());
        holder.minimumPrice.setText("৳ " + service.getMinimumPrice());

        holder.btnNext.setOnClickListener(v -> {
            String userId = FirebaseAuth.getInstance().getUid();

            if (userId != null) {
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();

                firestore.collection(USER_DATABASE).document(userId)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                UserModel user = documentSnapshot.toObject(UserModel.class);

                                Bundle bundle = new Bundle();
                                bundle.putSerializable("service", service);
                                bundle.putSerializable("user", user);

                                Fragment fragment = new RequestServicingFragment();
                                fragment.setArguments(bundle);

                                MainFragment.replaceFragment(fragment);
                            } else {
                                Log.e(ServiceAdapter.class.getSimpleName(), context.getString(R.string.user_not_found));
                            }
                        })
                        .addOnFailureListener(e -> Log.e(ServiceAdapter.class.getSimpleName(), Objects.requireNonNull(e.getMessage())));
            } else {
                Log.e(ServiceAdapter.class.getSimpleName(), context.getString(R.string.user_not_logged_in));
            }
        });
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {

        TextView title, minimumPrice;
        ImageView image;
        MaterialButton btnNext;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTitle);
            minimumPrice = itemView.findViewById(R.id.tvMinimumPrice);
            image = itemView.findViewById(R.id.imgService);
            btnNext = itemView.findViewById(R.id.btnNext);
        }
    }
}
