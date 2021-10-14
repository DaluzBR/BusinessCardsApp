package br.com.daluz.android.apps.businesscards.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.daluz.android.apps.businesscards.BusinessCardApplication
import br.com.daluz.android.apps.businesscards.R
import br.com.daluz.android.apps.businesscards.adapter.BusinessCardItemListAdapter
import br.com.daluz.android.apps.businesscards.databinding.FragmentBusinessCardListBinding
import br.com.daluz.android.apps.businesscards.model.BusinessCardViewModel
import br.com.daluz.android.apps.businesscards.model.BusinessCardViewModelFactory
import br.com.daluz.android.apps.businesscards.util.Image


class BusinessCardListFragment : Fragment() {

    companion object {
        private const val PERMISSION_CODE_RW_EXTERNAL_STORAGE = 1000
    }

    private var _binding: FragmentBusinessCardListBinding? = null
    private val binding: FragmentBusinessCardListBinding
        get() = _binding!!

    private val viewModel: BusinessCardViewModel by activityViewModels {
        BusinessCardViewModelFactory(
            (activity?.application as BusinessCardApplication).database.CardDao()
        )
    }

    private lateinit var mViewCard: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBusinessCardListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = BusinessCardItemListAdapter { id ->
            val action = BusinessCardListFragmentDirections
                .actionBusinessCardListFragmentToBusinessCardDetailsFragment(id)
            this.findNavController().navigate(action)
        }

        binding.rcvList.adapter = adapter
        binding.rcvList.layoutManager = LinearLayoutManager(this.requireContext())

        viewModel.allCards.observe(this.viewLifecycleOwner) { cards ->
            cards?.let {
                adapter.submitList(it)
            }
        }

        binding.fabCreate.setOnClickListener {
            val action = BusinessCardListFragmentDirections
                .actionBusinessCardListFragmentToBusinessCardCreateFragment(
                    getString(R.string.label_card_create)
                )
            findNavController().navigate(action)
        }

        adapter.onCardShare = { viewCard ->
            mViewCard = viewCard
            checkPermissionGranted()
        }
    }

    private fun checkPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ActivityCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_DENIED ||
                ActivityCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_DENIED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    PERMISSION_CODE_RW_EXTERNAL_STORAGE
                )
            } else {
                businessCardShare(mViewCard)
            }

        } else {
            businessCardShare(mViewCard)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE_RW_EXTERNAL_STORAGE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) &&
                    (grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                ) {
                    businessCardShare(mViewCard)
                } else {
                    Toast.makeText(context, "Permissão negada!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun businessCardShare(view: View) {
        Image.share(requireContext(), view)
    }
}