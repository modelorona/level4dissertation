package com.anguel.dissertation.persistence.logevent;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.anguel.dissertation.persistence.logevent.Converters;

@Database(entities = {LogEvent.class}, version = 1)
@TypeConverters(Converters.class)
public abstract class LogEventDatabase extends RoomDatabase {
    public abstract LogEventDao logEventDao();

    private static volatile LogEventDatabase INSTANCE;

    public static LogEventDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (LogEventDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), LogEventDatabase.class, "LogEvents.db").build();
                }
            }
        }
        return INSTANCE;
    }
}
