package com.example.android.politicalpreparedness.election

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.ElectionResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class ElectionsViewModel(private val dataSource: ElectionDao): ViewModel() {

    private val _upcomingElections = MutableLiveData<List<Election>>()
    val upcomingElections: LiveData<List<Election>>
        get() = _upcomingElections

    private val _savedElections = MutableLiveData<List<Election>>()
    val savedElections: LiveData<List<Election>>
        get() = _savedElections

    private val _loadingUpcomingElections = MutableLiveData<Boolean>()
    val loadingUpcomingElections: LiveData<Boolean>
        get() = _loadingUpcomingElections

    private val _loadingSavedElections = MutableLiveData<Boolean>()
    val loadingSavedElections: LiveData<Boolean>
        get() = _loadingSavedElections

    private val _navigateToElectionInformationScreen = MutableLiveData<Election>()
    val navigateToElectionInformationScreen: LiveData<Election>
        get() = _navigateToElectionInformationScreen

    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database
    fun fetchElections() {
        fetchUpcomingElections()
        fetchSavedElections()
    }

    private fun fetchUpcomingElections() {
        viewModelScope.launch {
            _loadingUpcomingElections.value = true
            try {
                val electionResponse = CivicsApi.retrofitService.getElections()
                _upcomingElections.value = electionResponse.elections
            }
            catch (e: Exception) {
                Log.e(ElectionsViewModel::class.java.simpleName, "Unable to fetch elections: $e")
            }
            _loadingUpcomingElections.value = false
        }
    }

    private fun fetchSavedElections() {
        viewModelScope.launch {
            _loadingSavedElections.value = true
            _savedElections.value = dataSource.getAllElections()
            _loadingSavedElections.value = false
        }
    }

    fun onElectionClicked(election: Election) {
        _navigateToElectionInformationScreen.value = election
    }

    fun onElectionInformationScreenNavigated() {
        _navigateToElectionInformationScreen.value = null
    }
}