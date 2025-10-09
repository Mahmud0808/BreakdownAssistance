package com.habibur.breakdown_assistance.adapters;

import static com.habibur.breakdown_assistance.config.Constants.REQUESTED_SERVICES_DATABASE;
import static com.habibur.breakdown_assistance.config.Constants.USER_DATABASE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.habibur.breakdown_assistance.R;
import com.habibur.breakdown_assistance.models.RequestServiceModel;
import com.habibur.breakdown_assistance.models.RequestStatus;
import com.habibur.breakdown_assistance.models.UserModel;
import com.habibur.breakdown_assistance.utils.TimeUtils;

import java.util.List;
import java.util.Objects;

public class ServicingRequestAdapter extends RecyclerView.Adapter<ServicingRequestAdapter.ServicingHistoryViewHolder> {

    private final Context context;
    private final List<RequestServiceModel> requestServiceList;
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public ServicingRequestAdapter(Context context, List<RequestServiceModel> requestServiceList) {
        this.context = context;
        this.requestServiceList = requestServiceList;
    }

    @NonNull
    @Override
    public ServicingHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_servicing_request_list, parent, false);
        return new ServicingHistoryViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ServicingHistoryViewHolder holder, int position) {
        RequestServiceModel requestService = requestServiceList.get(position);

        holder.service.setText(requestService.getService().getTitle());
        holder.requestedBy.setText(requestService.getRequestedBy().getName() + " (" + requestService.getRequestedBy().getPhone() + ")");
        holder.garage.setText(requestService.getGarage().getName());
        holder.minimumPrice.setText("৳ " + requestService.getService().getMinimumPrice());
        holder.serviceCharge.setText("৳ " + requestService.getService().getServiceCharge());
        holder.time.setText(TimeUtils.convertLongToTime("dd MMM yyyy, hh:mm a", requestService.getSubmitTime() != null ? requestService.getSubmitTime() : 0));
        holder.additionalInfo.setText(requestService.getAdditionalInfo() != null ? requestService.getAdditionalInfo() : "No Additional Info");

        holder.btnAccept.setOnClickListener(v -> updateServiceStatus(requestService, RequestStatus.ACCEPTED, holder.editTextFeedback.getText().toString().trim()));
        holder.btnReject.setOnClickListener(v -> updateServiceStatus(requestService, RequestStatus.REJECTED, holder.editTextFeedback.getText().toString().trim()));
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateServiceStatus(RequestServiceModel requestService, RequestStatus requestStatus, String feedback) {
        String userId = FirebaseAuth.getInstance().getUid();

        if (userId != null) {
            firestore.collection(USER_DATABASE)
                    .document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            UserModel user = documentSnapshot.toObject(UserModel.class);

                            requestService.setStatus(requestStatus);
                            requestService.setAssignedTo(user);
                            requestService.setFeedback(feedback);

                            firestore.collection(REQUESTED_SERVICES_DATABASE)
                                    .document(requestService.getId())
                                    .set(requestService);

                            Toast.makeText(context, "Status updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(ServiceAdapter.class.getSimpleName(), "User not found");
                        }
                    })
                    .addOnFailureListener(e -> Log.e(ServiceAdapter.class.getSimpleName(), Objects.requireNonNull(e.getMessage())));
        } else {
            Log.e(ServiceAdapter.class.getSimpleName(), "User not logged in");
        }
    }

    @Override
    public int getItemCount() {
        return requestServiceList.size();
    }

    static class ServicingHistoryViewHolder extends RecyclerView.ViewHolder {

        TextView service, requestedBy, garage, minimumPrice, serviceCharge, time, additionalInfo;
        EditText editTextFeedback;
        MaterialButton btnAccept, btnReject;

        public ServicingHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            service = itemView.findViewById(R.id.tvService);
            requestedBy = itemView.findViewById(R.id.tvRequestedBy);
            garage = itemView.findViewById(R.id.tvGarage);
            minimumPrice = itemView.findViewById(R.id.tvMinimumPrice);
            serviceCharge = itemView.findViewById(R.id.tvServiceCharge);
            time = itemView.findViewById(R.id.tvTime);
            additionalInfo = itemView.findViewById(R.id.tvAdditionalInfo);
            editTextFeedback = itemView.findViewById(R.id.editTextFeedback);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}
