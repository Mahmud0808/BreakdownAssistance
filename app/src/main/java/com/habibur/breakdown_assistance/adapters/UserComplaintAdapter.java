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
import com.habibur.breakdown_assistance.models.ComplaintModel;

import java.util.List;

public class UserComplaintAdapter extends RecyclerView.Adapter<UserComplaintAdapter.UserComplaintViewHolder> {

    private final Context context;
    private final List<ComplaintModel> complaintList;
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public UserComplaintAdapter(Context context, List<ComplaintModel> complaintList) {
        this.context = context;
        this.complaintList = complaintList;
    }

    @NonNull
    @Override
    public UserComplaintViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_complaint_list, parent, false);
        return new UserComplaintViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull UserComplaintViewHolder holder, int position) {
        ComplaintModel complaint = complaintList.get(position);

        holder.submittedBy.setText(complaint.getSubmittedBy().getName());
        holder.contact.setText(complaint.getContactInfo());
        holder.description.setText(complaint.getDescription());
    }

    @Override
    public int getItemCount() {
        return complaintList.size();
    }

    static class UserComplaintViewHolder extends RecyclerView.ViewHolder {

        TextView submittedBy, contact, description;

        public UserComplaintViewHolder(@NonNull View itemView) {
            super(itemView);
            submittedBy = itemView.findViewById(R.id.tvSubmittedBy);
            contact = itemView.findViewById(R.id.tvContact);
            description = itemView.findViewById(R.id.tvDescription);
        }
    }
}
