package com.anguel.dissertation.persistence.database.appcategory;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AppCategoryDao {

// --Commented out by Inspection START (1/10/20 1:52 AM):
//    @Query("SELECT * FROM AppCategory")
//    List<AppCategory> getAll();
// --Commented out by Inspection STOP (1/10/20 1:52 AM)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAppCategory(AppCategory appCategory);

}
