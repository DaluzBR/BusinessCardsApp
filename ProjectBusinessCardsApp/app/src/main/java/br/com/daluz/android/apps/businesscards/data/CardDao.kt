package br.com.daluz.android.apps.businesscards.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {

    @Query("SELECT * FROM $TABLE_NAME")
    fun getAllCards(): Flow<List<Card>>

    @Query("SELECT * FROM $TABLE_NAME WHERE id = :id")
    fun getCard(id: Int): Flow<Card>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(card: Card)

    @Update
    suspend fun update(card: Card)

    @Delete
    suspend fun delete(card: Card)

}