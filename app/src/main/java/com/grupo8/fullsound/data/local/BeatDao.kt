package com.grupo8.fullsound.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.grupo8.fullsound.data.models.Beat

@Dao
interface BeatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBeat(beat: Beat)

    @Query("SELECT * FROM beats WHERE id = :beatId")
    suspend fun getBeatById(beatId: String): Beat?

    @Query("SELECT * FROM beats")
    suspend fun getAllBeats(): List<Beat>
}