package com.anguel.dissertation.persistence.entity.calls;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CallDao {

    @Query("SELECT * FROM Calls")
    List<Call> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCallEvent(Call call);
}
