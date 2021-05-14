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

    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>>
        get() = _representatives

    private val _loadingRepresentatives = MutableLiveData<Boolean>()
    val loadingRepresentatives: LiveData<Boolean>
        get() = _loadingRepresentatives

    private val _address = MutableLiveData<Address>()
    val address: LiveData<Address>
        get() = _address

    fun fetchRepresentatives(address: Address) {
        viewModelScope.launch {
            _loadingRepresentatives.value = true

            try {
                val (offices, officials) = CivicsApi.retrofitService.getRepresentatives(getAddressForApiRequest(address))
                _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }
            }
            catch (e: Exception) {
                Log.e(RepresentativeViewModel::class.java.simpleName, "Unable to fetch representatives: $e")
            }

            _loadingRepresentatives.value = false
        }
    }

    //TODO: Create function get address from geo location

    fun getAddressFromFields(line1: String, line2: String?, city: String, state: String, zip: String): Address? {
        val newAddress = Address(line1, line2, city, state, zip)

        return if (isValid(newAddress)) {
            newAddress
        } else {
            null
        }
    }

    private fun isValid(address: Address): Boolean {
        return address.line1.isNotEmpty() && address.city.isNotEmpty()
                && address.state.isNotEmpty() && address.zip.isNotEmpty()
    }

    private fun getAddressForApiRequest(address: Address): String {
        with(address) {
            //return "$line1, $line2, $city, $state, $zip"
            return "$state, $zip"
        }
    }

    fun updateAddress(address: Address) {
        _address.value = address
    }
}
