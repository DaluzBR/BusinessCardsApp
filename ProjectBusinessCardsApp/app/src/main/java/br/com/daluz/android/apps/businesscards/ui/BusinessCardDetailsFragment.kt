package br.com.daluz.android.apps.businesscards.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.daluz.android.apps.businesscards.BusinessCardApplication
import br.com.daluz.android.apps.businesscards.R
import br.com.daluz.android.apps.businesscards.data.Card
import br.com.daluz.android.apps.businesscards.databinding.FragmentBusinessCardDetailsBinding
import br.com.daluz.android.apps.businesscards.model.BusinessCardViewModel
import br.com.daluz.android.apps.businesscards.model.BusinessCardViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class BusinessCardDetailsFragment : Fragment() {
    private var _binding: FragmentBusinessCardDetailsBinding? = null
    private val binding: FragmentBusinessCardDetailsBinding
        get() = _binding!!

    private val navigationArgs: BusinessCardDetailsFragmentArgs by navArgs()
    private lateinit var card: Card

    private val viewModel: BusinessCardViewModel by activityViewModels {
        BusinessCardViewModelFactory(
            (activity?.application as BusinessCardApplication).database.CardDao()
        )
    }


    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        hasOptionsMenu()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBusinessCardDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cardId = navigationArgs.cardId
        viewModel.getCardById(cardId).observe(this.viewLifecycleOwner) { currentCard ->
            card = currentCard
            bind(card)
        }

        binding.btnDelete.setOnClickListener { deleteCardMessage() }
        binding.btnEdit.setOnClickListener { editCard() }
    }

    private fun bind(card: Card) {
        binding.apply {
            txvNameInfo.text = card.name
            txvPhoneInfo.text = card.phone
            txvEmailInfo.text = card.email
            txvCompanyInfo.text = card.company
            txvBackgroundColorInfo.text = card.backgroundColor
        }
    }

    private fun editCard() {
        val action = BusinessCardDetailsFragmentDirections
            .actionBusinessCardDetailsFragmentToBusinessCardCreateFragment(
                getString(R.string.label_card_edit),
                card.id
            )
        this.findNavController().navigate(action)
    }

    private fun deleteCard() {
        viewModel.deleteCard(card)
        Toast.makeText(context, getString(R.string.label_message_card_deleted), Toast.LENGTH_SHORT)
            .show()
        this.findNavController().navigateUp()
    }

    private fun deleteCardMessage() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.label_message_advice))
            .setMessage(getString(R.string.label_message_card_delete))
            .setPositiveButton(getString(R.string.label_ok)) { _, _ ->
                deleteCard()
            }
            .setNegativeButton(getString(R.string.label_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_business_card_share, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.menu.menu_business_card_share) {
            // TODO
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}