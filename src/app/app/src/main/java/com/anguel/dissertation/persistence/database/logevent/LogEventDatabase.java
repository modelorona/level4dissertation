package com.anguel.dissertation.persistence.database.logevent;

import android.content.Context;

import com.anguel.dissertation.R;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {LogEvent.class}, version = 1)
@TypeConverters(Converters.class)
public abstract class LogEventDatabase extends RoomDatabase {
    public abstract LogEventDao logEventDao();

    private static volatile LogEventDatabase INSTANCE;

    public static LogEventDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (LogEventDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), LogEventDatabase.class, context.getString(R.string.log_events_db)).build();
                }
            }
        }
        return INSTANCE;
    }
}
