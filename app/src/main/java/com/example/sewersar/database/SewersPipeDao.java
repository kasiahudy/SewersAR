package com.example.sewersar.database;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

@Dao
public interface SewersPipeDao {
    @Query("SELECT * FROM SewersPipe")
    LiveData<List<SewersPipe>> getAll();
}
