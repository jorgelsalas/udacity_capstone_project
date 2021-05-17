package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.database.ElectionDatabase.Companion.getInstance
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener

class ElectionsFragment: Fragment() {

    private lateinit var electionsViewModel: ElectionsViewModel

    private lateinit var binding: FragmentElectionBinding

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        initViewModel()

        binding = FragmentElectionBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewModel = electionsViewModel

        val electionClickListener = ElectionListener { election -> electionsViewModel.onElectionClicked(election) }
        electionsViewModel.navigateToElectionInformationScreen.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController().navigate(actionElectionsFragmentToVoterInfoFragment(it.id, it.division))
                electionsViewModel.onElectionInformationScreenNavigated()
            }
        })

        val upcomingAdapter = ElectionListAdapter(electionClickListener)
        val savedAdapter = ElectionListAdapter(electionClickListener)
        binding.upcomingElectionsRecycler.adapter = upcomingAdapter
        binding.savedElectionsRecycler.adapter = savedAdapter

        electionsViewModel.upcomingElections.observe(viewLifecycleOwner, Observer { elections ->
            elections?.let { upcomingAdapter.submitList(elections) }
        })

        electionsViewModel.savedElections.observe(viewLifecycleOwner, Observer { elections ->
            elections?.let {
                savedAdapter.submitList(elections)

                if (elections.isEmpty()) {
                    binding.savedElectionsRecycler.visibility = GONE
                    binding.emptySavedElectionsMessage.visibility = VISIBLE
                }
                else {
                    binding.savedElectionsRecycler.visibility = VISIBLE
                    binding.emptySavedElectionsMessage.visibility = GONE
                }
            }
        })

        return binding.root
    }

    private fun initViewModel() {
        val viewModelFactory = ElectionsViewModelFactory(getInstance(requireContext().applicationContext).electionDao)
        electionsViewModel = ViewModelProvider(this, viewModelFactory).get(ElectionsViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        electionsViewModel.fetchElections()
    }
}