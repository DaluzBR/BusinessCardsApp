package br.com.daluz.android.apps.businesscards.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.daluz.android.apps.businesscards.data.Card
import br.com.daluz.android.apps.businesscards.databinding.AdapterItemBusinessCardBinding

class BusinessCardItemListAdapter(
    private var onCardClicked: (Int) -> Unit
) : ListAdapter<Card, BusinessCardItemListAdapter.ViewHolder>(DiffCallback()) {

    var onCardShare: (View) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdapterItemBusinessCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentCard = getItem(position)
        holder.itemView.setOnClickListener {
            onCardClicked(currentCard.id)
        }
        holder.bind(currentCard)
    }

    inner class ViewHolder(
        private val binding: AdapterItemBusinessCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(card: Card) {
            binding.apply {
                txvName.text = card.name
                txvPhone.text = card.phone
                txvEmail.text = card.email
                txvCompany.text = card.company
                cdvContent.setCardBackgroundColor(Color.parseColor(card.backgroundColor))
                imvShare.setOnClickListener {
                    onCardShare(cdvContent)
                }
            }
        }
    }
}

class DiffCallback : DiffUtil.ItemCallback<Card>() {
    override fun areItemsTheSame(oldItem: Card, newItem: Card) =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: Card, newItem: Card) =
        oldItem.id == newItem.id
}