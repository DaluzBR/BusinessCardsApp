package br.com.daluz.android.apps.businesscards.ui

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.daluz.android.apps.businesscards.BusinessCardApplication
import br.com.daluz.android.apps.businesscards.R
import br.com.daluz.android.apps.businesscards.data.Card
import br.com.daluz.android.apps.businesscards.databinding.FragmentBusinessCardCreateBinding
import br.com.daluz.android.apps.businesscards.model.BusinessCardViewModel
import br.com.daluz.android.apps.businesscards.model.BusinessCardViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class BusinessCardCreateFragment : Fragment() {

    private var _binding: FragmentBusinessCardCreateBinding? = null
    private val binding: FragmentBusinessCardCreateBinding
        get() = _binding!!

    private val viewModel: BusinessCardViewModel by activityViewModels {
        BusinessCardViewModelFactory(
            (activity?.application as BusinessCardApplication).database.CardDao()
        )
    }

    private val navigationArgs: BusinessCardCreateFragmentArgs by navArgs()
    private lateinit var card: Card

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBusinessCardCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cardId = navigationArgs.cardId
        if (cardId > 0) {
            viewModel.getCardById(cardId).observe(this.viewLifecycleOwner) { currentCard ->
                card = currentCard
                bind(card)
            }
        } else {
            binding.btnSave.setOnClickListener { insertCard() }
        }

        binding.btnCancel.setOnClickListener { backToMainScreen() }
    }

    private fun bind(card: Card) {
        binding.apply {
            tieName.setText(card.name)
            tiePhone.setText(card.phone)
            tieEmail.setText(card.email)
            tieCompany.setText(card.company)
            tieBackgroundColor.setText(card.backgroundColor)
            btnSave.text = getString(R.string.label_update)
            btnSave.setOnClickListener { updateCard() }
        }
    }

    private fun insertCard() {
        val businessCard = checkEmptyFields()

        if (businessCard == null) {
            emptyFieldMessage()
            return
        }

        viewModel.insertCard(businessCard)
        Toast.makeText(
            requireActivity(),
            getString(R.string.label_show_success_insert),
            Toast.LENGTH_SHORT
        ).show()

        backToMainScreen()
    }

    private fun updateCard() {
        val businessCard = checkEmptyFields()

        if (businessCard == null) {
            emptyFieldMessage()
            return
        }

        val businessCardUpdate = businessCard.copy(id = card.id)

        viewModel.updateCard(businessCardUpdate)

        Log.d("DEBUG10","Name: ${businessCard.name}")
        Toast.makeText(
            requireActivity(),
            getString(R.string.label_show_success_update),
            Toast.LENGTH_SHORT
        ).show()

        val action = BusinessCardCreateFragmentDirections
            .actionBusinessCardCreateFragmentToBusinessCardListFragment()

        findNavController().navigate(action)
    }

    private fun checkEmptyFields(): Card? {
        val name: String = binding.tilName.editText?.text.toString()
        val phone: String = binding.tilPhone.editText?.text.toString()
        val email: String = binding.tilEmail.editText?.text.toString()
        val company: String = binding.tilCompany.editText?.text.toString()
        var backgroundColor: String = binding.tilBackgroundColor.editText?.text.toString()
        if (backgroundColor.isBlank()) {
            backgroundColor = "#FFFFFFFF"
        }
        if (viewModel.isEntryValid(name, phone, email, company)) {
            return Card(
                name = name,
                phone = phone,
                email = email,
                company = company,
                backgroundColor = backgroundColor
            )
        }
        return null
    }

    private fun emptyFieldMessage() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.label_message_advice))
            .setMessage(getString(R.string.label_message_error_empty_field))
            .setPositiveButton(getString(R.string.label_ok)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


    private fun backToMainScreen() {
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Hide keyboard.
        val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }
}