package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
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

    //TODO: Declare ViewModel
    private lateinit var electionsViewModel: ElectionsViewModel

    private lateinit var binding: FragmentElectionBinding

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //TODO: Add ViewModel values and create ViewModel
        initViewModel()

        //TODO: Add binding values
        binding = FragmentElectionBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewModel = electionsViewModel

        //TODO: Link elections to voter info
        val electionClickListener = ElectionListener { election -> electionsViewModel.onElectionClicked(election) }
        electionsViewModel.navigateToElectionInformationScreen.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController().navigate(actionElectionsFragmentToVoterInfoFragment(it.id, it.division))
                electionsViewModel.onElectionInformationScreenNavigated()
            }
        })

        //TODO: Initiate recycler adapters
        val upcomingAdapter = ElectionListAdapter(electionClickListener)
        val savedAdapter = ElectionListAdapter(electionClickListener)
        binding.upcomingElectionsRecycler.adapter = upcomingAdapter
        binding.savedElectionsRecycler.adapter = savedAdapter

        //TODO: Populate recycler adapters
        electionsViewModel.upcomingElections.observe(viewLifecycleOwner, Observer { elections ->
            elections?.let { upcomingAdapter.submitList(elections) }
        })

        electionsViewModel.savedElections.observe(viewLifecycleOwner, Observer { elections ->
            elections?.let { savedAdapter.submitList(elections) }
        })

        return binding.root
    }

    private fun initViewModel() {
        val viewModelFactory = ElectionsViewModelFactory(getInstance(requireContext().applicationContext).electionDao)
        electionsViewModel = ViewModelProvider(this, viewModelFactory).get(ElectionsViewModel::class.java)
    }

    //TODO: Refresh adapters when fragment loads
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        electionsViewModel.fetchElections()
    }
}