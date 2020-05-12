package com.vladimir.rebusapp.database.tablerebuses;

import com.google.gson.annotations.SerializedName;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "rebuses")
public class Rebus {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "rebus_id")
    @SerializedName("rebus_id")
    public String rebusId;

    public String answer;

    public int score;

    public int difficulty;

}
