package com.prasoon.expense.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.prasoon.expense.model.Category

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category): Long

    @Query("SELECT * FROM category_item ORDER BY id DESC")
    suspend fun getAllCategory(): List<Category>

    @Query("DELETE FROM category_item WHERE id = :id")
    suspend fun deleteByUserId(id: Long)

    @Query("UPDATE category_item SET totalExpense = :total WHERE id = :id")
    suspend fun updateTotalById(id: Long, total: Double)

    @Query("SELECT totalExpense FROM category_item WHERE id = :id")
    suspend fun getTotalById(id: Long): Double

}