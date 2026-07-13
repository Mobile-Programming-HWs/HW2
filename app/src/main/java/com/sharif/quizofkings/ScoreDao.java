package com.sharif.quizofkings;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ScoreDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Score score);

    @Query("SELECT * FROM scores ORDER BY score DESC LIMIT 5")
    List<Score> getOrderedScores();

    @Query("SELECT * FROM scores ORDER BY score DESC LIMIT 1")
    Score getTopScore();
}
