package com.habibur.breakdown_assistance.adapters;

import static com.habibur.breakdown_assistance.config.Constants.USER_DATABASE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.habibur.breakdown_assistance.R;
import com.habibur.breakdown_assistance.models.AccountType;
import com.habibur.breakdown_assistance.models.UserModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final Context context;
    private final List<UserModel> userList;
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public UserAdapter(Context context, List<UserModel> userList) {
        this.context = context;
        this.userList = userList;
    }


    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_list, parent, false);
        return new UserViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        UserModel user = userList.get(position);

        holder.name.setText(user.getName());
        holder.phone.setText(user.getPhone());

        String accountType = (user.getAccountType() == AccountType.USER) ? context.getString(R.string.user) : ((user.getAccountType() == AccountType.MECHANIC) ? context.getString(R.string.mechanic) : context.getString(R.string.admin));
        holder.account.setText(accountType);

        holder.btnUser.setOnClickListener(v -> changeAccountType(user.getId(), AccountType.USER));
        holder.btnMechanic.setOnClickListener(v -> changeAccountType(user.getId(), AccountType.MECHANIC));
        holder.btnAdmin.setOnClickListener(v -> changeAccountType(user.getId(), AccountType.ADMIN));

        if (user.getAccountType() == AccountType.USER) {
            holder.btnUser.setEnabled(false);
            holder.btnUser.setAlpha(0.6f);

            holder.btnMechanic.setEnabled(true);
            holder.btnMechanic.setAlpha(1f);

            holder.btnAdmin.setEnabled(true);
            holder.btnAdmin.setAlpha(1f);
        }
        else if (user.getAccountType() == AccountType.MECHANIC) {
            holder.btnMechanic.setEnabled(false);
            holder.btnMechanic.setAlpha(0.6f);

            holder.btnUser.setEnabled(true);
            holder.btnUser.setAlpha(1f);

            holder.btnAdmin.setEnabled(true);
            holder.btnAdmin.setAlpha(1f);
        }
        else if (user.getAccountType() == AccountType.ADMIN) {
            holder.btnAdmin.setEnabled(false);
            holder.btnAdmin.setAlpha(0.6f);

            holder.btnUser.setEnabled(true);
            holder.btnUser.setAlpha(1f);

            holder.btnMechanic.setEnabled(true);
            holder.btnMechanic.setAlpha(1f);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void changeAccountType(String userId, AccountType accountType) {
        firestore.collection(USER_DATABASE).document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        UserModel user = documentSnapshot.toObject(UserModel.class);

                        if (user != null) {
                            user.setAccountType(accountType);
                            firestore.collection(USER_DATABASE).document(userId).set(user);

                            if (userId.equals(FirebaseAuth.getInstance().getUid())) {
                                Toast.makeText(context, R.string.logout_and_login_again_to_see_changes, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, R.string.account_type_changed_successfully, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Log.e(UserAdapter.class.getSimpleName(), context.getString(R.string.user_not_found));
                    }
                })
                .addOnFailureListener(e -> Log.e(UserAdapter.class.getSimpleName(), Objects.requireNonNull(e.getMessage())));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {

        TextView name, phone, account;
        MaterialButton btnUser, btnMechanic, btnAdmin;

        public UserViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvName);
            phone = itemView.findViewById(R.id.tvPhone);
            account = itemView.findViewById(R.id.tvAccount);
            btnUser = itemView.findViewById(R.id.btnMakeUser);
            btnMechanic = itemView.findViewById(R.id.btnMakeMechanic);
            btnAdmin = itemView.findViewById(R.id.btnMakeAdmin);
        }
    }

    public void filterList(AccountType accountType, List<UserModel> userList) {
        List<UserModel> filteredList = new ArrayList<>();
        for (UserModel user : userList) {
            if (accountType == null || user.getAccountType() == accountType) {
                filteredList.add(user);
            }
        }
        updateList(filteredList);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<UserModel> newList) {
        userList.clear();
        userList.addAll(newList);
        notifyDataSetChanged();
    }
}
