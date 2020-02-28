package com.anguel.dissertation.persistence.entity.location;

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
    long systemTimestamp; // represents when the timestamp was received

    @ColumnInfo
    long locationTimestamp; // the UTC timestamp, not monotonic

    @ColumnInfo
    long elapsedNanosSinceBoot; // this is the https://developer.android.com/reference/android/os/SystemClock#elapsedRealtimeNanos() method

    @ColumnInfo
    long elapsedNanosLocation; // this is the https://developer.android.com/reference/android/location/Location#getElapsedRealtimeNanos()

    @ColumnInfo
    String provider;


}
