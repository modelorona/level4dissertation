package com.anguel.dissertation.persistence.database.location;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity(tableName = "Locations")
public class Location {

    @PrimaryKey(autoGenerate = true)
    int id;

    @ColumnInfo
    double altitude;

    @ColumnInfo
    float hAccuracy;

    @ColumnInfo
    float vAccuracy;

    @ColumnInfo
    float bearing;

    @ColumnInfo
    float bearingAccuracy;

    @ColumnInfo
    double latitude;

    @ColumnInfo
    double longitude;

    @ColumnInfo
    float speed;

    @ColumnInfo
    float speedAccuracy;

    @ColumnInfo
    long timeNanos;

    @ColumnInfo
    String provider;


}
