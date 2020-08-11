package com.prasoon.expense.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.prasoon.expense.model.Budget
import com.prasoon.expense.model.Sms

@Dao
interface NotificationDao {

    @Query("SELECT * FROM sms ORDER BY id DESC")
    suspend fun getNotifications(): List<Sms>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotificationList(smsList: List<Sms>)

}