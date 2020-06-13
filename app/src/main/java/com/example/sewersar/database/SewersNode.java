package com.example.sewersar.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity (tableName = "SewersNode")
public class SewersNode {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "name")
    public String name;

    @NonNull
    @ColumnInfo(name = "lon")
    public double lon;

    @NonNull
    @ColumnInfo(name = "lat")
    public double lat;

    @NonNull
    @ColumnInfo(name = "color")
    public String color;
}
