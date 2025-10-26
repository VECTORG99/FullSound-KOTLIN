package com.grupo8.fullsound.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.grupo8.fullsound.data.models.Beat

@Dao
interface BeatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBeat(beat: Beat)

    @Query("SELECT * FROM beats WHERE id = :beatId")
    suspend fun getBeatById(beatId: String): Beat?

    @Query("SELECT * FROM beats")
    suspend fun getAllBeats(): List<Beat>

    @Update
    suspend fun updateBeat(beat: Beat)

    @Delete
    suspend fun deleteBeat(beat: Beat)

    @Query("DELETE FROM beats WHERE id = :beatId")
    suspend fun deleteBeatById(beatId: String)

    @Query("DELETE FROM beats")
    suspend fun deleteAllBeats()
}