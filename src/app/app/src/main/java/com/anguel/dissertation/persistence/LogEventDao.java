package com.anguel.dissertation.persistence;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LogEventDao {

    @Query("SELECT * FROM LogEvents")
    List<LogEvent> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLogEvent(LogEvent logEvent);

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    void insertLogEvents(List<LogEvent> logEvents);

    @Delete
    void delete(LogEvent logEvent);
}
