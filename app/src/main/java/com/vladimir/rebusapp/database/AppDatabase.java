package com.vladimir.rebusapp.database;

import com.vladimir.rebusapp.database.tablelevels.Level;
import com.vladimir.rebusapp.database.tablelevels.LevelDao;
import com.vladimir.rebusapp.database.tablerebuses.Rebus;
import com.vladimir.rebusapp.database.tablerebuses.RebusDao;

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