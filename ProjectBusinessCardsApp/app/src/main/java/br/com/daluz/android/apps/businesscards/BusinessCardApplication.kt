package br.com.daluz.android.apps.businesscards

import android.app.Application
import br.com.daluz.android.apps.businesscards.data.CardRoomDatabase

class BusinessCardApplication : Application() {

    val database: CardRoomDatabase by lazy {
        CardRoomDatabase.getDatabase(this)
    }
}