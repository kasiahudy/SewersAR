package com.example.sewersar.database;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

@Dao
public interface SewersNodeDao {
    @Query("SELECT * FROM sewersNode")
    LiveData<List<SewersNode>> getAll();

}

