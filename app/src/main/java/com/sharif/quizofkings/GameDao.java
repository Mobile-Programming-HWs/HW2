package com.sharif.quizofkings;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface GameDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Game game);

    @Query("SELECT * FROM games WHERE userEmail = :userEmail ORDER BY RANDOM() LIMIT 1")
    Game getGame(String userEmail);

    @Query("SELECT * FROM games WHERE id = :id")
    Game getGameById(int id);

    @Query("SELECT IFNULL(MAX(id), 0) + 1 FROM games")
    int getNextGameId();

    @Query("DELETE FROM logged_in_user")
    void deleteAll();
}
