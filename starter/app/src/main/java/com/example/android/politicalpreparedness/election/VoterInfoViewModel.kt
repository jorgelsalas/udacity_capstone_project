package com.example.android.politicalpreparedness.election

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception



class VoterInfoViewModel(private val dataSource: ElectionDao) : ViewModel() {

    private val _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo: LiveData<VoterInfoResponse>
        get() = _voterInfo

    private val _loadingVoterInfo = MutableLiveData<Boolean>()
    val loadingVoterInfo: LiveData<Boolean>
        get() = _loadingVoterInfo


    private val _navigateToUrl = MutableLiveData<Uri>()
    val navigateToUrl: LiveData<Uri>
        get() = _navigateToUrl

    private val _toggleButtonEnabled = MutableLiveData<Boolean>()
    val toggleButtonEnabled: LiveData<Boolean>
        get() = _toggleButtonEnabled

    private val _electionCurrentlySaved = MutableLiveData<Boolean>()
    val electionCurrentlySaved: LiveData<Boolean>
        get() = _electionCurrentlySaved

    init {
        _electionCurrentlySaved.value = false
        _toggleButtonEnabled.value = false
    }

    fun fetchVoterInfo(electionId: Long, division: Division) {
        viewModelScope.launch {
            _loadingVoterInfo.value = true
            try {
                _voterInfo.value = CivicsApi.retrofitService.getVoterInfo(getAddress(division), electionId)
                _loadingVoterInfo.value = false
                updateSaveButton(electionId)
            }
            catch (e: Exception) {
                Log.e(TAG, "Unable to fetch voter info: $e")
                _loadingVoterInfo.value = false
            }
        }
    }

    private fun getAddress(division: Division) : String {
        return "${division.state}, ${division.country}"
    }

    private fun updateSaveButton(electionId: Long) {
        viewModelScope.launch {
            val storedElection = dataSource.getElection(electionId.toInt())
            storedElection?.let {
                _electionCurrentlySaved.value = true
            }
            _toggleButtonEnabled.value = true
        }
    }

    fun onToggleButtonClicked() {
        _toggleButtonEnabled.value = false

        if(_electionCurrentlySaved.value!!) {
            deleteElection()
            _electionCurrentlySaved.value = false
        }
        else {
            saveElection()
            _electionCurrentlySaved.value = true
        }

        _toggleButtonEnabled.value = true
    }

    private fun saveElection() {
        viewModelScope.launch {
            if (voterInfo.value != null) {
                _voterInfo.value?.let {
                    dataSource.insert(it.election)
                }
            }
            else {
                Log.w(TAG, "Unable to insert election: Voter info not available")
            }
        }
    }

    private fun deleteElection() {
        viewModelScope.launch {
            if (voterInfo.value != null) {
                _voterInfo.value?.let {
                    dataSource.delete(it.election)
                }
            }
            else {
                Log.w(TAG, "Unable to delete election: Voter info not available")
            }
        }
    }

    fun onUrlClicked(url: String) {
        _navigateToUrl.value = Uri.parse(url)
    }

    fun onUrlNavigated() {
        _navigateToUrl.value = null
    }

    companion object {
        private val TAG = VoterInfoViewModel::class.java.simpleName
    }

}