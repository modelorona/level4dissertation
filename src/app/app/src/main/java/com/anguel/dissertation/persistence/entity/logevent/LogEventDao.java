package com.anguel.dissertation.persistence.entity.logevent;

import androidx.room.Dao;
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

// --Commented out by Inspection START (1/10/20 1:52 AM):
//    @Delete
//    void delete(LogEvent logEvent);
// --Commented out by Inspection STOP (1/10/20 1:52 AM)
}
