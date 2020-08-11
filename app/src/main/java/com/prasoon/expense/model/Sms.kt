package com.prasoon.expense.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Sms(
    @PrimaryKey val id:Long,
    @ColumnInfo val address: String,
    @ColumnInfo val time: String,
    @ColumnInfo val body: String
)