package com.vladimir.rebusapp.database.tablelevels;

import com.vladimir.rebusapp.database.tablerebuses.Rebus;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import io.reactivex.Flowable;

@Dao
public interface LevelDao {

//    @Query(
//            "SELECT (level_index + 1) " +
//            "FROM levels " +
//            "ORDER BY level_index DESC " +
//            "LIMIT 1"
//    )
//    int getRebusQuantity();
//
//    @Query(
//            "SELECT l.rebus_id, answer, score, difficulty " +
//            "FROM levels l " +
//            "JOIN rebuses r ON l.rebus_id = r.rebus_id " +
//            "GROUP BY level_index"
//    )
//    List<Rebus> getListOfAllRebuses();

    @Query(
            "SELECT l.rebus_id, answer, score, difficulty " +
            "FROM levels l " +
            "JOIN rebuses r ON l.rebus_id = r.rebus_id " +
            "GROUP BY level_index"
    )
    Flowable<List<Rebus>> getAllRebuses();

    @Query("DELETE FROM levels")
    void clearTable();

    @Insert
    void addNewLevel(Level level);

}
