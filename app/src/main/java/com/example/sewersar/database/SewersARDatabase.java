package com.example.sewersar.database;

import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {SewersNode.class}, version = 1)
public abstract class SewersARDatabase extends RoomDatabase {
    public abstract SewersNodeDao sewersNodeDao();

    private static volatile SewersARDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static SewersARDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (SewersARDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), SewersARDatabase.class, "mySewersDB2.db")
                            .createFromAsset("database/sewersDB.db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
