package com.vladimir.rebusapp.database.levels;

import com.vladimir.rebusapp.database.rebuses.Rebus;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import static com.vladimir.rebusapp.utils.Constants.REBUSES_QUANTITY_FOR_LEVEL;

@Entity(tableName = "levels")
public class Level {

    @ColumnInfo(name = "level_index")
    @PrimaryKey
    public int levelIndex;

    @ColumnInfo(name = "rebus_id")
    @ForeignKey(
            entity = Rebus.class,
            parentColumns = "rebus_id",
            childColumns = "rebus_id"
    )
    public String rebusId;

    @Ignore public int getLevel() {
        return levelIndex / REBUSES_QUANTITY_FOR_LEVEL;
    }

    @Ignore public int getSubLevel() {
        return levelIndex % REBUSES_QUANTITY_FOR_LEVEL;
    }

    public Level(int levelIndex, String rebusId) {
        this.levelIndex = levelIndex;
        this.rebusId = rebusId;
    }
}
