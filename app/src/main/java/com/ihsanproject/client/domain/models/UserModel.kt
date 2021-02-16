package com.ihsanproject.client.domain.models

import androidx.room.*


@Entity(tableName = "user")
data class UserModel(
    @PrimaryKey val id: Int,
    @ColumnInfo val first_name: String,
    @ColumnInfo val email: String,
    @ColumnInfo val access: String
)
