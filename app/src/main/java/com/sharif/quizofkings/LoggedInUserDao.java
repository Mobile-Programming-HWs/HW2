package com.sharif.quizofkings;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface LoggedInUserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(LoggedInUser user);

    @Query("DELETE FROM logged_in_user")
    void deleteAll();

    @Query("SELECT * FROM logged_in_user LIMIT 1")
    LoggedInUser user();
}
