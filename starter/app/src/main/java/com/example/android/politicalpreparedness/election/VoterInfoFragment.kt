package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.database.ElectionDatabase.Companion.getInstance
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding

class VoterInfoFragment : Fragment() {

    private lateinit var voterInfoViewModel: VoterInfoViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //TODO: Add ViewModel values and create ViewModel
        initViewModel()

        //TODO: Add binding values
        val binding = FragmentVoterInfoBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = voterInfoViewModel

        //TODO: Populate voter info -- hide views without provided data.
        /**
        Hint: You will need to ensure proper data is provided from previous fragment.
        */


        //TODO: Handle loading of URLs

        //TODO: Handle save button UI state
        //TODO: cont'd Handle save button clicks
        return binding.root
    }

    private fun initViewModel() {
        val viewModelFactory = VoterInfoViewModelFactory(getInstance(requireContext().applicationContext).electionDao)
        voterInfoViewModel = ViewModelProvider(this, viewModelFactory).get(VoterInfoViewModel::class.java)
    }

    //TODO: Create method to load URL intents

}