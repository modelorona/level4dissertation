package com.anguel.dissertation.persistence.userdata;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDataDao {
    @Query("SELECT * FROM UserData")
    List<UserData> getAll();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertUserData(UserData userData);
}
