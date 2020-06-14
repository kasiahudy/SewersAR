package com.example.sewersar.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "SewersPipe")
public class SewersPipe {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "index")
    public int index;

    @NonNull
    @ColumnInfo(name = "startNodeIndex")
    public int startNodeIndex;

    @NonNull
    @ColumnInfo(name = "endNodeIndex")
    public int endNodeIndex;

    @NonNull
    @ColumnInfo(name = "color")
    public String color;
}
