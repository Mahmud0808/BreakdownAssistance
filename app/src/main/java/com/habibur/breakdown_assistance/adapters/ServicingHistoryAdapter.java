package com.habibur.breakdown_assistance.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.habibur.breakdown_assistance.R;
import com.habibur.breakdown_assistance.models.RequestServiceModel;
import com.habibur.breakdown_assistance.utils.TimeUtils;

import java.util.List;

public class ServicingHistoryAdapter extends RecyclerView.Adapter<ServicingHistoryAdapter.ServicingHistoryViewHolder> {

    private final Context context;
    private final List<RequestServiceModel> requestServiceList;
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public ServicingHistoryAdapter(Context context, List<RequestServiceModel> requestServiceList) {
        this.context = context;
        this.requestServiceList = requestServiceList;
    }

    @NonNull
    @Override
    public ServicingHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_servicing_history_list, parent, false);
        return new ServicingHistoryViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ServicingHistoryViewHolder holder, int position) {
        RequestServiceModel requestService = requestServiceList.get(position);

        holder.service.setText(requestService.getService().getTitle());
        holder.requestedBy.setText(requestService.getRequestedBy().getName() + " (" + requestService.getRequestedBy().getPhone() + ")");
        holder.garage.setText(requestService.getGarage().getName());
        holder.status.setText(requestService.getStatus().toString());
        holder.assignedTo.setText(requestService.getAssignedTo() != null ? requestService.getAssignedTo().getName() + " (" + requestService.getAssignedTo().getPhone() + ")" : "Not Assigned");
        holder.feedback.setText(requestService.getFeedback() != null ? requestService.getFeedback() : "No Feedback");
        holder.review.setText(requestService.getReview() != null ? requestService.getReview() : "No Review");
        holder.time.setText(TimeUtils.convertLongToTime("dd MMM yyyy, hh:mm a", requestService.getSubmitTime() != null ? requestService.getSubmitTime() : 0));
    }

    @Override
    public int getItemCount() {
        return requestServiceList.size();
    }

    static class ServicingHistoryViewHolder extends RecyclerView.ViewHolder {

        TextView service, requestedBy, garage, status, assignedTo, feedback, review, time;

        public ServicingHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            service = itemView.findViewById(R.id.tvService);
            requestedBy = itemView.findViewById(R.id.tvRequestedBy);
            garage = itemView.findViewById(R.id.tvGarage);
            status = itemView.findViewById(R.id.tvStatus);
            assignedTo = itemView.findViewById(R.id.tvAssignedTo);
            feedback = itemView.findViewById(R.id.tvFeedback);
            review = itemView.findViewById(R.id.tvReview);
            time = itemView.findViewById(R.id.tvTime);
        }
    }
}
