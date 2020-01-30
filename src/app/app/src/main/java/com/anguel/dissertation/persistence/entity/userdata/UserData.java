package com.anguel.dissertation.persistence.entity.userdata;

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
@Entity(tableName = "UserData")
public class UserData {

    @SuppressWarnings("NullableProblems")
    @PrimaryKey @NonNull
    String userId;

    @ColumnInfo
    int sias;
}
