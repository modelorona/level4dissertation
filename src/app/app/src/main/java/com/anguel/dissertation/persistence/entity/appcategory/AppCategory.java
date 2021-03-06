package com.anguel.dissertation.persistence.entity.appcategory;

import androidx.annotation.NonNull;
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
@Entity(tableName = "AppCategory", primaryKeys = {"appName", "packageName"})
@SuppressWarnings("NullableProblems")
public class AppCategory {

    @NonNull
    String appName;

    @ColumnInfo
    String category;

    @ColumnInfo @NonNull
    String packageName;
}
