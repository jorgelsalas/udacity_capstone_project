package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View.INVISIBLE
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.android.politicalpreparedness.database.ElectionDatabase.Companion.getInstance
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.network.models.State
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse

class VoterInfoFragment : Fragment() {

    private lateinit var voterInfoViewModel: VoterInfoViewModel

    private lateinit var binding : FragmentVoterInfoBinding

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //TODO: Add ViewModel values and create ViewModel
        initViewModel()

        //TODO: Add binding values
        binding = FragmentVoterInfoBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = voterInfoViewModel

        //TODO: Populate voter info -- hide views without provided data.
        /**
        Hint: You will need to ensure proper data is provided from previous fragment.
        */
        val args: VoterInfoFragmentArgs by navArgs()
        voterInfoViewModel.fetchVoterInfo(args.argElectionId.toLong(), args.argDivision)
        voterInfoViewModel.voterInfo.observe(viewLifecycleOwner, Observer {
            updateVoterInfoViews(it)
        })

        //TODO: Handle loading of URLs

        //TODO: Handle save button UI state
        //TODO: cont'd Handle save button clicks
        return binding.root
    }

    private fun initViewModel() {
        val viewModelFactory = VoterInfoViewModelFactory(getInstance(requireContext().applicationContext).electionDao)
        voterInfoViewModel = ViewModelProvider(this, viewModelFactory).get(VoterInfoViewModel::class.java)
    }

    private fun updateVoterInfoViews(voterInfoResponse: VoterInfoResponse) {
        if (voterInfoResponse.state == null || voterInfoResponse.state.isEmpty()) {
            hideAllStateInfoViews()
        }
        else {
            hideViewsBasedOnDataAvailability(voterInfoResponse.state[0])
        }
    }

    private fun hideAllStateInfoViews() {
        with(binding) {
            stateHeader.visibility = INVISIBLE
            stateLocations.visibility = INVISIBLE
            stateBallot.visibility = INVISIBLE
            addressGroup.visibility = INVISIBLE
        }
    }

    private fun hideViewsBasedOnDataAvailability(state: State) {
        with(state) {
            if (electionAdministrationBody.ballotInfoUrl == null) {
                binding.stateBallot.visibility = INVISIBLE
            }

            if (electionAdministrationBody.votingLocationFinderUrl == null) {
                binding.stateLocations.visibility = INVISIBLE
            }

            if (electionAdministrationBody.correspondenceAddress == null) {
                binding.addressGroup.visibility = INVISIBLE
            }
        }
    }

    //TODO: Create method to load URL intents

}