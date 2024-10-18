package com.habibur.breakdown_assistance.ui.viewmodels;

import static com.habibur.breakdown_assistance.config.Constants.SERVICES_DATABASE;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.habibur.breakdown_assistance.models.Resource;
import com.habibur.breakdown_assistance.models.ServiceModel;

import java.util.List;

public class ServicesViewModel extends ViewModel {

    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private final MutableLiveData<Resource<List<ServiceModel>>> _allServices = new MutableLiveData<>();
    public final LiveData<Resource<List<ServiceModel>>> allServices = _allServices;

    public ServicesViewModel() {
        getAllServices();
    }

    private void getAllServices() {
        _allServices.setValue(Resource.loading());

        firestore.collection(SERVICES_DATABASE).addSnapshotListener((value, error) -> {
            if (error != null) {
                _allServices.setValue(Resource.error(error.getMessage()));
            } else {
                if (value != null) {
                    List<ServiceModel> services = value.toObjects(ServiceModel.class);
                    _allServices.setValue(Resource.success(services));
                }
            }
        });
    }
}
