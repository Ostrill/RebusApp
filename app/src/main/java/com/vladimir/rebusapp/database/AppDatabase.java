package com.vladimir.rebusapp.database;

import com.vladimir.rebusapp.database.levels.Level;
import com.vladimir.rebusapp.database.levels.LevelDao;
import com.vladimir.rebusapp.database.rebuses.Rebus;
import com.vladimir.rebusapp.database.rebuses.RebusDao;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(
        entities = {Level.class, Rebus.class},
        version = 2,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    public abstract LevelDao levelDao();
    public abstract RebusDao rebusDao();
}