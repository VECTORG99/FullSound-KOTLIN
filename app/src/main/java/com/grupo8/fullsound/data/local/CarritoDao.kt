package com.grupo8.fullsound.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.grupo8.fullsound.data.models.CarritoItem
import kotlinx.coroutines.flow.Flow

@Dao
interface CarritoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: CarritoItem)

    @Query("SELECT * FROM carrito")
    fun getAllItems(): Flow<List<CarritoItem>>

    @Query("SELECT * FROM carrito")
    suspend fun getAllItemsList(): List<CarritoItem>

    @Query("SELECT * FROM carrito WHERE beatId = :beatId LIMIT 1")
    suspend fun getItemByBeatId(beatId: Int): CarritoItem?

    @Update
    suspend fun updateItem(item: CarritoItem)

    @Delete
    suspend fun deleteItem(item: CarritoItem)

    @Query("DELETE FROM carrito WHERE id = :itemId")
    suspend fun deleteItemById(itemId: Int)

    @Query("DELETE FROM carrito")
    suspend fun deleteAllItems()

    @Query("SELECT SUM(precio * cantidad) FROM carrito")
    suspend fun getTotalPrice(): Double?
}

