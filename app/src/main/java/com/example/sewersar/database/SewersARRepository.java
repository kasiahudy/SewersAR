package com.example.sewersar.database;

import android.app.Application;
import android.content.Context;

import java.util.List;

import androidx.lifecycle.LiveData;

public class SewersARRepository {
    private SewersNodeDao mSewersNodeDao;
    private SewersPipeDao mSewersPipeDao;
    private LiveData<List<SewersNode>> sewersNodes;
    private LiveData<List<SewersPipe>> sewersPipes;

    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public SewersARRepository(Application application) {
        SewersARDatabase db = SewersARDatabase.getDatabase(application);
        mSewersNodeDao = db.sewersNodeDao();
        sewersNodes = mSewersNodeDao.getAll();
        mSewersPipeDao = db.sewersPipeDao();
        sewersPipes = mSewersPipeDao.getAll();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<SewersNode>> getAllNodes() {
        return sewersNodes;
    }

    public LiveData<List<SewersPipe>> getAllPipes() {
        return sewersPipes;
    }

}
