package com.habibur.breakdown_assistance.adapters;

import static com.habibur.breakdown_assistance.config.Constants.REQUESTED_SERVICES_DATABASE;

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
import com.habibur.breakdown_assistance.utils.TimeUtils;

import java.util.List;

public class MyRequestedServicingAdapter extends RecyclerView.Adapter<MyRequestedServicingAdapter.MyRequestedServicingViewHolder> {

    private final Context context;
    private final List<RequestServiceModel> requestServiceList;
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public MyRequestedServicingAdapter(Context context, List<RequestServiceModel> requestServiceList) {
        this.context = context;
        this.requestServiceList = requestServiceList;
    }

    @NonNull
    @Override
    public MyRequestedServicingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_requested_servicing_list, parent, false);
        return new MyRequestedServicingViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyRequestedServicingViewHolder holder, int position) {
        RequestServiceModel requestService = requestServiceList.get(position);

        holder.service.setText(requestService.getService().getTitle());
        holder.garage.setText(requestService.getGarage().getName());
        holder.minimumPrice.setText("৳ " + requestService.getService().getMinimumPrice());
        holder.serviceCharge.setText("৳ " + requestService.getService().getServiceCharge());
        holder.time.setText(TimeUtils.convertLongToTime(context, "dd MMM yyyy, hh:mm a", requestService.getSubmitTime() != null ? requestService.getSubmitTime() : 0));
        holder.additionalInfo.setText(requestService.getAdditionalInfo() != null ? requestService.getAdditionalInfo() : context.getString(R.string.no_additional_info));
        holder.status.setText(requestService.getStatus().toString());
        holder.feedback.setText(requestService.getFeedback() != null ? requestService.getFeedback() : context.getString(R.string.no_feedback));
        holder.review.setText(requestService.getReview() != null ? requestService.getReview() : context.getString(R.string.no_review));

        if (requestService.getStatus() == RequestStatus.PENDING) {
            holder.layoutFeedback.setVisibility(View.GONE);
            holder.layoutReview.setVisibility(View.GONE);
            holder.layoutSubmitReview.setVisibility(View.GONE);
        } else {
            holder.layoutFeedback.setVisibility(View.VISIBLE);
            holder.layoutReview.setVisibility(View.VISIBLE);
            holder.layoutSubmitReview.setVisibility(View.VISIBLE);
        }

        if (requestService.getStatus() == RequestStatus.PENDING) {
            holder.layoutSubmitReview.setVisibility(View.GONE);
        } else if (requestService.getReview() != null && !requestService.getReview().isEmpty()) {
            holder.layoutReview.setVisibility(View.VISIBLE);
            holder.layoutSubmitReview.setVisibility(View.GONE);
        } else {
            holder.layoutReview.setVisibility(View.GONE);
            holder.layoutSubmitReview.setVisibility(View.VISIBLE);
        }

        holder.btnSubmit.setOnClickListener(v -> submitReview(holder, requestService, holder.editTextReview.getText().toString().trim()));
    }

    @SuppressLint("NotifyDataSetChanged")
    private void submitReview(MyRequestedServicingViewHolder holder, RequestServiceModel requestService, String review) {
        if (review.trim().isEmpty()) {
            Toast.makeText(context, R.string.please_enter_review, Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseAuth.getInstance().getUid();

        if (userId != null) {
            requestService.setReview(review);

            firestore.collection(REQUESTED_SERVICES_DATABASE)
                    .document(requestService.getId())
                    .set(requestService);

            Toast.makeText(context, R.string.review_submitted_successfully, Toast.LENGTH_SHORT).show();

            holder.editTextReview.setText("");
            holder.layoutSubmitReview.setVisibility(View.GONE);
        } else {
            Log.e(ServiceAdapter.class.getSimpleName(), context.getString(R.string.user_not_logged_in));
        }
    }

    @Override
    public int getItemCount() {
        return requestServiceList.size();
    }

    static class MyRequestedServicingViewHolder extends RecyclerView.ViewHolder {

        TextView service, garage, minimumPrice, serviceCharge, time, additionalInfo, status, feedback, review;
        EditText editTextReview;
        MaterialButton btnSubmit;
        ViewGroup layoutFeedback, layoutReview, layoutSubmitReview;

        public MyRequestedServicingViewHolder(@NonNull View itemView) {
            super(itemView);
            service = itemView.findViewById(R.id.tvService);
            garage = itemView.findViewById(R.id.tvGarage);
            minimumPrice = itemView.findViewById(R.id.tvMinimumPrice);
            serviceCharge = itemView.findViewById(R.id.tvServiceCharge);
            time = itemView.findViewById(R.id.tvTime);
            additionalInfo = itemView.findViewById(R.id.tvAdditionalInfo);
            status = itemView.findViewById(R.id.tvStatus);
            feedback = itemView.findViewById(R.id.tvFeedback);
            review = itemView.findViewById(R.id.tvReview);
            editTextReview = itemView.findViewById(R.id.editTextReview);
            btnSubmit = itemView.findViewById(R.id.btnSubmit);
            layoutFeedback = itemView.findViewById(R.id.linearLayoutFeedback);
            layoutReview = itemView.findViewById(R.id.linearLayoutReview);
            layoutSubmitReview = itemView.findViewById(R.id.linearLayoutSubmitReview);
        }
    }
}
