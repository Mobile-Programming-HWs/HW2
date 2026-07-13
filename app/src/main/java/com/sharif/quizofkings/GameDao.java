package com.sharif.quizofkings;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GameDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Game game);

    @Query("SELECT * FROM games WHERE userEmail = :userEmail AND difficulty = :difficulty AND category = :category AND numberOfQuestions = :numberOfQuestions ORDER BY id DESC")
    List<Game> getGames(String userEmail, String difficulty, int category, int numberOfQuestions);

    @Query("SELECT * FROM games WHERE id = :id")
    Game getGameById(int id);

    @Query("SELECT IFNULL(MAX(id), 0) + 1 FROM games")
    int getNextGameId();

    @Query("DELETE FROM games")
    void deleteAll();
}
