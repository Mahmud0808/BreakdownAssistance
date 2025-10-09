package com.habibur.breakdown_assistance.ui.fragments;

import static com.habibur.breakdown_assistance.config.Constants.USER_DATABASE;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.habibur.breakdown_assistance.R;
import com.habibur.breakdown_assistance.adapters.UserAdapter;
import com.habibur.breakdown_assistance.databinding.FragmentUsersBinding;
import com.habibur.breakdown_assistance.models.UserModel;
import com.habibur.breakdown_assistance.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UsersFragment extends BaseFragment {

    private FragmentUsersBinding binding;
    private UserAdapter adapter;
    private final List<UserModel> userList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUsersBinding.inflate(inflater, container, false);

        ViewUtils.setToolbarTitle(requireContext(), binding.header.toolbar, R.string.users, true);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new UserAdapter(requireContext(), userList);
        binding.recyclerView.setAdapter(adapter);

        fetchUsers();

        return binding.getRoot();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchUsers() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection(USER_DATABASE).addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.d(UsersFragment.class.getSimpleName(), Objects.requireNonNull(error.getMessage()));
                return;
            }

            userList.clear();

            if (value != null) {
                for (DocumentSnapshot doc : value) {
                    UserModel user = doc.toObject(UserModel.class);
                    userList.add(user);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}