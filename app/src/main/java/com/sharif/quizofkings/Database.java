package com.sharif.quizofkings;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@androidx.room.Database(entities = {User.class, Score.class, Game.class, LoggedInUser.class}, version = 9, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class Database extends RoomDatabase {
    public abstract UserDao UserDao();
    public abstract GameDao GameDao();
    public abstract ScoreDao ScoreDao();
    public abstract LoggedInUserDao LoggedInUserDao();

    private static volatile Database INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);


    static Database getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (Database.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            Database.class, "database")
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }


}
