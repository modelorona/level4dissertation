package com.anguel.dissertation.persistence.database.userdata;

import android.content.Context;

import com.anguel.dissertation.R;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {UserData.class}, version = 1)
public abstract class UserDataDatabase extends RoomDatabase {
    public abstract UserDataDao userDataDao();

    private static volatile UserDataDatabase INSTANCE;

    public static UserDataDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (UserDataDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), UserDataDatabase.class, context.getString(R.string.userdata_db)).build();
                }
            }
        }
        return INSTANCE;
    }
}
