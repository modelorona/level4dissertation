package com.anguel.dissertation.persistence;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {AppCategory.class}, version = 1)
public abstract class AppCategoryDatabase extends RoomDatabase {
    public abstract AppCategoryDao appCategoryDao();

    private static volatile AppCategoryDatabase INSTANCE;

    public static AppCategoryDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (LogEventDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppCategoryDatabase.class, "AppCategory.db").build();
                }
            }
        }
        return INSTANCE;
    }
}
