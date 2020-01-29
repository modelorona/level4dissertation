package com.anguel.dissertation.persistence.database.calls;

import androidx.room.Entity;

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
@Entity(tableName = "Calls", primaryKeys = {"startTime", "endTime"})
public class Call {

    long startTime;
    long endTime;

}
