package com.habibur.breakdown_assistance.ui.fragments;

import static com.habibur.breakdown_assistance.config.Constants.USER_DATABASE;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.habibur.breakdown_assistance.R;
import com.habibur.breakdown_assistance.adapters.UserAdapter;
import com.habibur.breakdown_assistance.databinding.FragmentUsersBinding;
import com.habibur.breakdown_assistance.models.AccountType;
import com.habibur.breakdown_assistance.models.UserModel;
import com.habibur.breakdown_assistance.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UsersFragment extends BaseFragment {

    private FragmentUsersBinding binding;
    private UserAdapter adapter;
    private final List<UserModel> userList = new ArrayList<>();
    private final List<UserModel> fullUserList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUsersBinding.inflate(inflater, container, false);

        ViewUtils.setToolbarTitle(requireContext(), binding.header.toolbar, R.string.users, true);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new UserAdapter(requireContext(), userList);
        binding.recyclerView.setAdapter(adapter);

        setupSpinner();

        fetchUsers();

        return binding.getRoot();
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(), R.array.user_filter_options, R.layout.dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerFilter.setAdapter(adapter);
        binding.spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                AccountType accountType = null;
                switch (selectedItem) {
                    case "User":
                        accountType = AccountType.USER;
                        break;
                    case "Mechanic":
                        accountType = AccountType.MECHANIC;
                        break;
                    case "Admin":
                        accountType = AccountType.ADMIN;
                        break;
                }
                filterUsers(accountType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                filterUsers(null);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchUsers() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection(USER_DATABASE).addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.d(UsersFragment.class.getSimpleName(), Objects.requireNonNull(error.getMessage()));
                return;
            }

            fullUserList.clear();
            userList.clear();

            if (value != null) {
                for (DocumentSnapshot doc : value) {
                    UserModel user = doc.toObject(UserModel.class);
                    fullUserList.add(user);
                }
                userList.addAll(fullUserList);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void filterUsers(AccountType accountType) {
        adapter.filterList(accountType, fullUserList);
    }
}