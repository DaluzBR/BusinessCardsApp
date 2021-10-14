package br.com.daluz.android.apps.businesscards.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

const val TABLE_NAME = "Card"

@Entity(tableName = TABLE_NAME)
data class Card(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "card_name")
    val name: String,
    @ColumnInfo(name = "card_company")
    val company: String,
    @ColumnInfo(name = "card_phone")
    val phone: String,
    @ColumnInfo(name = "card_email")
    val email: String,
    @ColumnInfo(name = "card_bg_color")
    val backgroundColor: String = "#FFFFFFFF"
)