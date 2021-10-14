package br.com.daluz.android.apps.businesscards.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Card::class], version = 1, exportSchema = false)
abstract class CardRoomDatabase : RoomDatabase() {

    abstract fun CardDao(): CardDao

    companion object {
        @Volatile
        private var INSTANCE: CardRoomDatabase? = null

        fun getDatabase(context: Context): CardRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CardRoomDatabase::class.java,
                    "Card_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
