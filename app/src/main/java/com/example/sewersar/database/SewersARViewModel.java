package com.example.sewersar.database;

import android.app.Application;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class SewersARViewModel extends AndroidViewModel {
    private SewersARRepository sewersARRepository;
    private LiveData<List<SewersNode>> mAllSewersNodes;

    public SewersARViewModel (Application application) {
        super(application);
        sewersARRepository = new SewersARRepository(application);
        mAllSewersNodes = sewersARRepository.getAllNodes();
    }

    public LiveData<List<SewersNode>> getAllNodes() { return mAllSewersNodes; }
}
