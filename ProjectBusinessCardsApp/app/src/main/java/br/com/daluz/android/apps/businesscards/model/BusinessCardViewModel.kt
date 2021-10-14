package br.com.daluz.android.apps.businesscards.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import br.com.daluz.android.apps.businesscards.data.Card
import br.com.daluz.android.apps.businesscards.data.CardDao
import kotlinx.coroutines.launch

class BusinessCardViewModel(
    private val cardDao: CardDao
) : ViewModel() {

    val allCards: LiveData<List<Card>> = getCards()

    private fun getCards(): LiveData<List<Card>> {
        return cardDao.getAllCards().asLiveData()
    }

    fun getCardById(id: Int): LiveData<Card> {
        return cardDao.getCard(id).asLiveData()
    }

    fun insertCard(card: Card) {
        viewModelScope.launch {
            cardDao.insert(card)
        }
    }

    fun updateCard(card: Card) {
        viewModelScope.launch {
            cardDao.update(card)
        }
    }

    fun deleteCard(card: Card) {
        viewModelScope.launch {
            cardDao.delete(card)
        }
    }

    fun isEntryValid(name: String, phone: String, email: String, company: String): Boolean {
        if (name.isBlank() || phone.isBlank() || email.isBlank() || company.isBlank()) {
            return false
        }
        return true
    }
}