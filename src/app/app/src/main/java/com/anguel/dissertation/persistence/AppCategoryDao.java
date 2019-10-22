package com.anguel.dissertation.persistence;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AppCategoryDao {

    @Query("SELECT * FROM AppCategory")
    List<AppCategory> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAppCategory(AppCategory appCategory);

    @Delete
    void delete(AppCategory appCategory);
}
