package com.example.android.politicalpreparedness.election

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.launch
import java.lang.Exception

class VoterInfoViewModel(private val dataSource: ElectionDao) : ViewModel() {

    //TODO: Add live data to hold voter info
    private val _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo: LiveData<VoterInfoResponse>
        get() = _voterInfo

    //TODO: Add var and methods to populate voter info
    private val _loadingVoterInfo = MutableLiveData<Boolean>()
    val loadingVoterInfo: LiveData<Boolean>
        get() = _loadingVoterInfo

    //TODO: Add var and methods to support loading URLs

    //TODO: Add var and methods to save and remove elections to local database
    //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

    fun fetchVoterInfo(electionId: Long, division: Division) {
        viewModelScope.launch {
            try {
                _loadingVoterInfo.value = true
                _voterInfo.value = CivicsApi.retrofitService.getVoterInfo(getAddress(division), electionId)
                _loadingVoterInfo.value = false
            }
            catch (e: Exception) {
                Log.e(VoterInfoViewModel::class.java.simpleName, "Unable to fetch voter info: $e")
            }
        }
    }

    private fun getAddress(division: Division) : String {
        return "${division.state}, ${division.country}"
    }

}