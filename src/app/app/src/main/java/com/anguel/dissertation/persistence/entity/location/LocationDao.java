package com.anguel.dissertation.persistence.entity.location;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LocationDao {

    @Query("SELECT * FROM Locations")
    List<Location> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLocationEvent(Location location);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLocations(List<Location> locations);
}
