package com.anguel.dissertation.persistence.database.location;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.anguel.dissertation.R;

@Database(entities = {Location.class}, version = 1)
public abstract class LocationDatabase extends RoomDatabase {
    public abstract LocationDao locationDao();

    private static volatile LocationDatabase INSTANCE;

    public static LocationDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (LocationDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), LocationDatabase.class, context.getString(R.string.location_db)).build();
                }
            }
        }
        return INSTANCE;
    }
}
