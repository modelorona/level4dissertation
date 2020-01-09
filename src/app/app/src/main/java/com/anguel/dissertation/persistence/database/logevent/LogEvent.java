package com.anguel.dissertation.persistence.database.logevent;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.TypeConverters;

import java.util.List;
import java.util.Map;

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
@Entity(tableName = "LogEvents", primaryKeys = {"sessionStart", "sessionEnd"})
public class LogEvent {

    long sessionStart;
    long sessionEnd;


    @ColumnInfo
    @TypeConverters(Converters.class)
    List<Map<String, String>> data;


}
