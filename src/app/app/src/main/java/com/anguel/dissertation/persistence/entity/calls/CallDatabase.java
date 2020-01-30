package com.anguel.dissertation.persistence.entity.calls;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.anguel.dissertation.R;

@Database(entities = {Call.class}, version = 1)
public abstract class CallDatabase extends RoomDatabase {
    public abstract CallDao callDao();

    private static volatile CallDatabase INSTANCE;

    public static CallDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (CallDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), CallDatabase.class, context.getString(R.string.call_db)).build();
                }
            }
        }
        return INSTANCE;
    }

}
