package com.habibur.breakdown_assistance.ui.viewmodels;

import static com.habibur.breakdown_assistance.BreakdownAssistance.getFirestore;
import static com.habibur.breakdown_assistance.config.Constants.GARAGES_DATABASE;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.habibur.breakdown_assistance.models.GarageModel;
import com.habibur.breakdown_assistance.models.Resource;

import java.util.List;

public class LocationsViewModel extends ViewModel {

    private final FirebaseFirestore firestore = getFirestore();

    private final MutableLiveData<Resource<List<GarageModel>>> _allGarages = new MutableLiveData<>();
    public final LiveData<Resource<List<GarageModel>>> allGarages = _allGarages;

    public LocationsViewModel() {
        getAllGarages();
    }

    private void getAllGarages() {
        _allGarages.setValue(Resource.loading());

        firestore.collection(GARAGES_DATABASE).addSnapshotListener((value, error) -> {
            if (error != null) {
                _allGarages.setValue(Resource.error(error.getMessage()));
            } else {
                if (value != null) {
                    List<GarageModel> garages = value.toObjects(GarageModel.class);
                    _allGarages.setValue(Resource.success(garages));
                }
            }
        });
    }
}
