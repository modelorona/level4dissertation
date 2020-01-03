package com.anguel.dissertation.persistence.logevent;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
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

    public long sessionStart;
    public long sessionEnd;

//    @ColumnInfo  // this is UUID.toString(). actually unnecessary for the device database
//    public String userId;

    @ColumnInfo
    @TypeConverters(Converters.class)
    List<Map<String, String>> data;


}
