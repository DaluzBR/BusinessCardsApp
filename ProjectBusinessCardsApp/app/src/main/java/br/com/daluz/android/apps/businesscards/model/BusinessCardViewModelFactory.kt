package br.com.daluz.android.apps.businesscards.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.daluz.android.apps.businesscards.data.CardDao

class BusinessCardViewModelFactory(
    private val cardDao: CardDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(BusinessCardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BusinessCardViewModel(cardDao) as T
        }
        throw IllegalArgumentException("Unknown viewModel class.")
    }
}