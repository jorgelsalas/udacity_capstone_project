package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.models.Election

class ElectionsViewModel(private val dataSource: ElectionDao): ViewModel() {

    private val _upcomingElections = MutableLiveData<List<Election>>()
    val upcomingElections: LiveData<List<Election>>
        get() = _upcomingElections

    private val _savedElections = MutableLiveData<List<Election>>()
    val savedElections: LiveData<List<Election>>
        get() = _savedElections

    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database
    fun fetchElections() {
        fetchUpcomingElections()
        fetchSavedElections()
    }

    private fun fetchSavedElections() {

    }

    private fun fetchUpcomingElections() {

    }

    //TODO: Create functions to navigate to saved or upcoming election voter info
    fun onElectionClicked(election: Election) {

    }
}