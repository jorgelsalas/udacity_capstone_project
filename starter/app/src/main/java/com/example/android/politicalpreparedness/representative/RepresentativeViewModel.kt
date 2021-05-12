package com.example.android.politicalpreparedness.representative

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch

class RepresentativeViewModel: ViewModel() {

    //TODO: Establish live data for representatives and address

    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>>
        get() = _representatives

    private val _loadingRepresentatives = MutableLiveData<Boolean>()
    val loadingRepresentatives: LiveData<Boolean>
        get() = _loadingRepresentatives

    //TODO: Create function to fetch representatives from API from a provided address
    fun fetchRepresentatives(address: Address) {
        viewModelScope.launch {
            _loadingRepresentatives.value = true

            try {
                val (offices, officials) = CivicsApi.retrofitService.getRepresentatives("${address.state}, ${address.zip}")
                _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }
            }
            catch (e: Exception) {
                Log.e(RepresentativeViewModel::class.java.simpleName, "Unable to fetch representatives: $e")
            }

            _loadingRepresentatives.value = false
        }
    }

    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

    val (offices, officials) = getRepresentativesDeferred.await()
    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

    Note: getRepresentatives in the above code represents the method used to fetch data from the API
    Note: _representatives in the above code represents the established mutable live data housing representatives

     */

    //TODO: Create function get address from geo location

    //TODO: Create function to get address from individual fields

}
