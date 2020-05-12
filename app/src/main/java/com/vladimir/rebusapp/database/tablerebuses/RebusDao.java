package com.vladimir.rebusapp.database.tablerebuses;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface RebusDao {

    @Query(
            "DELETE FROM rebuses " +
            "WHERE score = 0"
    )
    void clearUnsolved();

    @Query(
            "SELECT EXISTS(" +
            "SELECT * " +
            "FROM rebuses " +
            "WHERE rebus_id = :rebusId" +
            ")"
    )
    boolean isRebusExists(String rebusId);

    @Query(
            "SELECT score " +
            "FROM rebuses " +
            "WHERE rebus_id = :rebusId"
    )
    int getScoreById(String rebusId);

//    @Query(
//            "SELECT count(score) " +
//            "FROM rebuses " +
//            "WHERE score <> 0"
//    )
//    int getSolvedQuantity();

    @Query(
            "UPDATE rebuses " +
            "SET score = :score " +
            "WHERE rebus_id = :rebusId"
    )
    void solveRebus(String rebusId, int score);

    @Query("UPDATE rebuses SET score = 0")
    void resetSolved();

    @Insert
    void insertNewRebus(Rebus rebus);
}
